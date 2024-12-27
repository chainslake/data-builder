$CHAINSLAKE_HOME_DIR/spark/script/chainslake-run.sh --class chainslake.cex.Main \
    --name BinanceExchangeInfo \
    --master local[4] \
    --driver-memory 4g \
    --conf "spark.app_properties.app_name=binance_cex.exchange_info" \
    --conf "spark.app_properties.binance_cex_url=$BINANCE_CEX_URL" \
    --conf "spark.app_properties.config_file=binance/application.properties"