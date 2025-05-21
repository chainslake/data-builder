$CHAINSLAKE_RUN_DIR/chainslake-run.sh --class chainslake.sql.Main \
    --name EthereumSwapV2Trades \
    --conf "spark.app_properties.app_name=sql.transformer" \
    --conf "spark.app_properties.config_file=ethereum/application.properties" \
    --conf "spark.app_properties.sql_file=evm_dex/swap_v2_trades.sql"