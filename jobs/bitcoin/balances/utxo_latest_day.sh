$CHAINSLAKE_RUN_DIR/chainslake-run.sh --class chainslake.sql.Main \
    --name BitcoinUTXOLatestDay \
    --conf "spark.app_properties.app_name=sql.transformer" \
    --conf "spark.app_properties.max_number_partition=4320" \
    --conf "spark.app_properties.max_time_run=1" \
    --conf "spark.app_properties.config_file=bitcoin/application.properties" \
    --conf "spark.app_properties.sql_file=bitcoin_balances/utxo_latest_day.sql"