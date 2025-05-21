package cex

import chainslake.cex.CexTradeMinute
import com.google.gson.Gson
import org.scalatest.funsuite.AnyFunSuite
import scalaj.http.Http

import java.sql.{Date, Timestamp}
import java.util

class BinanceTest extends AnyFunSuite  {
  test("Cex get price ") {
    val api = "https://api.binance.com/api/v3/klines?interval=1m"
    val fromBlockTime = 1747474260
    val toBlockTime = 1747560600
    val startTime = fromBlockTime * 1000L
    val endTime = (toBlockTime - 60L) * 1000L
    var result = List[CexTradeMinute]()
    val gson = new Gson()
    val response = Http(api + s"&symbol=BTCUSDT&startTime=${startTime}&endTime=${endTime}&limit=1300").header("Content-Type", "application/json")
      .timeout(50000, 50000)
      .asString
    gson.fromJson(response.body, classOf[Object]).asInstanceOf[util.ArrayList[util.ArrayList[Any]]]
      .forEach(item => {
        val minute = new Timestamp(item.get(0).asInstanceOf[Double].toLong)
        val date = new Date(minute.getTime)
        result = result :+ CexTradeMinute(date, minute, "BTCUSDT", "BTC", "USDT",
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
    println(result)
  }
}
