{
    "name": "Wallet trade on DEX",
    "table": "ethereum_dex.token_trades",
    "description": "Alerting when wallet [${name}](https://etherscan.io/address/${wallet}) has DEX trade transaction.",
    "dashboard_url": "https://metabase.chainslake.io/public/dashboard/64a35b47-810e-4d5d-bf87-00a4f1bdaba4?wallet=${wallet}",
    "transform_id": "ethereum_token_trades",
    "query": "taker=${wallet}",
    "message": "[${taker}](https://etherscan.io/address/${taker}) ${trade_type} ${token_value} [${token_symbol}](https://metabase.chainslake.io/public/dashboard/ac9dbee4-af29-4ba8-b494-eae69f4ee835?token_address=${token_address}) with value ${volume} USD at txn [${tx_hash}](https://etherscan.io/tx/${tx_hash}). View [analyst](https://metabase.chainslake.io/public/dashboard/3c10be82-3e17-4312-ad0d-272b275a3a89?wallet=${taker}&token_address=${token_address})",
    "query_parameter": [
        {
            "name": "name",
            "type": "string",
            "value": "vitalik"
        },
        {
            "name": "wallet",
            "type": "string",
            "value": "0xd8da6bf26964af9d7eed9e03e53415d37aa96045"
        }
    ]
}

===

select * from ethereum_dex.token_trades
where block_minute >= cast(${from} as timestamp)
and block_minute < cast(${to} as timestamp)