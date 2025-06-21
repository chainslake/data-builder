package chainslake.bitcoin_extract

import chainslake.ChainslakeTest
import chainslake.bitcoin.extract.Blocks
import org.apache.spark.sql.SaveMode

class BlocksTest extends ChainslakeTest {

  test("transform") {
    properties.setProperty("list_input_tables", "bitcoin_origin.transaction_blocks")
    val result = Blocks.transform(spark, 902068, 902068, properties)
    assert(result.count() == 1)
//    result.show()
//    spark.sql(s"create database if not exists bitcoin")
//    result.write
//      .mode(SaveMode.Overwrite).format("delta")
//      .saveAsTable("bitcoin.blocks")
  }

}
