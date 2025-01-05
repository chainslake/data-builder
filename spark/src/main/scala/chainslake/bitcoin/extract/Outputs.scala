package chainslake.bitcoin.extract

import chainslake.bitcoin.{ExtractedOutput, OriginBlock, ResponseBlock}
import chainslake.job.TaskRun
import com.google.gson.Gson
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions.col
import org.apache.spark.storage.StorageLevel

import java.util.Properties

object Outputs extends TaskRun {

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
    processTable(spark, chainName + ".outputs", properties)
  }

  override protected def onProcess(spark: SparkSession, outputTable: String, from: Long, to: Long, properties: Properties): Unit = {
    import spark.implicits._
    val inputTableName = properties.getProperty("list_input_tables")
    spark.read.table(inputTableName).where(col("block_number") >= from && col("block_number") <= to)
      .as[OriginBlock].flatMap(block => {
        val gson = new Gson()
        val extractBlock = gson.fromJson(block.block, classOf[ResponseBlock]).result
        extractBlock.tx.flatMap(transaction => {
          transaction.vout.map(vout => {
            ExtractedOutput(block.block_date,
              block.block_number,
              block.block_time,
              transaction.txid,
              vout.n,
              vout.value,
              vout.scriptPubKey.asm,
              vout.scriptPubKey.hex,
              vout.scriptPubKey.reqSigs,
              vout.scriptPubKey.`type`,
              vout.scriptPubKey.addresses,
              vout.scriptPubKey.address
            )
          })
        })
      })
      .persist(StorageLevel.MEMORY_AND_DISK)
      .repartitionByRange(col("block_date"), col("block_time"))
      .write.partitionBy("block_date")
      .mode(SaveMode.Append).format("delta")
      .saveAsTable(outputTable)
  }
}
