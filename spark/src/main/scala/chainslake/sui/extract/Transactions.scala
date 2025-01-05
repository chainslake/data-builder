package chainslake.sui.extract

import chainslake.job.TaskRun
import chainslake.sui.{ExtractedTransaction, OriginBlock, ResponseBlock, Transaction}
import com.google.gson.Gson
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions.col
import org.apache.spark.storage.StorageLevel

import java.util.Properties

object Transactions extends TaskRun {
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
    processTable(spark, chainName + ".transactions", properties)
  }

  override protected def onProcess(spark: SparkSession, outputTable: String, from: Long, to: Long, properties: Properties): Unit = {
    import spark.implicits._
    val inputTableName = properties.getProperty("list_input_tables")
    spark.read.table(inputTableName).where(col("block_number") >= from && col("block_number") <= to)
      .as[OriginBlock].flatMap(block => {
        val gson = new Gson()
        val extractTransactions = gson.fromJson(block.transactions, classOf[Array[Transaction]])
        extractTransactions.map(transaction => {
          ExtractedTransaction(
            block.block_date,
            block.block_number,
            block.block_time,
            transaction.digest,
            transaction.transaction.data.transaction.kind,
            transaction.transaction.txSignatures,
            transaction.transaction.data.messageVersion,
            transaction.transaction.data.sender,
            transaction.transaction.data.gasData.owner,
            transaction.transaction.data.gasData.price.toLong,
            transaction.transaction.data.gasData.budget.toLong,
            transaction.transaction.data.gasData.payment.map(payment => gson.toJson(payment)),
            try {
              transaction.transaction.data.transaction.inputs.map(input => gson.toJson(input))
            } catch {
              case e: Exception => null
            },
            try {
              transaction.transaction.data.transaction.transactions.map(transaction => gson.toJson(transaction))
            } catch {
              case e: Exception => null
            },
            gson.toJson(transaction.effects.status),
            transaction.effects.executedEpoch.toLong,
            transaction.effects.gasUsed.computationCost.toLong,
            transaction.effects.gasUsed.storageCost.toLong,
            transaction.effects.gasUsed.storageRebate.toLong,
            transaction.effects.gasUsed.nonRefundableStorageFee.toLong,
            try {
              transaction.effects.modifiedAtVersions.map(mav => gson.toJson(mav))
            } catch {
              case e: Exception => null
            },
            try {
              transaction.effects.sharedObjects.map(so => gson.toJson(so))
            } catch {
              case e: Exception => null
            },
            try {
              transaction.effects.created.map(created => gson.toJson(created))
            } catch {
              case e: Exception => null
            },
            try {
              transaction.effects.mutated.map(mutated => gson.toJson(mutated))
            } catch {
              case e: Exception => null
            },
            try {
              transaction.effects.deleted.map(deleted => gson.toJson(deleted))
            } catch {
              case e: Exception => null
            },
            transaction.effects.eventsDigest,
            transaction.effects.dependencies
          )
        })
      })
      .persist(StorageLevel.MEMORY_AND_DISK)
      .repartitionByRange(col("block_date"), col("block_time"))
      .write.partitionBy("block_date")
      .mode(SaveMode.Append).format("delta")
      .saveAsTable(outputTable)
  }
}
