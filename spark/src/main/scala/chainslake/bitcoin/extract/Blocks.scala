package chainslake.bitcoin.extract

import chainslake.bitcoin.origin.TransactionBlocks
import chainslake.bitcoin.{ExtractedBlock, OriginBlock, ResponseBlock, TransactionBlock}
import chainslake.job.TaskRun
import com.google.gson.Gson
import org.apache.spark.sql.{Dataset, SaveMode, SparkSession}
import org.apache.spark.sql.functions.col
import org.apache.spark.storage.StorageLevel

import java.sql.Timestamp
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
    transform(spark, from, to, properties)
      .persist(StorageLevel.MEMORY_AND_DISK)
      .repartitionByRange(col("block_date"), col("block_time"))
      .write.partitionBy("block_date")
      .mode(SaveMode.Append).format("delta")
      .saveAsTable(outputTable)
  }

  def transform(spark: SparkSession, from: Long, to: Long, properties: Properties): Dataset[ExtractedBlock] = {
    import spark.implicits._
    val inputTableName = properties.getProperty("list_input_tables")
    spark.read.table(inputTableName).where(col("block_number") >= from && col("block_number") <= to)
      .as[OriginBlock].map(block => {
        val gson = new Gson()
        var extractBlock: TransactionBlock = null
        try {
          extractBlock = gson.fromJson(block.block, classOf[ResponseBlock]).result
        } catch {
          case e: Exception => {
            println(s"Block number: ${block.block_number}")
            val rpcList = properties.getProperty("rpc_list").split(",")
            val result = TransactionBlocks.getOriginBlock(rpcList, block.block_number, 10)
            block.block = result._1
            extractBlock = gson.fromJson(block.block, classOf[ResponseBlock]).result
            //            throw e
          }
        }
        ExtractedBlock(block.block_date,
          block.block_number,
          block.block_time,
          extractBlock.hash,
          extractBlock.size,
          extractBlock.strippedsize,
          extractBlock.weight,
          extractBlock.version,
          extractBlock.versionHex,
          extractBlock.merkleroot,
          new Timestamp(extractBlock.mediantime * 1000L),
          extractBlock.nonce,
          extractBlock.bits,
          extractBlock.difficulty,
          extractBlock.chainwork,
          extractBlock.nTx,
          extractBlock.previousblockhash
        )
      })
  }
}
