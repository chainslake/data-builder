$CHAINSLAKE_HOME_DIR/spark/script/chainslake-run.sh --class chainslake.evm.Main \
    --name EthereumOriginTraces \
    --master local[4] \
    --driver-memory 8g \
    --conf "spark.app_properties.app_name=evm_origin.traces" \
    --conf "spark.app_properties.rpc_list=$ETHEREUM_RPCS" \
    --conf "spark.app_properties.config_file=ethereum/application.properties"