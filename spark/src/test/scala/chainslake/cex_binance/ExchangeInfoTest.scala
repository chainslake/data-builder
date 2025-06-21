package chainslake.cex_binance

import chainslake.ChainslakeTest
import chainslake.cex.binance.ExchangeInfo
import org.apache.spark.sql.SaveMode
import scalaj.http.HttpResponse

class ExchangeInfoTest extends ChainslakeTest {

  def mockHttpCall(url: String): HttpResponse[String] = {
    HttpResponse[String](
      body = """{"timezone":"UTC","serverTime":1750476472810,"rateLimits":[{"rateLimitType":"REQUEST_WEIGHT","interval":"MINUTE","intervalNum":1,"limit":6000},{"rateLimitType":"ORDERS","interval":"SECOND","intervalNum":10,"limit":100},{"rateLimitType":"ORDERS","interval":"DAY","intervalNum":1,"limit":200000},{"rateLimitType":"RAW_REQUESTS","interval":"MINUTE","intervalNum":5,"limit":61000}],"exchangeFilters":[],"symbols":[{"symbol":"ETHBTC","status":"TRADING","baseAsset":"ETH","baseAssetPrecision":8,"quoteAsset":"BTC","quotePrecision":8,"quoteAssetPrecision":8,"baseCommissionPrecision":8,"quoteCommissionPrecision":8,"orderTypes":["LIMIT","LIMIT_MAKER","MARKET","STOP_LOSS","STOP_LOSS_LIMIT","TAKE_PROFIT","TAKE_PROFIT_LIMIT"],"icebergAllowed":true,"ocoAllowed":true,"otoAllowed":true,"quoteOrderQtyMarketAllowed":true,"allowTrailingStop":true,"cancelReplaceAllowed":true,"amendAllowed":true,"isSpotTradingAllowed":true,"isMarginTradingAllowed":true,"filters":[{"filterType":"PRICE_FILTER","minPrice":"0.00001000","maxPrice":"922327.00000000","tickSize":"0.00001000"},{"filterType":"LOT_SIZE","minQty":"0.00010000","maxQty":"100000.00000000","stepSize":"0.00010000"},{"filterType":"ICEBERG_PARTS","limit":10},{"filterType":"MARKET_LOT_SIZE","minQty":"0.00000000","maxQty":"985.33449583","stepSize":"0.00000000"},{"filterType":"TRAILING_DELTA","minTrailingAboveDelta":10,"maxTrailingAboveDelta":2000,"minTrailingBelowDelta":10,"maxTrailingBelowDelta":2000},{"filterType":"PERCENT_PRICE_BY_SIDE","bidMultiplierUp":"5","bidMultiplierDown":"0.2","askMultiplierUp":"5","askMultiplierDown":"0.2","avgPriceMins":5},{"filterType":"NOTIONAL","minNotional":"0.00010000","applyMinToMarket":true,"maxNotional":"9000000.00000000","applyMaxToMarket":false,"avgPriceMins":5},{"filterType":"MAX_NUM_ORDERS","maxNumOrders":200},{"filterType":"MAX_NUM_ALGO_ORDERS","maxNumAlgoOrders":5}],"permissions":[],"permissionSets":[],"defaultSelfTradePreventionMode":"EXPIRE_MAKER","allowedSelfTradePreventionModes":["EXPIRE_TAKER","EXPIRE_MAKER","EXPIRE_BOTH","DECREMENT"]}]}""",
      code = 200,
      headers = Map()
    )
  }

  test("transform") {
    ExchangeInfo.httpCall = mockHttpCall
    val result = ExchangeInfo.transform(spark, properties)
    assert(result.count() == 1)
//    result.show()
//    spark.sql("create database if not exists cex_binance")
//    result.write
//      .mode(SaveMode.Overwrite).format("delta")
//      .saveAsTable("cex_binance.exchange_info")
  }

  ignore("defaultHttpCall") {
    val url = "https://data-api.binance.vision"
    val api = url + "/api/v3/exchangeInfo?symbolStatus=TRADING&showPermissionSets=false"
    val result = ExchangeInfo.defaultHttpCall(api).body
    println(result)
  }
}
