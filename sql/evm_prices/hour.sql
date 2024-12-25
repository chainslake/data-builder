frequent_type=hour
list_input_tables=${chain_name}_prices.${input_table_name}
output_table=${chain_name}_prices.${output_table_name}

re_partition_by_range=block_date,block_hour
partition_by=block_date
write_mode=Append

===

with price_with_hour as (
    select block_date
        , block_minute
        , date_trunc('hour', block_minute) as block_hour
        , token_address
        , token_symbol
        , token_volume
        , currency_volume
        , price
    from ${list_input_tables}
    where block_minute >= cast(${from} as timestamp)
    and block_minute < cast(${to} as timestamp)
)


, price_ohlc as (
    select block_date
        , block_hour
        , token_address
        , token_symbol
        , sum(token_volume) as token_volume
        , sum(currency_volume) as currency_volume
        , min_by(price, block_minute) as open_price
        , max(price) as high_price
        , min(price) as low_price
        , max_by(price, block_minute) as close_price
    from price_with_hour
    group by block_date, block_hour, token_address, token_symbol
)

select block_date
    , block_hour
    , current_timestamp() as updated_time
    , token_address
    , token_symbol
    , token_volume
    , currency_volume
    , open_price
    , high_price
    , low_price
    , close_price
    , currency_volume / token_volume as avg_price
from price_ohlc