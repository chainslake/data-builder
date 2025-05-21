{
    "name": "Top wallet received most BTC",
    "table": "bitcoin_balances.utxo_latest_day",
    "description": "Alert top wallet received most BTC",
    "dashboard_url": "",
    "query_parameter": [
    {
            "name": "limit",
            "type": "number",
            "value": "1"
        }
    ],
    "transform_id": "bitcoin_balances_utxo_transfer_day",
    "query": "1 = 1",
    "message": "Top wallets received most #Bitcoin https://metabase.chainslake.io/question/358-top-wallet-received-most-btc-in-last-day"
}

===

SELECT block_date FROM bitcoin_balances.utxo_transfer_day
WHERE block_date = CURRENT_DATE - interval '1' DAY
LIMIT 1