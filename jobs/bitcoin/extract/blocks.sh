$CHAINSLAKE_HOME_DIR/spark/script/chainslake-run.sh --class chainslake.bitcoin.Main \
    --name BitcoinExtractBlocks \
    --master local[4] \
    --driver-memory 6g \
    --conf "spark.app_properties.app_name=bitcoin.blocks" \
    --conf "spark.app_properties.rpc_list=$BITCOIN_RPCS" \
    --conf "spark.app_properties.config_file=bitcoin/application.properties"