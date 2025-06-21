package bitcoin_extract

import chainslake.ChainslakeTest
import chainslake.bitcoin.extract.Outputs


class OutputsTest extends ChainslakeTest {

  test("transform") {
    mockData("bitcoin_origin.transaction_blocks").createTempView("transaction_blocks")
    properties.setProperty("list_input_tables", "transaction_blocks")
    val result = Outputs.transform(spark, 902068, 902068, properties)
    assert(result.count() == 8)
//    result.show()
  }

}
