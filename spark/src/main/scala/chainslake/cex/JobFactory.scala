package chainslake.cex

import chainslake.job.JobInf


object JobFactory {
  def createJob(name: String): JobInf = {
    name match {
      case "binance_cex.exchange_info" => binance.ExchangeInfo
      case "binance_cex.trade_minute" => binance.TradeMinute
    }
  }
}
