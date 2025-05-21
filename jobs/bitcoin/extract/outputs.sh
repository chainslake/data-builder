$CHAINSLAKE_RUN_DIR/chainslake-run.sh --class chainslake.bitcoin.Main \
    --name BitcoinExtractOutputs \
    --conf "spark.app_properties.app_name=bitcoin.outputs" \
    --conf "spark.app_properties.rpc_list=$BITCOIN_RPCS" \
    --conf "spark.app_properties.config_file=bitcoin/application.properties"