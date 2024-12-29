$CHAINSLAKE_HOME_DIR/spark/script/chainslake-run.sh --class chainslake.bitcoin.Main \
    --name BitcoinExtractInputs \
    --master local[4] \
    --driver-memory 4g \
    --conf "spark.app_properties.app_name=bitcoin.inputs" \
    --conf "spark.app_properties.config_file=bitcoin/application.properties"