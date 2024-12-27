spark-submit --class chainslake.cex.Main \
    --deploy-mode client \
    --name BinanceExchangeInfo \
    --master local[4] \
    --driver-memory 4g \
    --conf "spark.app_properties.app_name=binance_cex.exchange_info" \
    --conf "spark.app_properties.start_number=0" \
    --conf "spark.app_properties.end_number=0" \
    --conf "spark.app_properties.binance_cex_url=https://data-api.binance.vision" \
    --conf "spark.app_properties.config_file=binance/application.properties" \
    --conf "spark.app_properties.chainslake_home_dir=../../" \
    --conf "spark.sql.extensions=io.delta.sql.DeltaSparkSessionExtension" \
    --conf "spark.sql.catalog.spark_catalog=org.apache.spark.sql.delta.catalog.DeltaCatalog" \
    --conf spark.databricks.delta.retentionDurationCheck.enabled=false \
    --conf spark.scheduler.mode=FAIR \
    --jars ../lib/chainslake-job.jar \
    --packages com.esaulpaugh:headlong:9.2.0,org.web3j:abi:4.5.10,org.web3j:core:4.5.10,io.delta:delta-spark_2.12:3.2.0,org.scalaj:scalaj-http_2.12:2.4.2,com.github.ajrnz:scemplate_2.12:0.5.1 \
    chainslake-app.jar

