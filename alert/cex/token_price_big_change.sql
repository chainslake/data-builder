{
    "name": "#PRICE CEX big change token price",
    "table": "binance_cex.trade_minute_agg_volume",
    "description": "Alerting when any coin in binance has price increase more than ${percent}%",
    "dashboard_url": "https://metabase.chainslake.io/public/dashboard/169d39da-25a0-484d-ad51-53ef13eda9de",
    "query_parameter": [
    {
            "name": "percent",
            "type": "number",
            "value": "10"
        }
    ],
    "transform_id": "binance_cex_price",
    "query": "ratio_change_price >= ${percent} and minute_max > minute_min",
    "message": "Price of #${base_asset} increased ${ratio_change_price}%. https://metabase.chainslake.io/question/353-cex-token-price-big-change?coin=${base_asset})"
}

===

WITH
  trade_data AS (
    SELECT
      block_minute,
      base_asset,
      open_price,
      24h_volume
    FROM
      cex_binance.trade_minute_agg_volume
    WHERE
      block_minute > CURRENT_TIMESTAMP - interval '1' HOUR
  ),
  trade_filter AS (
    SELECT
      base_asset,
      max_by(24h_volume, open_price) - min_by(24h_volume, open_price) as change_volume,
      max_by(block_minute, open_price) AS minute_max,
      min_by(block_minute, open_price) AS minute_min,
      (max(open_price) - min(open_price)) / min(open_price) * 100 AS ratio_change_price
    FROM
      trade_data
    GROUP BY
      base_asset
  )
SELECT
  *
FROM
  trade_filter
WHERE
  minute_max >= cast(${from} AS timestamp)