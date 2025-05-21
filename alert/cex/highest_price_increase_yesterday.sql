{
    "name": "#PRICE Highest price increase yesterday",
    "table": "cex_binance.trade_day",
    "description": "Alerting daily highest price increase",
    "dashboard_url": "",
    "query_parameter": [
    {
            "name": "limit",
            "type": "number",
            "value": "1"
        }
    ],
    "transform_id": "cex_binance_trade_day",
    "query": "1 = 1",
    "message": "Price of #${base_asset} increased ${increase_percent}% https://metabase.chainslake.io/question/357-price-trading-volume?coin=${base_asset}"
}

===

SELECT base_asset, increase_percent FROM cex_binance.trade_day
WHERE block_date = CURRENT_DATE - interval '1' DAY
ORDER BY increase_percent DESC
LIMIT 1