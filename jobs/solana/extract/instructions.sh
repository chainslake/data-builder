$CHAINSLAKE_RUN_DIR/chainslake-run.sh --class chainslake.solana.Main \
    --name SolanaExtractInstructions \
    --master local[2] \
    --driver-memory 10g \
    --conf "spark.app_properties.app_name=solana.instructions" \
    --conf "spark.app_properties.config_file=solana/application.properties"