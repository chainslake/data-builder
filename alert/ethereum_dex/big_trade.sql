{
    "name": "Big trade on DEX",
    "table": "ethereum_dex.token_trades",
    "description": "Alerting when have dex trade transactions with value greater than ${value} USD.",
    "dashboard_url": "https://metabase.chainslake.io/public/dashboard/9e0bbb07-1772-455f-9aae-8ef337dc3051?value=${value}",
    "query": "volume > ${value}",
    "transform_id": "ethereum_token_trades",
    "message": "message=[${taker}](https://etherscan.io/address/${taker}) ${trade_type} ${token_value} [${token_symbol}](https://metabase.chainslake.io/public/dashboard/ac9dbee4-af29-4ba8-b494-eae69f4ee835?token_address=${token_address}) with value ${volume} USD at txn [${tx_hash}](https://metabase.chainslake.io/public/dashboard/0d9984c5-7903-4ba6-8a18-012dc1674150?block_date=${block_date}&tx_hash=${tx_hash}&token_address=${token_address}). View [analyst](https://metabase.chainslake.io/public/dashboard/3c10be82-3e17-4312-ad0d-272b275a3a89?wallet=${taker}&token_address=${token_address})",
    "query_parameter": [
        {
            "name": "value",
            "type": "number",
            "value": "1000000"
        }
    ]
}

===

select * from ethereum_dex.token_trades
where block_minute >= cast(${from} as timestamp)
and block_minute < cast(${to} as timestamp)