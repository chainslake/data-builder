$CHAINSLAKE_HOME_DIR/spark/script/chainslake-run.sh --class chainslake.sql.Main \
    --name BitcoinUTXOTransferHour \
    --master local[4] \
    --driver-memory 4g \
    --conf "spark.app_properties.app_name=sql.transformer" \
    --conf "spark.app_properties.config_file=bitcoin/application.properties" \
    --conf "spark.app_properties.sql_file=bitcoin_balances/utxo_transfer_hour.sql"