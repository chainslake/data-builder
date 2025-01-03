package chainslake.sui.extract

import chainslake.job.TaskRun
import chainslake.sui.{ExtractedObjectChange, OriginBlock, Transaction}
import com.google.gson.Gson
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions.col

import java.util.Properties

object ObjectChanges extends TaskRun {
  override def run(spark: SparkSession, properties: Properties): Unit = {
    val chainName = properties.getProperty("chain_name")
    properties.setProperty("frequent_type", "block")
    properties.setProperty("list_input_tables", chainName + "_origin.transaction_blocks")
    val database = chainName
    try {
      spark.sql(s"create database if not exists $database")
    } catch {
      case e: Exception => e.getMessage
    }
    processTable(spark, chainName + ".object_changes", properties)
  }

  override protected def onProcess(spark: SparkSession, outputTable: String, from: Long, to: Long, properties: Properties): Unit = {
    import spark.implicits._
    val inputTableName = properties.getProperty("list_input_tables")
    spark.read.table(inputTableName).where(col("block_number") >= from && col("block_number") <= to)
      .as[OriginBlock].flatMap(block => {
        val gson = new Gson()
        val extractTransactions = gson.fromJson(block.transactions, classOf[Array[Transaction]])
        extractTransactions.flatMap(transaction => {
          transaction.objectChanges.map(objectChange => {
            ExtractedObjectChange(
              block.block_date,
              block.block_number,
              block.block_time,
              transaction.digest,
              objectChange.digest,
              objectChange.`type`,
              objectChange.sender,
              objectChange.owner.AddressOwner,
              objectChange.objectType,
              objectChange.objectId,
              objectChange.version.toLong,
              try {
                objectChange.previousVersion.toLong
              } catch {
                case e: Exception => 0L
              }
            )
          })
        })
      }).repartitionByRange(col("block_date"), col("block_time"))
      .write.partitionBy("block_date")
      .mode(SaveMode.Append).format("delta")
      .saveAsTable(outputTable)
  }
}
