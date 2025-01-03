$CHAINSLAKE_HOME_DIR/spark/script/chainslake-run.sh --class chainslake.solana.Main \
    --name SolanaExtractBlocks \
    --master local[2] \
    --driver-memory 10g \
    --conf "spark.app_properties.app_name=solana.blocks" \
    --conf "spark.app_properties.config_file=solana/application.properties"