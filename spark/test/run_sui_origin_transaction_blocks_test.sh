./build.sh
spark-submit --class chainslake.sui.Main \
    --deploy-mode client \
    --name Sui \
    --master local[40] \
    --driver-memory 8g \
    --conf "spark.app_properties.app_name=sui_origin.transaction_blocks" \
    --conf "spark.app_properties.start_number=97936844" \
    --conf "spark.app_properties.number_partitions=40" \
    --conf "spark.app_properties.end_number=97937484" \
    --conf "spark.app_properties.rpc_list=http://node-prod.chainslake:1800/sui3" \
    --conf "spark.app_properties.config_file=sui/application.properties" \
    --conf "spark.app_properties.chainslake_home_dir=../../" \
    --conf "spark.sql.extensions=io.delta.sql.DeltaSparkSessionExtension" \
    --conf "spark.sql.catalog.spark_catalog=org.apache.spark.sql.delta.catalog.DeltaCatalog" \
    --conf spark.databricks.delta.retentionDurationCheck.enabled=false \
    --conf spark.scheduler.mode=FAIR \
    --jars ../lib/chainslake-job.jar \
    --packages com.esaulpaugh:headlong:9.2.0,org.web3j:abi:4.5.10,org.web3j:core:4.5.10,io.delta:delta-spark_2.12:3.2.0,org.scalaj:scalaj-http_2.12:2.4.2,com.github.ajrnz:scemplate_2.12:0.5.1 \
    chainslake-app.jar

