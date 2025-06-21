package chainslake.cex_binance

import chainslake.ChainslakeTest
import chainslake.cex.binance.TradeMinute
import org.apache.spark.sql.SaveMode
import scalaj.http.HttpResponse

class TradeMinuteTest extends ChainslakeTest {

  def mockHttpCall(url: String): HttpResponse[String] = {
    var result = ""
    if (url.contains("time")) {
      result = """{"serverTime":1750478331191}"""
    } else {
      result = """[[1750477740000,"0.02345000","0.02345000","0.02345000","0.02345000","0.00000000",1750477799999,"0.00000000",0,"0.00000000","0.00000000","0"],[1750477800000,"0.02345000","0.02345000","0.02344000","0.02344000","2.48770000",1750477859999,"0.05833633",10,"2.46520000","0.05780893","0"],[1750477860000,"0.02344000","0.02344000","0.02344000","0.02344000","5.65590000",1750477919999,"0.13257427",15,"5.65590000","0.13257427","0"],[1750477920000,"0.02343000","0.02343000","0.02343000","0.02343000","0.20010000",1750477979999,"0.00468831",6,"0.00000000","0.00000000","0"],[1750477980000,"0.02343000","0.02343000","0.02343000","0.02343000","0.00000000",1750478039999,"0.00000000",0,"0.00000000","0.00000000","0"],[1750478040000,"0.02343000","0.02343000","0.02343000","0.02343000","0.00000000",1750478099999,"0.00000000",0,"0.00000000","0.00000000","0"],[1750478100000,"0.02343000","0.02344000","0.02343000","0.02344000","0.20580000",1750478159999,"0.00482201",3,"0.01300000","0.00030472","0"],[1750478160000,"0.02344000","0.02344000","0.02344000","0.02344000","0.00000000",1750478219999,"0.00000000",0,"0.00000000","0.00000000","0"],[1750478220000,"0.02344000","0.02344000","0.02344000","0.02344000","0.00000000",1750478279999,"0.00000000",0,"0.00000000","0.00000000","0"],[1750478280000,"0.02343000","0.02343000","0.02343000","0.02343000","16.43140000",1750478339999,"0.38498763",14,"0.00000000","0.00000000","0"]]"""
    }

    HttpResponse[String](
      body = result,
      code = 200,
      headers = Map()
    )
  }

  test("transform") {
    TradeMinute.httpCall = mockHttpCall
    properties.setProperty("list_input_tables", "cex_binance.exchange_info")
    properties.setProperty("quote_asset", "BTC")
    val result = TradeMinute.transform(spark, 1750477731L, 1750478331L, properties)
    assert(result.count() == 10)
//    result.show()
//    result.write
//      .mode(SaveMode.Overwrite).format("delta")
//      .saveAsTable("cex_binance.trade_minute")
  }

  test("getLatestInput") {
    TradeMinute.httpCall = mockHttpCall
    val result = TradeMinute.getLatestInput(spark, properties)
    assert(result == 1750478331)
  }

  ignore("defaultHttpCall") {
    val url = ""

    // Get latest time
    var result = TradeMinute.defaultHttpCall(url + "/api/v3/time").body
    println(result)

    // Get data
    result = TradeMinute.defaultHttpCall(url + "/api/v3/klines?interval=1m" + s"&symbol=ETHBTC&startTime=1750477731191&endTime=1750478331191&limit=1500").body
    println(result)
  }
}
