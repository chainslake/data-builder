$CHAINSLAKE_HOME_DIR/spark/script/chainslake-run.sh --class chainslake.bitcoin.Main \
    --name BitcoinExtractOutputs \
    --master local[4] \
    --driver-memory 4g \
    --conf "spark.app_properties.app_name=bitcoin.outputs" \
    --conf "spark.app_properties.config_file=bitcoin/application.properties"