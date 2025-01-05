$CHAINSLAKE_HOME_DIR/spark/script/chainslake-run.sh --class chainslake.sui.Main \
    --name SuiOriginTransactionBlocks \
    --master local[20] \
    --driver-memory 8g \
    --conf "spark.app_properties.app_name=sui_origin.transaction_blocks" \
    --conf "spark.app_properties.rpc_list=$SUI_RPCS" \
    --conf "spark.app_properties.config_file=sui/application.properties"