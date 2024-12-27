package chainslake.cex

import java.sql.{Date, Timestamp}

case class CexExchangeInfo(var symbol: String,
                           var base_asset: String,
                           var quote_asset: String)

case class CexTradeMinute(var block_date: Date,
                          var block_minute: Timestamp,
                          var symbol: String,
                          var base_asset: String,
                          var quote_asset: String,
                          var open_price: Double,
                          var high_price: Double,
                          var low_price: Double,
                          var close_price: Double,
                          var volume: Double,
                          var quote_volume: Double,
                          var number_trades: Int,
                          var taker_buy_base_volume: Double,
                          var taker_buy_quote_volume: Double
                         )
