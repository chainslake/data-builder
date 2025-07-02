$CHAINSLAKE_RUN_DIR/chainslake-run.sh --class chainslake.evm.Main \
    --name EthereumContractInfo \
    --conf "spark.app_properties.app_name=evm.contract_info" \
    --conf "spark.app_properties.rpc_list=$ETHEREUM_RPCS" \
    --conf "spark.app_properties.max_retry=3" \
    --conf "spark.app_properties.config_file=ethereum/application.properties" \
    --conf "spark.app_properties.contract_info_file=$1.conf"