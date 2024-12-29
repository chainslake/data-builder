package chainslake.bitcoin.extract

import chainslake.bitcoin.{ExtractedInput, OriginBlock, ResponseBlock}
import chainslake.job.TaskRun
import com.google.gson.Gson
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.{SaveMode, SparkSession}

import java.util.Properties

object Inputs extends TaskRun {

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
    processTable(spark, chainName + ".inputs", properties)
  }

  override protected def onProcess(spark: SparkSession, outputTable: String, from: Long, to: Long, properties: Properties): Unit = {
    import spark.implicits._
    val inputTableName = properties.getProperty("list_input_tables")
    spark.read.table(inputTableName).where(col("block_number") >= from && col("block_number") <= to)
      .as[OriginBlock].flatMap(block => {
        val gson = new Gson()
        val extractBlock = gson.fromJson(block.block, classOf[ResponseBlock]).result
        extractBlock.tx.flatMap(transaction => {
          transaction.vin.map(vin => {
            ExtractedInput(block.block_date,
              block.block_number,
              block.block_time,
              transaction.txid,
              vin.txid,
              vin.coinbase,
              vin.vout,
              if (vin.scriptSig != null) vin.scriptSig.asm else null,
              if (vin.scriptSig != null) vin.scriptSig.hex else null,
              vin.sequence,
              vin.txinwitness
            )
          })
        })
      }).repartitionByRange(col("block_date"), col("block_time"))
      .write.partitionBy("block_date")
      .mode(SaveMode.Append).format("delta")
      .saveAsTable(outputTable)
  }
}
