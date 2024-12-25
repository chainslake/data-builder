spark-submit "$@" \
    --deploy-mode client \
    --conf "spark.app_properties.chainslake_home_dir=$CHAINSLAKE_HOME_DIR" \
    --conf "spark.app_properties.postgres_url=$POSTGRES_ADMIN_URL" \
    --conf "spark.app_properties.postgres_user=$POSTGRES_USER" \
    --conf "spark.app_properties.postgres_password=$POSTGRES_PASSWORD" \
    --conf "spark.app_properties.sonar_token=$BOT_TOKEN" \
    --conf "spark.app_properties.chainslake_token=$ADMIN_TOKEN" \
    --conf "spark.app_properties.admin_group_id=$ADMIN_GROUP_ID" \
    --conf "spark.sql.extensions=io.delta.sql.DeltaSparkSessionExtension" \
    --conf "spark.sql.catalog.spark_catalog=org.apache.spark.sql.delta.catalog.DeltaCatalog" \
    --conf spark.databricks.delta.retentionDurationCheck.enabled=false \
    --conf spark.scheduler.mode=FAIR \
    --packages com.esaulpaugh:headlong:9.2.0,org.web3j:abi:4.5.10,org.web3j:core:4.5.10,io.delta:delta-spark_2.12:3.2.0,org.scalaj:scalaj-http_2.12:2.4.2,com.github.ajrnz:scemplate_2.12:0.5.1 \
    ~/chainslake.jar