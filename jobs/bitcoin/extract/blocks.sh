$CHAINSLAKE_RUN_DIR/chainslake-run.sh --class chainslake.bitcoin.Main \
    --name BitcoinExtractBlocks \
    --conf "spark.app_properties.app_name=bitcoin.blocks" \
    --conf "spark.app_properties.rpc_list=$BITCOIN_RPCS" \
    --conf "spark.app_properties.config_file=bitcoin/application.properties"