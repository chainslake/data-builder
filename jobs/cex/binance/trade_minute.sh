$CHAINSLAKE_RUN_DIR/chainslake-run.sh --class chainslake.cex.Main \
    --name BinanceTradeMinute \
    --conf "spark.app_properties.app_name=cex_binance.trade_minute" \
    --conf "spark.app_properties.binance_cex_url=$BINANCE_CEX_URL" \
    --conf "spark.app_properties.number_re_partitions=4" \
    --conf "spark.app_properties.max_number_partition=16" \
    --conf "spark.app_properties.quote_asset=USDT" \
    --conf "spark.app_properties.wait_milliseconds=1" \
    --conf "spark.app_properties.config_file=cex/application.properties"