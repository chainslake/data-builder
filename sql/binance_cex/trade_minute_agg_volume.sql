frequent_type=minute
list_input_tables=binance_cex.trade_minute
output_table=binance_cex.trade_minute_agg_volume
re_partition_by_range=block_date,block_minute
write_mode=Append
number_index_columns=6
partition_by=block_date

===

with trade_minute_agg_volume as (
  select block_date, block_minute, symbol, base_asset, open_price, quote_volume
    , sum(quote_volume) over (PARTITION by symbol order by block_minute ROWS 1440 PRECEDING) as 24h_volume
    , sum(volume) over (PARTITION by symbol order by block_minute ROWS 1440 PRECEDING) as 24h_coin_volume
  from ${list_input_tables}
  where block_minute >= cast((${from} - 86520) as timestamp)
    and block_minute < cast(${to} as timestamp)
)
select *, 24h_volume / 24h_coin_volume as 24h_avg_price from trade_minute_agg_volume
where block_minute >= cast(${from} as timestamp)