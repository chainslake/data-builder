package bitcoin_extract

import chainslake.ChainslakeTest
import chainslake.bitcoin.extract.Inputs

class InputsTest extends ChainslakeTest {

  test("transform") {
    mockData("bitcoin_origin.transaction_blocks").createTempView("transaction_blocks")
    properties.setProperty("list_input_tables", "transaction_blocks")
    val result = Inputs.transform(spark, 902068, 902068, properties)
    assert(result.count() == 2)
//    result.show()
  }

}
