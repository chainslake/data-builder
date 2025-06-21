package bitcoin_extract

import chainslake.ChainslakeTest
import chainslake.bitcoin.extract.Transactions

class TransactionsTest extends ChainslakeTest {

  test("transform") {
    mockData("bitcoin_origin.transaction_blocks").createTempView("transaction_blocks")
    properties.setProperty("list_input_tables", "transaction_blocks")
    val result = Transactions.transform(spark, 902068, 902068, properties)
    assert(result.count() == 2)
//    result.show()
  }

}
