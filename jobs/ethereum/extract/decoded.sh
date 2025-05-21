$CHAINSLAKE_RUN_DIR/chainslake-run.sh --class chainslake.evm.Main \
    --name EthereumDecoded \
    --conf "spark.app_properties.app_name=evm.decoded" \
    --conf "spark.app_properties.protocol_name=$1" \
    --conf "spark.app_properties.config_file=ethereum/application.properties" \
    --conf "spark.app_properties.abi_file=$1.json"