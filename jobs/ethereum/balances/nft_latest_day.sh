$CHAINSLAKE_RUN_DIR/chainslake-run.sh --class chainslake.sql.Main \
    --name EthereumNFTLatestDay \
    --conf "spark.app_properties.app_name=sql.transformer" \
    --conf "spark.app_properties.max_number_partition=4320" \
    --conf "spark.app_properties.max_time_run=1" \
    --conf "spark.app_properties.run_mode=backward" \
    --conf "spark.app_properties.config_file=ethereum/application.properties" \
    --conf "spark.app_properties.sql_file=evm_balances/nft_latest_day.sql"