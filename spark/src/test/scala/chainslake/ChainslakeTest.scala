package chainslake

import org.apache.spark.sql.SparkSession
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite

import java.nio.file.Paths
import java.util.Properties

trait SparkTestSupport {
  lazy val spark: SparkSession = SparkSession.builder()
    .master("local[*]")
    .appName("test")
    .config("spark.sql.extensions", "io.delta.sql.DeltaSparkSessionExtension")
    .config("spark.sql.catalog.spark_catalog", "org.apache.spark.sql.delta.catalog.DeltaCatalog")
    .enableHiveSupport()
    .getOrCreate()
  spark.sparkContext.setLogLevel("ERROR")
}

class ChainslakeTest extends AnyFunSuite with BeforeAndAfterAll with SparkTestSupport {
  protected var properties: Properties = _

  override def beforeAll(): Unit = {
    properties = new Properties()
    properties.setProperty("number_partitions", "1")
    properties.setProperty("rpc_list", "https://localhost")
    properties.setProperty("max_retry", "1")
    properties.setProperty("number_re_partitions", "1")
    properties.setProperty("wait_milliseconds", "1000")

    // Use for sql.Transformer
    val chainslakeHomeDir = Paths.get(System.getProperty("user.dir")).getParent.toString
    properties.setProperty("chainslake_home_dir", chainslakeHomeDir)
    properties.setProperty("is_existed_table", "false")
    properties.setProperty("use_version", "false")
    properties.setProperty("current_version", "0")
    properties.setProperty("next_version", "1")

  }

}