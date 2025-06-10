{
    "name": "Top wallet received most ETH",
    "table": "ethereum_balances.token_latest_day",
    "description": "Alert top wallet received most ETH",
    "dashboard_url": "",
    "query_parameter": [
    {
            "name": "limit",
            "type": "number",
            "value": "1"
        }
    ],
    "transform_id": "ethereum_balances.token_transfer_day",
    "query": "1 = 1",
    "message": "Top wallets received most #ETH https://metabase.chainslake.io/question/360-top-wallet-received-most-eth-in-last-day"
}

===

SELECT block_date FROM ethereum_balances.token_transfer_day
WHERE block_date = CURRENT_DATE - interval '1' DAY
LIMIT 1