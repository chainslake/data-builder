$CHAINSLAKE_RUN_DIR/chainslake-run.sh --class chainslake.evm.Main \
    --name EthereumOriginTraces \
    --conf "spark.app_properties.app_name=evm_origin.traces" \
    --conf "spark.app_properties.rpc_list=$ETHEREUM_RPCS" \
    --conf "spark.app_properties.config_file=ethereum/application.properties"