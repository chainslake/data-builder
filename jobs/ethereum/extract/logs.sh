$CHAINSLAKE_HOME_DIR/spark/script/chainslake-run.sh --class chainslake.Main \
    --name EthereumLogs \
    --master local[2] \
    --driver-memory 4g \
    --conf "spark.app_properties.app_name=evm.logs" \
    --conf "spark.app_properties.rpc_list=$ETHEREUM_RPCS" \
    --conf "spark.app_properties.input_extract_logs_table=blocks_receipt" \
    --conf "spark.app_properties.config_file=ethereum/application.properties"