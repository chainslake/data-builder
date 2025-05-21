package chainslake.cex.binance

import chainslake.job.TaskRun
import chainslake.cex.{CexExchangeInfo, CexTradeMinute}
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.{SaveMode, SparkSession}
import scalaj.http.Http

import java.sql.{Date, Timestamp}
import java.util
import java.util.Properties

object TradeMinute extends TaskRun {

  override def run(spark: SparkSession, properties: Properties): Unit = {
    properties.setProperty("frequent_type", "minute")
    properties.setProperty("list_input_tables", "binance")
    try {
      spark.sql(s"create database if not exists cex_binance")
    } catch {
      case e: Exception => e.getMessage
    }
    processTable(spark, "cex_binance.trade_minute", properties)
  }

  protected def onProcess(spark: SparkSession, outputTable: String, fromBlockTime: Long, toBlockTime: Long, properties: Properties): Unit = {
    import spark.implicits._
    val quoteAsset = properties.getProperty("quote_asset")
    val numberPartition = properties.getProperty("number_re_partitions").toInt
    val waitMilliseconds = properties.getProperty("wait_milliseconds").toLong
    val url = properties.getProperty("binance_cex_url")
    val api = url + "/api/v3/klines?interval=1m"
    val inputTable = "cex_binance.exchange_info"
    spark.read.table(inputTable).repartition(numberPartition).where(col("quote_asset") === quoteAsset).as[CexExchangeInfo].flatMap(exchange => {
        val gson = new Gson()
        val startTime = fromBlockTime * 1000L
        val endTime = (toBlockTime - 60L) * 1000L
        var result = List[CexTradeMinute]()
        val response = Http(api + s"&symbol=${exchange.symbol}&startTime=${startTime}&endTime=${endTime}&limit=1500").header("Content-Type", "application/json")
          .timeout(50000, 50000)
          .asString
        Thread.sleep(waitMilliseconds)
        gson.fromJson(response.body, classOf[Object]).asInstanceOf[util.ArrayList[util.ArrayList[Any]]]
          .forEach(item => {
            val minute = new Timestamp(item.get(0).asInstanceOf[Double].toLong)
            val date = new Date(minute.getTime)
            result = result :+ CexTradeMinute(date, minute, exchange.symbol, exchange.base_asset, exchange.quote_asset,
              item.get(1).asInstanceOf[String].toDouble,
              item.get(2).asInstanceOf[String].toDouble,
              item.get(3).asInstanceOf[String].toDouble,
              item.get(4).asInstanceOf[String].toDouble,
              item.get(5).asInstanceOf[String].toDouble,
              item.get(7).asInstanceOf[String].toDouble,
              item.get(8).asInstanceOf[Double].toInt,
              item.get(9).asInstanceOf[String].toDouble,
              item.get(10).asInstanceOf[String].toDouble
            )
          })
        result
      }).write.format("delta")
      .option("optimizeWrite", "True")
      .mode(SaveMode.Append)
      .partitionBy("block_date")
      .saveAsTable(outputTable)
  }

  override def getLatestInput(spark: SparkSession, properties: Properties): Long = {
    val url = properties.getProperty("binance_cex_url")
    val api = url + "/api/v3/time"
    val response = Http(api).header("Content-Type", "application/json")
      .timeout(50000, 50000)
      .asString
    val gson = new Gson()
    gson.fromJson(response.body, classOf[Object]).asInstanceOf[LinkedTreeMap[String, Any]]
      .get("serverTime").asInstanceOf[Double].toLong / 1000L
  }

  override def getFirstInput(spark: SparkSession, properties: Properties): Long = {
    0L
  }
}
