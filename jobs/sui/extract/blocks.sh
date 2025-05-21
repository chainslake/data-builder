$CHAINSLAKE_RUN_DIR/chainslake-run.sh --class chainslake.sui.Main \
    --name SuiExtractBlocks \
    --master local[4] \
    --driver-memory 4g \
    --conf "spark.app_properties.app_name=sui.blocks" \
    --conf "spark.app_properties.config_file=sui/application.properties"