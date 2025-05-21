$CHAINSLAKE_RUN_DIR/chainslake-run.sh --class chainslake.solana.Main \
    --name SolanaExtractNativeBalances \
    --master local[2] \
    --driver-memory 10g \
    --conf "spark.app_properties.app_name=solana.native_balances" \
    --conf "spark.app_properties.config_file=solana/application.properties"