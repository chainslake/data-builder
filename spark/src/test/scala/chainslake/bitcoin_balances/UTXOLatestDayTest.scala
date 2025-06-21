package chainslake.bitcoin_balances

import chainslake.ChainslakeTest
import chainslake.sql.Transformer
import org.apache.spark.sql.SaveMode

class UTXOLatestDayTest extends ChainslakeTest {
  test("transform when first time") {
    properties.setProperty("sql_file", "bitcoin_balances/utxo_latest_day.sql")
    properties.setProperty("chain_name", "bitcoin")
    Transformer.prepareProperties(properties)

    val result = Transformer.transform(spark, 1750377600, 1750377601, properties)
    assert(result.count() == 5)
//    result.show()
//    result.write.mode(SaveMode.Overwrite)
//      .format("delta").saveAsTable("bitcoin_balances.utxo_latest_day")
  }

  test("transform when next time") {
    properties.setProperty("sql_file", "bitcoin_balances/utxo_latest_day.sql")
    properties.setProperty("chain_name", "bitcoin")
    properties.setProperty("is_existed_table", "true")
    properties.setProperty("use_version", "true")
    properties.setProperty("current_version", "1")
    properties.setProperty("next_version", "2")
    Transformer.prepareProperties(properties)

//    spark.sql("select * from bitcoin_balances.utxo_latest_day").show()
    val result = Transformer.transform(spark, 1750377600, 1750377601, properties)
    assert(result.count() == 5)
//    result.show()
  }
}