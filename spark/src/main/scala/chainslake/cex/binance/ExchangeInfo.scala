package chainslake.cex.binance

import chainslake.cex.CexExchangeInfo
import chainslake.job.JobInf
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import org.apache.spark.sql.{Dataset, SaveMode, SparkSession}
import scalaj.http.{Http, HttpResponse}

import java.util
import java.util.Properties

object ExchangeInfo extends JobInf {
  def defaultHttpCall(url: String): HttpResponse[String] = {
    Http(url).header("Content-Type", "application/json")
      .timeout(50000, 50000)
      .asString
  }
  var httpCall: String => HttpResponse[String] = defaultHttpCall

  override def run(spark: SparkSession, properties: Properties): Unit = {
    val outputTable = "cex_binance.exchange_info"
    transform(spark, properties).write.mode(SaveMode.Overwrite).format("delta")
      .saveAsTable(outputTable)
  }

  def transform(spark: SparkSession, properties: Properties): Dataset[CexExchangeInfo] = {
    import spark.implicits._
    try {
      spark.sql("create database if not exists cex_binance")
    } catch {
      case e: Exception => e.getMessage
    }
    val url = properties.getProperty("binance_cex_url")
    val api = url + "/api/v3/exchangeInfo?symbolStatus=TRADING&showPermissionSets=false"
    spark.read.json(Seq(s"""{"url": "$api"}""").toDS).flatMap(row => {
      val gson = new Gson()
      val response = httpCall(api)
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
    })
  }
}
