{
    "name": "Distribution trade volume on DEX Ethereum",
    "table": "ethereum_prices.erc20_usd_day",
    "description": "Alert top token with biggest volume on DEX Ethereum",
    "dashboard_url": "",
    "query_parameter": [
    {
            "name": "limit",
            "type": "number",
            "value": "1"
        }
    ],
    "transform_id": "ethereum_prices_erc20_usd_day",
    "query": "1 = 1",
    "message": "Distribution of trading volume on #DEX #Ethereum https://metabase.chainslake.io/question/359-distribution-of-trading-volume-on-dex"
}

===

SELECT block_date FROM ethereum_prices.erc20_usd_day
WHERE block_date = CURRENT_DATE - interval '1' DAY
LIMIT 1