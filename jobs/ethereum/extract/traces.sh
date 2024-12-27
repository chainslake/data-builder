$CHAINSLAKE_HOME_DIR/spark/script/chainslake-run.sh --class chainslake.evm.Main \
    --name EthereumLogs \
    --master local[1] \
    --driver-memory 4g \
    --conf "spark.app_properties.app_name=evm.traces" \
    --conf "spark.app_properties.rpc_list=$ETHEREUM_RPCS" \
    --conf "spark.app_properties.config_file=ethereum/application.properties"