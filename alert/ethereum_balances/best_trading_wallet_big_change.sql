{
    "name": "#Best trading wallets big change",
    "table": "ethereum_balances.token_transfer_hour",
    "description": "Alerting when best trading wallets balance changes with value more than ${volume} usd",
    "dashboard_url": "https://metabase.chainslake.io/public/dashboard/606fe645-eddc-4a6f-bedd-410664d0ee9b",
    "query_parameter": [
        {
            "name": "volume",
            "type": "number",
            "value": "10000000"
        }
    ],
    "transform_id": "ethereum best trading wallets change",
    "query": "usd_amount >= ${volume}",
    "message": "Best trading wallets #${action} ${amount} $${symbol} with value ${usd_amount}. [detail](https://metabase.chainslake.io/public/dashboard/606fe645-eddc-4a6f-bedd-410664d0ee9b)"
}

===

WITH wallet_change_balance AS (
    SELECT
      coin.symbol,
      sum(balance.amount) as amount
    FROM
      ethereum_balances.token_transfer_hour balance
      INNER JOIN binance_cex.coin_token_address coin ON (balance.token_address = coin.token_address)
      INNER JOIN ethereum_balances.best_trading_wallets wallet ON (balance.wallet_address = wallet.wallet_address)
    WHERE
      block_date = CURRENT_DATE
      and balance.amount != 0
    group by coin.symbol
),
  
coin_price_day AS (
    SELECT
      base_asset,
      min(open_price) AS min_price
    FROM
      binance_cex.trade_minute
    WHERE
      block_date = CURRENT_DATE
    GROUP BY
      base_asset
),
  
balance_change_with_value AS (
    SELECT
      balance.symbol,
      abs(balance.amount) as amount,
      case 
        when balance.amount > 0 then 'received'
        else 'sent'
      end as action,
      abs(balance.amount * price.min_price) as usd_amount
    FROM
      wallet_change_balance balance
      INNER JOIN coin_price_day price ON (
        balance.symbol = price.base_asset
      )
)
select * from balance_change_with_value