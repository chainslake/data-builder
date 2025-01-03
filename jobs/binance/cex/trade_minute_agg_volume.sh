$CHAINSLAKE_HOME_DIR/spark/script/chainslake-run.sh --class chainslake.sql.Main \
    --name BinanceCexAggVolume \
    --master local[4] \
    --driver-memory 2g \
    --conf "spark.app_properties.app_name=sql.transformer" \
    --conf "spark.app_properties.config_file=binance/application.properties" \
    --conf "spark.app_properties.sql_file=binance_cex/trade_minute_agg_volume.sql"