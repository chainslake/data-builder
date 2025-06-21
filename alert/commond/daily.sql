{
    "name": "${table} daily alert",
    "table": "${table}",
    "description": "Alerting daily on ${table}",
    "dashboard_url": "",
    "query_parameter": [
        {
            "name": "table",
            "type": "string",
            "value": "cex_binance.trade_day"
        },
        {
            "name": "message",
            "type": "string",
            "value": "Table has update today"
        }
    ],
    "transform_id": "${table}_daily_alert",
    "query": "1 = 1",
    "message": "${message}"
}

===

SELECT block_date FROM ${table}
WHERE block_date = CURRENT_DATE - interval '1' DAY
LIMIT 1