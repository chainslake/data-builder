$CHAINSLAKE_RUN_DIR/chainslake-run.sh --class chainslake.sui.Main \
    --name SuiExtractEvents \
    --master local[4] \
    --driver-memory 4g \
    --conf "spark.app_properties.app_name=sui.events" \
    --conf "spark.app_properties.rpc_list=$SUI_RPCS" \
    --conf "spark.app_properties.config_file=sui/application.properties"