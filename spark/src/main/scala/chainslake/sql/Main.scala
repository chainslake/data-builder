package chainslake.sql

import org.apache.spark.sql.SparkSession

import java.io.FileInputStream
import java.util.Properties

object Main {
  def main(args: Array[String]) {
    val properties = new Properties
    val spark = SparkSession.builder
      .enableHiveSupport()
      .getOrCreate()

    val configFile = spark.conf.get("spark.app_properties.chainslake_home_dir") + "/jobs/" + spark.conf.get("spark.app_properties.config_file")
    properties.load(new FileInputStream(configFile))

    spark.conf.getAll.foreach(pair => {
      if (pair._1.startsWith("spark.app_properties")) {
        properties.setProperty(pair._1.substring(21), pair._2)
      }
    })

    JobFactory.createJob(properties.getProperty("app_name")).run(spark, properties)
    spark.stop()
    print("Application stopped!!!")

  }

}
