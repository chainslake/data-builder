package chainslake.solana.extract

import chainslake.job.TaskRun
import chainslake.solana.{ExtractedInstruction, OriginBlock, ResponseBlock}
import com.google.gson.Gson
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions.col
import org.apache.spark.storage.StorageLevel

import java.util.Properties

object Instructions extends TaskRun {

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
    processTable(spark, chainName + ".instructions", properties)
  }

  override protected def onProcess(spark: SparkSession, outputTable: String, from: Long, to: Long, properties: Properties): Unit = {
    import spark.implicits._
    val inputTableName = properties.getProperty("list_input_tables")
    spark.read.table(inputTableName).where(col("block_number") >= from && col("block_number") <= to)
      .as[OriginBlock].flatMap(block => {
        val gson = new Gson()
        val extractBlock = gson.fromJson(block.block, classOf[ResponseBlock]).result
        extractBlock.transactions.filter(transaction => transaction.meta.err == null).flatMap(transaction => {
          transaction.transaction.message.instructions.map(instruction => {
            ExtractedInstruction(
              block.block_date,
              block.block_number,
              block.block_time,transaction.transaction.signatures(0),
              instruction.programIdIndex,
              instruction.programId,
              instruction.program,
              if (instruction.parsed != null) gson.toJson(instruction.parsed) else null,
              instruction.accounts,
              instruction.data
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
