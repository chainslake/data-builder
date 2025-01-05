{
    "name": "Big trade of token on DEX",
    "table": "ethereum_dex.token_trades",
    "description": "Alerting when token [${token symbol}](https://metabase.chainslake.io/public/dashboard/ac9dbee4-af29-4ba8-b494-eae69f4ee835?token_address=${token address}) have a dex trade transaction with value greater than ${value} USD.",
    "dashboard_url": "https://metabase.chainslake.io/public/dashboard/e9e015f3-6a01-472c-a99a-2388d3cad7b4?token_address=${token address}&value=${value}",
    "transform_id": "ethereum_token_trades",
    "query": "token_address = ${token address} and volume > ${value}",
    "query_parameter": [
        {
            "name": "token symbol",
            "type": "string",
            "value": "USDT"
        },
        {
            "name": "token address",
            "type": "string",
            "value": "0xdac17f958d2ee523a2206206994597c13d831ec7"
        },
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