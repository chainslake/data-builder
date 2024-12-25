$CHAINSLAKE_HOME_DIR/spark/script/chainslake-run.sh --class chainslake.Main \
    --name EthereumDecoded \
    --master local[4] \
    --driver-memory 4g \
    --conf "spark.app_properties.app_name=evm.decoded" \
    --conf "spark.app_properties.protocol_name=$1" \
    --conf "spark.app_properties.config_file=ethereum/application.properties" \
    --conf "spark.app_properties.abi_file=$1.json"