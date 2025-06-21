package chainslake.bitcoin_balances

import chainslake.ChainslakeTest
import chainslake.sql.Transformer
import org.apache.spark.sql.SaveMode

class UTXOTransferDayTest extends ChainslakeTest {
  test("transform") {
    properties.setProperty("sql_file", "bitcoin_balances/utxo_transfer_day.sql")
    properties.setProperty("chain_name", "bitcoin")
    Transformer.prepareProperties(properties)

    val result = Transformer.transform(spark, 1750377600, 1750377601, properties)
    assert(result.count() == 5)
//    result.show()
//    result.write.mode(SaveMode.Overwrite)
//      .format("delta").saveAsTable("bitcoin_balances.utxo_transfer_day")
  }
}
