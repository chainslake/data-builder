package chainslake.bitcoin_extract

import chainslake.ChainslakeTest
import chainslake.bitcoin.extract.Outputs
import org.apache.spark.sql.SaveMode


class OutputsTest extends ChainslakeTest {

  test("transform") {
    properties.setProperty("list_input_tables", "bitcoin_origin.transaction_blocks")
    val result = Outputs.transform(spark, 902068, 902068, properties)
    assert(result.count() == 8)
//    result.show()
//    result.write
//      .mode(SaveMode.Overwrite).format("delta")
//      .saveAsTable("bitcoin.outputs")
  }

}
