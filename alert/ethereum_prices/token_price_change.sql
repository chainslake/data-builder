{
    "name": "Token price change",
    "table": "ethereum_prices.erc20_usd_hour",
    "description": "Alerting when price of token [${token symbol}](https://metabase.chainslake.io/public/dashboard/ac9dbee4-af29-4ba8-b494-eae69f4ee835?token_address=${token address}) on DEX changed more than ${percent}%",
    "dashboard_url": "https://metabase.chainslake.io/public/dashboard/ac9dbee4-af29-4ba8-b494-eae69f4ee835?token_address=${token address}",
    "transform_id": "ethereum_prices_percent_erc20_usd",
    "query": "token_address = ${token address} and abs(change_percent) > ${percent}",
    "message": "Price of token [${token_symbol}](https://metabase.chainslake.io/public/dashboard/ac9dbee4-af29-4ba8-b494-eae69f4ee835?token_address=${token_address}) on DEX changed ${change_percent}% to ${price} USD.",
    "query_parameter": [
        {
            "name": "token symbol",
            "type": "string",
            "value": "WETH"
        },
        {
            "name": "token address",
            "type": "string",
            "value": "0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2"
        },
    {
            "name": "percent",
            "type": "number",
            "value": "1"
        }
    ]
}

===

WITH
  join_previous_data AS (
    SELECT
      *,
      LAG(avg_price, 1) OVER (
        PARTITION BY
          token_address
        ORDER BY
          block_hour
      ) AS previous_price
    FROM
      ethereum_prices.erc20_usd_hour
    WHERE
      block_hour >= cast(${from} - 7200 AS timestamp)
      AND block_hour < cast(${to} AS timestamp)
  )
SELECT
  block_hour,
  token_address,
  token_symbol,
  avg_price as price,
  CASE
    WHEN previous_price IS NOT NULL THEN (avg_price - previous_price) / previous_price * 100
    ELSE 0
  END AS change_percent
FROM
  join_previous_data
WHERE
  block_hour >= cast(${from} AS timestamp)
  AND block_hour < cast(${to} AS timestamp)