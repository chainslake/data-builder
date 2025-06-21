package chainslake.bitcoin_balances

import chainslake.ChainslakeTest
import chainslake.sql.Transformer
import org.apache.spark.sql.SaveMode

class UTXOTransferHourTest extends ChainslakeTest {
  test("transform") {
    properties.setProperty("sql_file", "bitcoin_balances/utxo_transfer_hour.sql")
    properties.setProperty("chain_name", "bitcoin")
    Transformer.prepareProperties(properties)

    val result = Transformer.transform(spark, 1750434033, 1750434034, properties)
    assert(result.count() == 5)
//    result.show()
//    spark.sql(s"create database if not exists bitcoin_balances")
//    result.write.mode(SaveMode.Overwrite)
//      .format("delta").saveAsTable("bitcoin_balances.utxo_transfer_hour")
  }
}
