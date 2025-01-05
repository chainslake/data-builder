{
    "name": "Wallet execute a transaction",
    "table": "ethereum.transactions",
    "description": "Alerting when wallet [${name}](https://etherscan.io/address/${wallet}) execute a transaction.",
    "dashboard_url": "https://metabase.chainslake.io/public/dashboard/7b12d704-333b-40a4-9042-c887a0b45c8a?wallet=${wallet}",
    "transform_id": "ethereum_transactions",
    "query": "from = ${wallet}",
    "message": "[${name}](https://etherscan.io/address/${from}) have executed txn [${hash}](https://etherscan.io/tx/${hash})",
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

select `from`, hash from ethereum.transactions
where block_number >= ${from}
and block_number <= ${to}