package bitcoin_extract

import chainslake.ChainslakeTest
import chainslake.bitcoin.extract.Blocks

class BlocksTest extends ChainslakeTest {

  test("transform") {
    mockData("bitcoin_origin.transaction_blocks").createTempView("transaction_blocks")
    properties.setProperty("list_input_tables", "transaction_blocks")
    val result = Blocks.transform(spark, 902068, 902068, properties)
    assert(result.count() == 1)
//    result.show()
  }

}
