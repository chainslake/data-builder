{
    "name": "#VOLUME CEX token volume big change",
    "table": "binance_cex.trade_minute_agg_volume",
    "description": "Alerting when any coin in binance has volume change more than ${percent}%",
    "dashboard_url": "https://metabase.chainslake.io/public/dashboard/169d39da-25a0-484d-ad51-53ef13eda9de",
    "query_parameter": [
    {
            "name": "percent",
            "type": "number",
            "value": "10"
        }
    ],
    "transform_id": "binance_cex_volume",
    "query": "ratio_change_volume >= ${percent}",
    "message": "Volume of $${base_asset} ${change_type} ${ratio_change_volume} % with volume change ${change_volume} USD. [detail](https://metabase.chainslake.io/public/dashboard/13b762f3-91c3-40ec-b6c6-53c0775ceb12?coin=${base_asset})"
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
      binance_cex.trade_minute_agg_volume
    WHERE
      block_minute > CURRENT_TIMESTAMP - interval '15' MINUTE
  ),
  trade_filter AS (
    SELECT
      base_asset,
      max(24h_volume) - min(24h_volume) as change_volume,
      max_by(block_minute, 24h_volume) AS minute_max,
      min_by(block_minute, 24h_volume) AS minute_min,
      (max(24h_volume) - min(24h_volume)) / min(24h_volume) * 100 AS ratio_change_volume
    FROM
      trade_data
    GROUP BY
      base_asset
  )
SELECT
  *, case
       when minute_max > minute_min then 'increase'
       else 'decrease'
    end as change_type
FROM
  trade_filter
WHERE
  minute_max >= cast(${from} AS timestamp)
or minute_min >= cast(${from} AS timestamp)