$CHAINSLAKE_HOME_DIR/spark/script/chainslake-run.sh --class chainslake.cex.Main \
    --name BinanceTradeMinute \
    --master local[4] \
    --driver-memory 4g \
    --conf "spark.app_properties.app_name=binance_cex.trade_minute" \
    --conf "spark.app_properties.binance_cex_url=$BINANCE_CEX_URL" \
    --conf "spark.app_properties.number_re_partitions=4" \
    --conf "spark.app_properties.quote_asset=USDT" \
    --conf "spark.app_properties.wait_milliseconds=1" \
    --conf "spark.app_properties.config_file=binance/application.properties"