$CHAINSLAKE_HOME_DIR/spark/script/chainslake-run.sh --class chainslake.solana.Main \
    --name SolanaExtractTokenbalances \
    --master local[2] \
    --driver-memory 10g \
    --conf "spark.app_properties.app_name=solana.token_balances" \
    --conf "spark.app_properties.config_file=solana/application.properties"