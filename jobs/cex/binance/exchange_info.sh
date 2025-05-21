$CHAINSLAKE_RUN_DIR/chainslake-run.sh --class chainslake.cex.Main \
    --name BinanceExchangeInfo \
    --conf "spark.app_properties.app_name=cex_binance.exchange_info" \
    --conf "spark.app_properties.max_time_run=1" \
    --conf "spark.app_properties.binance_cex_url=$BINANCE_CEX_URL" \
    --conf "spark.app_properties.config_file=cex/application.properties"