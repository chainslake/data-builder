$CHAINSLAKE_RUN_DIR/chainslake-run.sh --class chainslake.sql.Main \
    --name BinanceCexAggVolume \
    --conf "spark.app_properties.app_name=sql.transformer" \
    --conf "spark.app_properties.config_file=cex/application.properties" \
    --conf "spark.app_properties.sql_file=cex_binance/trade_minute_agg_volume.sql"