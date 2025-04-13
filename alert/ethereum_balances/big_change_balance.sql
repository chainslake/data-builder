{
    "name": "Big change balance of wallet in hour",
    "table": "ethereum_balances.token_transfer_hour",
    "description": "Alerting when [${wallet name}](https://metabase.chainslake.io/public/dashboard/d705fb02-38db-43d1-93fc-195a79718a71?token_address=${token address}&value=${amount}&wallet_address=${wallet address}) wallet change balance of [${token symbol}](https://metabase.chainslake.io/public/dashboard/ac9dbee4-af29-4ba8-b494-eae69f4ee835?token_address=${token address}) more than ${value} token in a hour",
    "dashboard_url": "https://metabase.chainslake.io/public/dashboard/d705fb02-38db-43d1-93fc-195a79718a71?token_address=${token address}&value=${value}&wallet_address=${wallet address}",
    "query_parameter": [
        {
            "name": "wallet name",
            "type": "string",
            "value": "any"
        },
        {
            "name": "wallet address",
            "type": "string",
            "value": "any"
        },
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
            "value": "100000000"
        }
    ],
    "transform_id": "ethereum_balance_token_transfer_hour",
    "query": "token_address = ${token address} and abs(amount) > ${value} and (${wallet address} = 'any' or wallet_address = ${wallet address})",
    "message": "[${wallet_address}](https://etherscan.io/address/${wallet_address}) changed balance of token [${token_symbol}](https://metabase.chainslake.io/public/dashboard/ac9dbee4-af29-4ba8-b494-eae69f4ee835?token_address=${token_address}) with amount ${amount} token. View [analyst](https://metabase.chainslake.io/public/dashboard/3c10be82-3e17-4312-ad0d-272b275a3a89?wallet=${wallet_address}&token_address=${token_address})"
}

===

select * from ethereum_balances.token_transfer_hour
where block_hour >= cast(${from} as timestamp)
and block_hour < cast(${to} as timestamp)