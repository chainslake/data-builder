{
    "name": "#Balance of Wallets has big change",
    "table": "ethereum_balances.token_transfer_hour",
    "description": "Alerting when wallet ${name} balance changes greater than ${percent}%",
    "dashboard_url": "https://metabase.chainslake.io/public/dashboard/3c10be82-3e17-4312-ad0d-272b275a3a89",
    "query_parameter": [
        {
            "name": "name",
            "type": "string",
            "value": "Binance hot wallet"
        },
        {
            "name": "wallets",
            "type": "string",
            "value": "0xF977814e90dA44bFA03b6295A0616a897441aceC"
        },
        {
            "name": "percent",
            "type": "number",
            "value": "10"
        }
    ],
    "tranform_id": "ethereum balance ${name} change",
    "query": "ratio_change_volume >= ${percent}",
    "message": "Balance of $${symbol} in ${name} ${change_type} ${ratio_change_volume}%. [detail](https://metabase.chainslake.io/public/dashboard/3c10be82-3e17-4312-ad0d-272b275a3a89?wallet=${wallets}&token_address=${token_address})"
}

=== 

WITH
  wallets AS (
    SELECT
      EXPLODE (split(lower(${wallets}), ',')) AS wallet
  ),
  hour_balance_change AS (
    SELECT
      t.block_hour,
      t.wallet_address,
      t.token_address,
      t.symbol,
      sum(t.amount) OVER (
        PARTITION BY
          token_address, symbol
        ORDER BY
          t.block_hour
      ) AS cumulative_amount
    FROM
      ethereum_balances.token_transfer_hour t
      INNER JOIN wallets w ON (t.wallet_address = w.wallet)
    WHERE
      block_hour >= CURRENT_TIMESTAMP - interval '24' HOUR
  ),
  transfer_filter AS (
    SELECT
      token_address,
      symbol,
      max(cumulative_amount) - min(cumulative_amount) as delta_change,
      max_by(block_hour, cumulative_amount) AS hour_max,
      min_by(block_hour, cumulative_amount) AS hour_min,
      (max(cumulative_amount) - min(cumulative_amount)) / min(cumulative_amount) * 100 AS ratio_change_volume
    FROM
      hour_balance_change
    GROUP BY
      token_address, symbol
  )
SELECT
  t.*,
  CASE
    WHEN t.hour_max > t.hour_min THEN 'increase'
    ELSE 'decrease'
  END AS change_type
FROM
  transfer_filter t
WHERE
  t.hour_max >= cast(${from} AS timestamp)
  OR t.hour_min >= cast(${from} AS timestamp)