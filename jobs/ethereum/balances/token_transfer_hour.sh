$CHAINSLAKE_HOME_DIR/spark/script/chainslake-run.sh --class chainslake.sql.Main \
    --name EthereumTokenTransferHour \
    --master local[4] \
    --driver-memory 4g \
    --conf "spark.app_properties.app_name=sql.transformer" \
    --conf "spark.app_properties.native_symbol=ETH" \
    --conf "spark.app_properties.config_file=ethereum/application.properties" \
    --conf "spark.app_properties.sql_file=evm_balances/token_transfer_hour.sql"