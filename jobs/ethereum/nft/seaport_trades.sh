$CHAINSLAKE_RUN_DIR/chainslake-run.sh --class chainslake.sql.Main \
    --name EthereumSeaportTrades \
    --conf "spark.app_properties.app_name=sql.transformer" \
    --conf "spark.app_properties.native_coin=ETH" \
    --conf "spark.app_properties.wrap_native_address=0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2" \
    --conf "spark.app_properties.config_file=ethereum/application.properties" \
    --conf "spark.app_properties.sql_file=evm_nft/seaport_trades.sql"