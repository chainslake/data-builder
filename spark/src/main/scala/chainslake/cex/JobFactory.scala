package chainslake.cex

import chainslake.job.JobInf


object JobFactory {
  def createJob(name: String): JobInf = {
    name match {
      case "cex_binance.exchange_info" => binance.ExchangeInfo
      case "cex_binance.trade_minute" => binance.TradeMinute
    }
  }
}
