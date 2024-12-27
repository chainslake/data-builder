$CHAINSLAKE_HOME_DIR/spark/script/chainslake-run.sh --class chainslake.sql.Main \
    --name EthereumPriceHour \
    --master local[4] \
    --driver-memory 4g \
    --conf "spark.app_properties.app_name=sql.transformer" \
    --conf "spark.app_properties.input_table_name=$1" \
    --conf "spark.app_properties.output_table_name=$2" \
    --conf "spark.app_properties.config_file=ethereum/application.properties" \
    --conf "spark.app_properties.sql_file=evm_prices/hour.sql"