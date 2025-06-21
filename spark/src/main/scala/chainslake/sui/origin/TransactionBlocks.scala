package chainslake.sui.origin

import chainslake.job.TaskRun
import chainslake.sui.{OriginBlock, ResponseRawBlock, ResponseRawString, ResponseRawTransactions}
import com.google.gson.Gson
import org.apache.spark.sql.{Dataset, SaveMode, SparkSession}
import org.apache.spark.sql.functions.{col, explode, lit, sequence}
import org.apache.spark.storage.StorageLevel
import scalaj.http.{Http, HttpResponse}

import java.sql.{Date, Timestamp}
import java.util.Properties

object TransactionBlocks extends TaskRun {

  def defaultRpcCall(url: String, body: String): HttpResponse[String] = {
    Http(url).header("Content-Type", "application/json")
      .postData(body)
      .timeout(50000, 50000)
      .asString
  }
  var rpcCall: (String, String) => HttpResponse[String] = defaultRpcCall

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
      .persist(StorageLevel.MEMORY_AND_DISK)
      .repartitionByRange(col("block_date"), col("block_time"))
      .write.partitionBy("block_date")
      .mode(SaveMode.Append).format("delta")
      .saveAsTable(outputTable)
  }

  def processCrawlBlocks(spark: SparkSession, fromBlock: Long, toBlock: Long, properties: Properties): Dataset[OriginBlock] = {
    import spark.implicits._
    val numberPartition = properties.getProperty("number_partitions").toInt
    val blockStr = s"""{"from_block": $fromBlock, "to_block": $toBlock }"""
    val rpcList = properties.getProperty("rpc_list").split(",")
    val maxRetry = properties.getProperty("max_retry").toInt
    spark.read.json(Seq(blockStr).toDS).select(explode(sequence(col("from_block"), col("to_block"))).alias("block_number"),
        lit(new Timestamp(0l)).as("block_time"), lit(new Date(0l)).as("block_date"),
        lit("").as("block"), lit("").as("transactions"))
      .as[OriginBlock].repartitionByRange(numberPartition, col("block_number")).mapPartitions(par => {

        val blockData = par.map(block => {
          val transactionBlock = getOriginBlock(rpcList, block.block_number, maxRetry)
          block.block = transactionBlock._1
          block.transactions = transactionBlock._2
          block.block_time = new Timestamp(transactionBlock._3.longValue())
          block.block_date = new Date(block.block_time.getTime)
          block
        }).filter(block => block.block != null)
        blockData
      })
  }

  def getOriginBlock(listRpc: Array[String], blockNumber: Long, maxRetry: Int): (String, String, BigInt) = {
    var success = false
    var numberRetry = 0
    val gson = new Gson()
    var block = ""
    var transactions = ""
    var blockTimestamp = BigInt(0)
    while (!success && numberRetry < maxRetry) {
      val rpc = listRpc {
        scala.util.Random.nextInt(listRpc.length)
      }
      try {
        var response = rpcCall(rpc, s"""{"jsonrpc":"2.0","method":"sui_getCheckpoint","params":["${blockNumber}"],"id":1}""")
        val responseRawBlock = gson.fromJson(response.body, classOf[ResponseRawBlock])
        val transactionBlock = responseRawBlock.result
        if (transactionBlock == null) {
          throw new Exception("don't have transaction block from block: " + blockNumber)
        }

        blockTimestamp = transactionBlock.timestampMs.toLong
        block = response.body
        val mTrans = transactionBlock.transactions.grouped(10)
        var listResult = Array[Any]()
        mTrans.foreach(trans => {
          response = rpcCall(rpc, s"""{"method":"sui_multiGetTransactionBlocks","params":[["${trans.mkString("\", \"")}"], {
                                                                                      |      "showInput": true,
                                                                                      |      "showRawInput": false,
                                                                                      |      "showEffects": true,
                                                                                      |      "showEvents": true,
                                                                                      |      "showObjectChanges": true,
                                                                                      |      "showBalanceChanges": true,
                                                                                      |      "showRawEffects": false
                                                                                      |    }],"id":1,"jsonrpc":"2.0"}""".stripMargin)
          val transactionBlock = gson.fromJson(response.body, classOf[ResponseRawTransactions]).result
          if (transactionBlock == null) {
            throw new Exception("don't have transaction block from block: " + blockNumber)
          }
          listResult = listResult ++ transactionBlock
        })
        if (transactionBlock.transactions.length != listResult.length) {
          throw new Exception("Not enough transaction at: " + blockNumber)
        }
        transactions = gson.toJson(listResult)

        success = true
      } catch {
        case e: Exception => {
          println("error in block: " + blockNumber + " with rpc: ", rpc)
//          Thread.sleep(1000)
          //          throw e
        }
      }
      numberRetry += 1
    }
    if (!success) {
      throw new Exception("Max number retry")
    }

    (block, transactions, blockTimestamp)
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
        val response = rpcCall(rpc, s"""{"jsonrpc":"2.0","method":"sui_getLatestCheckpointSequenceNumber","params":[],"id":1}""")
        latestBlock = gson.fromJson(response.body, classOf[ResponseRawString]).result.toLong
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
