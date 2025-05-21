$CHAINSLAKE_RUN_DIR/chainslake-run.sh --class chainslake.bitcoin.Main \
    --name BitcoinExtractTransactions \
    --conf "spark.app_properties.app_name=bitcoin.transactions" \
    --conf "spark.app_properties.rpc_list=$BITCOIN_RPCS" \
    --conf "spark.app_properties.config_file=bitcoin/application.properties"