$CHAINSLAKE_HOME_DIR/spark/script/chainslake-run.sh --class chainslake.Main \
    --name EthereumDexTokenTrades \
    --master local[4] \
    --driver-memory 4g \
    --conf "spark.app_properties.app_name=sql.transformer" \
    --conf "spark.app_properties.input_table_name=$1" \
    --conf "spark.app_properties.currency_contracts=$2" \
    --conf "spark.app_properties.token_contracts=$3" \
    --conf "spark.app_properties.config_file=ethereum/application.properties" \
    --conf "spark.app_properties.sql_file=evm_dex/token_trades.sql"