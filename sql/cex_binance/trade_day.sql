frequent_type=day
list_input_tables=cex_binance.trade_minute
output_table=cex_binance.trade_day
re_partition_by_range=block_date
write_mode=Append
number_index_columns=2
partition_by=block_date

===

WITH
  coin_price AS (
    SELECT
      block_date,
      base_asset,
      min_by(open_price, block_minute) AS open_price,
      max_by(close_price, block_minute) AS close_price,
      sum(quote_volume) as volume
    FROM
      cex_binance.trade_minute
    WHERE
      block_date >= cast(${from} as timestamp)
      AND 
        block_date < cast(${to} as timestamp)
    GROUP BY
      block_date, base_asset
  )
SELECT
  block_date,
  base_asset,
  open_price,
  close_price,
  (close_price - open_price) / open_price * 100 AS increase_percent,
  volume
FROM
  coin_price