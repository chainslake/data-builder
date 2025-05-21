$CHAINSLAKE_RUN_DIR/chainslake-run.sh --class chainslake.bitcoin.Main \
    --name BitcoinExtractInputs \
    --conf "spark.app_properties.app_name=bitcoin.inputs" \
    --conf "spark.app_properties.rpc_list=$BITCOIN_RPCS" \
    --conf "spark.app_properties.config_file=bitcoin/application.properties"