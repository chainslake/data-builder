$CHAINSLAKE_HOME_DIR/spark/script/chainslake-run.sh --class chainslake.bitcoin.Main \
    --name BitcoinOriginTransactionBlocks \
    --master local[4] \
    --driver-memory 4g \
    --conf "spark.app_properties.app_name=bitcoin_origin.transaction_blocks" \
    --conf "spark.app_properties.rpc_list=$BITCOIN_RPCS" \
    --conf "spark.app_properties.config_file=bitcoin/application.properties"