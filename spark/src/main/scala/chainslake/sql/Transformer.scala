package chainslake.sql

import org.apache.spark.sql.functions.col
import org.apache.spark.sql.{SaveMode, SparkSession}

import java.io.{FileInputStream, StringReader}
import java.util.Properties
import ajr.scemplate._
import chainslake.job.TaskRun
import org.apache.spark.storage.StorageLevel

object Transformer extends TaskRun {
  override def run(spark: SparkSession, properties: Properties): Unit = {
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


    val outputTable = properties.getProperty("output_table")
    val database = outputTable.split("\\.")(0)
    try {
      spark.sql(s"create database if not exists $database")
    } catch {
      case e: Exception => e.getMessage
    }
    processTable(spark, outputTable, properties)
  }

  protected def onProcess(spark: SparkSession, outputTable: String, from: Long, to: Long, properties: Properties): Unit = {
    val sqlTemplateString = properties.getProperty("sql_template")
    val sqlTemplate = new Template(sqlTemplateString)
    val isExistedTable = spark.catalog.tableExists(outputTable)
    var context = Context()
    properties.stringPropertyNames().forEach(key => {
      context = context.withValues(key -> StringValue(properties.getProperty(key).replace(",", "','")))
    })
    context = context.withValues("from" -> IntValue(from.toInt))
    context = context.withValues("to" -> IntValue(to.toInt))
    context = context.withValues("table_existed" -> BooleanValue(isExistedTable))
    val sqlString = sqlTemplate.render(context)
    var sqlDf = spark.sql(sqlString)
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
    }
  }
}
