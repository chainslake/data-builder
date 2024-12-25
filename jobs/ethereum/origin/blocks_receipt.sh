$CHAINSLAKE_HOME_DIR/spark/script/chainslake-run.sh --class chainslake.Main \
    --name EthereumOriginBlocksReceipt \
    --master local[4] \
    --driver-memory 4g \
    --conf "spark.app_properties.app_name=evm_origin.blocks_receipt" \
    --conf "spark.app_properties.rpc_list=$ETHEREUM_RPCS" \
    --conf "spark.app_properties.config_file=ethereum/application.properties"