$CHAINSLAKE_HOME_DIR/spark/script/chainslake-run.sh --class chainslake.solana.Main \
    --name SolanaOriginTransactionBlocks \
    --master local[20] \
    --driver-memory 10g \
    --conf "spark.app_properties.app_name=solana_origin.transaction_blocks" \
    --conf "spark.app_properties.rpc_list=$SOLANA_RPCS" \
    --conf "spark.app_properties.config_file=solana/application.properties"