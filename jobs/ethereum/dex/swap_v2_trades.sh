$CHAINSLAKE_HOME_DIR/spark/script/chainslake-run.sh --class chainslake.Main \
    --name EthereumSwapV2Trades \
    --master local[4] \
    --driver-memory 4g \
    --conf "spark.app_properties.app_name=sql.transformer" \
    --conf "spark.app_properties.config_file=ethereum/application.properties" \
    --conf "spark.app_properties.sql_file=evm_dex/swap_v2_trades.sql"