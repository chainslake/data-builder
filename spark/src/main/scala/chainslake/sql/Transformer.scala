package chainslake.sql

import org.apache.spark.sql.functions.col
import org.apache.spark.sql.{DataFrame, SparkSession}

import java.io.{FileInputStream, StringReader}
import java.util.Properties
import ajr.scemplate._
import chainslake.job.TaskRun
import chainslake.libs.Utils
import org.apache.spark.storage.StorageLevel
import org.apache.spark.sql.functions.udf

object Transformer extends TaskRun {
  override def run(spark: SparkSession, properties: Properties): Unit = {

    prepareProperties(properties)

    val outputTable = properties.getProperty("output_table")
    val database = outputTable.split("\\.")(0)
    try {
      spark.sql(s"create database if not exists $database")
    } catch {
      case e: Exception => e.getMessage
    }
    processTable(spark, outputTable, properties)
  }

  def prepareProperties(properties: Properties) = {
    val externalFile = new FileInputStream(properties.getProperty("chainslake_home_dir") + "/sql/" + properties.getProperty("sql_file"))
    properties.setProperty("external_file", scala.io.Source.fromInputStream(externalFile).mkString)

    val sqlFile = properties.getProperty("external_file").split("===")
    val sqlPropertiesRaw = sqlFile(0)
    var context = Context()
    properties.stringPropertyNames().forEach(key => {
      context = context.withValues(key -> StringValue(properties.getProperty(key)))
    })
    val template = new Template(sqlPropertiesRaw)

    val sqlTemplate = sqlFile(1)

    properties.load(new StringReader(template.render(context)))
    properties.setProperty("sql_template", sqlTemplate)
  }

  protected def onProcess(spark: SparkSession, outputTable: String, from: Long, to: Long, properties: Properties): Unit = {
    val isExistedTable = spark.catalog.tableExists(outputTable)
    var useVersion = false
    var currentVersion = 0
    var nextVersion = 1
    if (properties.containsKey("partition_by")) {
      if (properties.getProperty("partition_by") == "version") {
        useVersion = true
        if (isExistedTable) {
          currentVersion = spark.sql(s"SHOW TBLPROPERTIES $outputTable ('version');").collect()(0).getAs[String]("value").toInt
          if (!spark.sql(s"select version from $outputTable where version != $currentVersion limit 1").isEmpty) {
            spark.sql(s"delete from $outputTable where version != $currentVersion")
          }
          nextVersion = currentVersion + 1
        }
      }
    }

    properties.setProperty("is_existed_table", isExistedTable.toString)
    properties.setProperty("use_version", useVersion.toString)
    properties.setProperty("current_version", currentVersion.toString)
    properties.setProperty("next_version", nextVersion.toString)

    var sqlDf = transform(spark, from, to, properties)
    if (properties.containsKey("re_partition_by_range")) {
      val rangeColumns = properties.getProperty("re_partition_by_range").split(",").map(column => {
        col(column)
      })
      sqlDf = sqlDf.persist(StorageLevel.MEMORY_AND_DISK)
        .repartitionByRange(rangeColumns:_*)
    }

    var sqlWriter = sqlDf.write.format("delta")

    if (properties.containsKey("write_mode")) {
      sqlWriter = sqlWriter.mode(properties.getProperty("write_mode"))
    }

    if (properties.containsKey("partition_by")) {
      if (properties.containsKey("merge_by") && isExistedTable) {

      } else {
        val partitionColumns = properties.getProperty("partition_by").split(",")
        sqlWriter = sqlWriter.partitionBy(partitionColumns: _*)
      }
    }
    if (properties.containsKey("merge_by")) {
      if (isExistedTable) {
        val tmpTable = outputTable + "__tmp"
        sqlWriter.saveAsTable(tmpTable)
        val mergeSQL = s"MERGE INTO $outputTable as dest USING $tmpTable as src ON " +
          properties.getProperty("merge_by").split(",").map(column => s"dest.$column = src.$column").mkString(" and ") +
          """
            |WHEN MATCHED THEN
            |  UPDATE SET *
            |WHEN NOT MATCHED THEN
            |  INSERT *
            |""".stripMargin
        spark.sql(mergeSQL)

        spark.sql(s"DROP TABLE IF EXISTS $tmpTable")
      } else {
        sqlWriter.saveAsTable(properties.getProperty("output_table"))
      }
    } else {
      sqlWriter.saveAsTable(properties.getProperty("output_table"))
      if (useVersion) {
        spark.sql(s"ALTER TABLE $outputTable SET TBLPROPERTIES (version=$nextVersion)")
        spark.sql(s"delete from $outputTable where version != $nextVersion")
      }
    }
  }

  def transform(spark: SparkSession, from: Long, to: Long, properties: Properties): DataFrame = {
    val sqlTemplateString = properties.getProperty("sql_template")
    val isExistedTable = properties.getProperty("is_existed_table").toBoolean
    val useVersion = properties.getProperty("use_version").toBoolean
    val currentVersion = properties.getProperty("current_version").toInt
    val nextVersion = properties.getProperty("next_version").toInt
    val rpcList = properties.getProperty("rpc_list")
    if (rpcList != null) {
      val evmGetNFTInfo = udf((contractAddress: String, tokenId: String) => {
        Utils.evmGetNFTInfo(rpcList, contractAddress, tokenId)
      })
      spark.udf.register("evmGetNFTInfo", evmGetNFTInfo)
    }
    val sqlTemplate = new Template(sqlTemplateString)
    var context = Context()
    properties.stringPropertyNames().forEach(key => {
      context = context.withValues(key -> StringValue(properties.getProperty(key).replace(",", "','")))
    })
    context = context.withValues("from" -> IntValue(from.toInt))
    context = context.withValues("to" -> IntValue(to.toInt))
    context = context.withValues("table_existed" -> BooleanValue(isExistedTable))
    if (useVersion) {
      context = context.withValues("current_version" -> IntValue(currentVersion))
      context = context.withValues("next_version" -> IntValue(nextVersion))
    }
    val sqlString = sqlTemplate.render(context)
    spark.sql(sqlString)
  }
}
