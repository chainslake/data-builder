$CHAINSLAKE_HOME_DIR/spark/script/chainslake-run.sh --class chainslake.Main \
    --name EthereumOriginLogs \
    --master local[4] \
    --driver-memory 4g \
    --conf "spark.app_properties.app_name=evm_origin.logs" \
    --conf "spark.app_properties.rpc_list=$ETHEREUM_RPCS" \
    --conf "spark.app_properties.config_file=ethereum/application.properties"