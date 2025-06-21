package chainslake.bitcoin_extract

import chainslake.ChainslakeTest
import chainslake.bitcoin.extract.Inputs
import org.apache.spark.sql.SaveMode

class InputsTest extends ChainslakeTest {

  test("transform") {
    properties.setProperty("list_input_tables", "bitcoin_origin.transaction_blocks")
    val result = Inputs.transform(spark, 902068, 902068, properties)
    assert(result.count() == 2)
//    result.show()

//    result.write
//      .mode(SaveMode.Overwrite).format("delta")
//      .saveAsTable("bitcoin.inputs")
  }

}
