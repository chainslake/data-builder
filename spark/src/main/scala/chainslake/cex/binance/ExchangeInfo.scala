package chainslake.cex.binance

import chainslake.cex.CexExchangeInfo
import chainslake.job.JobInf
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import org.apache.spark.sql.{SaveMode, SparkSession}
import scalaj.http.Http

import java.util
import java.util.Properties

object ExchangeInfo extends JobInf {
  override def run(spark: SparkSession, properties: Properties): Unit = {
    import spark.implicits._
    val outputTable = "binance_cex.exchange_info"
    try {
      spark.sql("create database if not exists binance_cex")
    } catch {
      case e: Exception => e.getMessage
    }
    val url = properties.getProperty("binance_cex_url")
    val api = url + "/api/v3/exchangeInfo?symbolStatus=TRADING&showPermissionSets=false"
    spark.read.json(Seq(s"""{"url": "$api"}""").toDS).flatMap(row => {
      val gson = new Gson()
      val response = Http(api).header("Content-Type", "application/json")
        .timeout(50000, 50000)
        .asString
      var result = List[CexExchangeInfo]()
      gson.fromJson(response.body, classOf[Object]).asInstanceOf[LinkedTreeMap[String, Any]]
        .get("symbols").asInstanceOf[util.ArrayList[LinkedTreeMap[String, Any]]]
        .forEach(symbol => {
          result = result :+ CexExchangeInfo(symbol.get("symbol").asInstanceOf[String],
            symbol.get("baseAsset").asInstanceOf[String],
            symbol.get("quoteAsset").asInstanceOf[String]
          )
        })
      result
    }).write.mode(SaveMode.Overwrite).format("delta")
      .saveAsTable(outputTable)
  }
}
