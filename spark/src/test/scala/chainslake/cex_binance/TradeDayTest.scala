package chainslake.cex_binance

import chainslake.ChainslakeTest
import chainslake.sql.Transformer
import org.apache.spark.sql.SaveMode

class TradeDayTest extends ChainslakeTest {
  test("transform") {
    properties.setProperty("sql_file", "cex_binance/trade_day.sql")
    Transformer.prepareProperties(properties)

    val result = Transformer.transform(spark, 1750464000, 1750464001, properties)
    assert(result.count() == 1)
//    result.show()
//    result.write.mode(SaveMode.Overwrite)
//      .format("delta").saveAsTable("cex_binance.trade_day")
  }
}