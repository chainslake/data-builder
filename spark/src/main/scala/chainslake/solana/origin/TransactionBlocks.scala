package chainslake.solana.origin

import chainslake.job.TaskRun
import chainslake.solana.{OriginBlock, ResponseRawBlock, ResponseRawNumber}
import com.google.gson.Gson
import org.apache.spark.sql.{Dataset, SaveMode, SparkSession}
import org.apache.spark.sql.functions.{col, explode, lit, sequence}
import scalaj.http.Http

import java.sql.{Date, Timestamp}
import java.util.Properties

object TransactionBlocks extends TaskRun {

  override def run(spark: SparkSession, properties: Properties): Unit = {
    val chainName = properties.getProperty("chain_name")
    properties.setProperty("frequent_type", "block")
    properties.setProperty("list_input_tables", "node")
    val database = chainName + "_origin"
    try {
      spark.sql(s"create database if not exists $database")
    } catch {
      case e: Exception => e.getMessage
    }
    processTable(spark, chainName + "_origin.transaction_blocks", properties)
  }

  protected def onProcess(spark: SparkSession, outputTable: String, fromBlock: Long, toBlock: Long, properties: Properties): Unit = {
    processCrawlBlocks(spark, fromBlock, toBlock, properties)
      .repartitionByRange(col("block_date"), col("block_time"))
      .write.partitionBy("block_date")
      .mode(SaveMode.Append).format("delta")
      .saveAsTable(outputTable)
  }

  private def processCrawlBlocks(spark: SparkSession, fromBlock: Long, toBlock: Long, properties: Properties): Dataset[OriginBlock] = {
    import spark.implicits._
    val numberPartition = properties.getProperty("number_partitions").toInt
    val blockStr = s"""{"from_block": $fromBlock, "to_block": $toBlock }"""
    val rpcList = properties.getProperty("rpc_list").split(",")
    val maxRetry = properties.getProperty("max_retry").toInt
    spark.read.json(Seq(blockStr).toDS).select(explode(sequence(col("from_block"), col("to_block"))).alias("block_number"),
        lit(new Timestamp(0l)).as("block_time"), lit(new Date(0l)).as("block_date"),
        lit("").as("block"))
      .as[OriginBlock].repartitionByRange(numberPartition, col("block_number")).mapPartitions(par => {

        val blockData = par.map(block => {
          val transactionBlock = getOriginBlock(rpcList, block.block_number, maxRetry)
          block.block = transactionBlock._1
          block.block_time = new Timestamp(transactionBlock._2.longValue() * 1000L)
          block.block_date = new Date(block.block_time.getTime)
          block
        }).filter(block => block.block != null)
        blockData
      })
  }

  def getOriginBlock(listRpc: Array[String], blockNumber: Long, maxRetry: Int): (String, BigInt) = {
    var success = false
    var numberRetry = 0
    val gson = new Gson()
    var result = ""
    var blockTimestamp = BigInt(0)
    while (!success && numberRetry < maxRetry) {
      val rpc = listRpc {
        scala.util.Random.nextInt(listRpc.length)
      }
      try {
        val response = Http(rpc).header("Content-Type", "application/json")
          .postData(s"""{"method":"getBlock","params":[$blockNumber, {"encoding": "jsonParsed","maxSupportedTransactionVersion":0,"transactionDetails":"full","rewards":true}],"id":1,"jsonrpc":"2.0"}""").asString
        val responseRawBlock = gson.fromJson(response.body, classOf[ResponseRawBlock])
        val transactionBlock = responseRawBlock.result
        if (transactionBlock == null) {
          if (responseRawBlock.error != null && responseRawBlock.error.code == -32007) {
            blockTimestamp = 0
            result = null
            success = true
          } else {
            throw new Exception("don't have transaction block from block: " + blockNumber)
          }
        } else {
          blockTimestamp = transactionBlock.blockTime
          result = response.body
          success = true
        }
      } catch {
        case e: Exception => {
          println("error in block: " + blockNumber + " with rpc: ", rpc)
          Thread.sleep(100)
          //          throw e
        }
      }
      numberRetry += 1
    }
    if (!success) {
      throw new Exception("Max number retry")
    }

    (result, blockTimestamp)
  }

  override def getFirstInput(spark: SparkSession, properties: Properties): Long = {
    0L
  }

  override def getLatestInput(spark: SparkSession, properties: Properties): Long = {
    val listRpc = properties.getProperty("rpc_list").split(",")
    val gson = new Gson()
    val maxRetry = properties.getProperty("max_retry").toInt
    var success = false
    var numberRetry = 0
    var latestBlock = 0l
    while (!success && numberRetry < maxRetry) {
      val rpc = listRpc {
        scala.util.Random.nextInt(listRpc.length)
      }
      try {
        val response = Http(rpc).header("Content-Type", "application/json")
          .postData(s"""{"method":"getSlot","params":[],"id":1,"jsonrpc":"2.0"}""").asString
        latestBlock = gson.fromJson(response.body, classOf[ResponseRawNumber]).result
        success = true
      } catch {
        case e: Exception => {
          println("error in get block number")
          Thread.sleep(1000)
        }
        //          Thread.sleep(100 * numberRetry)
      }
      numberRetry += 1
    }
    if (!success) {
      throw new Exception("Max number retry")
    }
    latestBlock
  }
}
