$CHAINSLAKE_HOME_DIR/spark/script/chainslake-run.sh --class chainslake.Main \
    --name EthereumPriceUSDMinute \
    --master local[4] \
    --driver-memory 4g \
    --conf "spark.app_properties.app_name=sql.transformer" \
    --conf "spark.app_properties.price_erc20_table_name=erc20_weth_minute" \
    --conf "spark.app_properties.price_wrap_native_table_name=weth_usd_minute" \
    --conf "spark.app_properties.config_file=ethereum/application.properties" \
    --conf "spark.app_properties.sql_file=evm_prices/usd_minute.sql"