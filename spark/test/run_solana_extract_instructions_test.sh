./build.sh
spark-submit --class chainslake.solana.Main \
    --deploy-mode client \
    --name Solana \
    --master local[1] \
    --driver-memory 1g \
    --conf "spark.app_properties.app_name=solana.instructions" \
    --conf "spark.app_properties.start_number=311388000" \
    --conf "spark.app_properties.number_partitions=4" \
    --conf "spark.app_properties.end_number=311388010" \
    --conf "spark.app_properties.config_file=solana/application.properties" \
    --conf "spark.app_properties.chainslake_home_dir=../../" \
    --conf "spark.sql.extensions=io.delta.sql.DeltaSparkSessionExtension" \
    --conf "spark.sql.catalog.spark_catalog=org.apache.spark.sql.delta.catalog.DeltaCatalog" \
    --conf spark.databricks.delta.retentionDurationCheck.enabled=false \
    --conf spark.scheduler.mode=FAIR \
    --jars ../lib/chainslake-job.jar \
    --packages com.esaulpaugh:headlong:9.2.0,org.web3j:abi:4.5.10,org.web3j:core:4.5.10,io.delta:delta-spark_2.12:3.2.0,org.scalaj:scalaj-http_2.12:2.4.2,com.github.ajrnz:scemplate_2.12:0.5.1 \
    chainslake-app.jar

