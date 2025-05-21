$CHAINSLAKE_RUN_DIR/chainslake-run.sh --class chainslake.sql.Main \
    --name BitcoinUTXOTransferHour \
    --conf "spark.app_properties.app_name=sql.transformer" \
    --conf "spark.app_properties.config_file=bitcoin/application.properties" \
    --conf "spark.app_properties.sql_file=bitcoin_balances/utxo_transfer_hour.sql"