package chainslake.sui.extract

import chainslake.job.TaskRun
import chainslake.sui.{ExtractedBlock, OriginBlock, ResponseBlock}
import com.google.gson.Gson
import org.apache.spark.sql.functions.{col, lit}
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.storage.StorageLevel

import java.util.Properties

object Blocks extends TaskRun {
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
    processTable(spark, chainName + ".blocks", properties)
  }


  override protected def onProcess(spark: SparkSession, outputTable: String, from: Long, to: Long, properties: Properties): Unit = {
    import spark.implicits._
    val inputTableName = properties.getProperty("list_input_tables")
    spark.read.table(inputTableName).select("block_date", "block_number", "block_time", "block").withColumn("transactions", lit(""))
      .where(col("block_number") >= from && col("block_number") <= to)
      .as[OriginBlock].map(block => {
        val gson = new Gson()
        val extractBlock = gson.fromJson(block.block, classOf[ResponseBlock]).result
        ExtractedBlock(block.block_date,
          block.block_number,
          block.block_time,
          extractBlock.digest,
          extractBlock.epoch.toLong,
          extractBlock.networkTotalTransactions.toLong,
          extractBlock.previousDigest,
          extractBlock.epochRollingGasCostSummary.computationCost.toLong,
          extractBlock.epochRollingGasCostSummary.storageCost.toLong,
          extractBlock.epochRollingGasCostSummary.storageRebate.toLong,
          extractBlock.epochRollingGasCostSummary.nonRefundableStorageFee.toLong,
          extractBlock.validatorSignature,
          extractBlock.transactions.length
        )
      })
      .persist(StorageLevel.MEMORY_AND_DISK)
      .repartitionByRange(col("block_date"), col("block_time"))
      .write.partitionBy("block_date")
      .mode(SaveMode.Append).format("delta")
      .saveAsTable(outputTable)
  }
}
