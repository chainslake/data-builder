frequent_type=minute
list_input_tables=${chain_name}_dex.uniswap_v2_trades,${chain_name}_dex.uniswap_v3_trades
output_table=${chain_name}_prices.${output_table_name}
trade_v2_table=${chain_name}_dex.uniswap_v2_trades
trade_v3_table=${chain_name}_dex.uniswap_v3_trades

re_partition_by_range=block_date,block_minute
partition_by=block_date
write_mode=Append

===

with uniswap_dex as (
    select block_date
        , block_time
        , date_trunc('minute', block_time) as block_minute
        , token_address
        , token_symbol
        , token_value
        , currency_value
    from ${trade_v2_table}
    where block_time >= cast(${from} as timestamp)
    and block_time < cast(${to} as timestamp)
    and currency_address in ('${currency_contracts}')
    and (
       '${token_contracts}' = 'all' or token_address in ('${token_contracts}')
    )
    union all
    select block_date
        , block_time
        , date_trunc('minute', block_time) as block_minute
        , token_address
        , token_symbol
        , token_value
        , currency_value
    from ${trade_v3_table}
    where block_time >= cast(${from} as timestamp)
    and block_time < cast(${to} as timestamp)
    and currency_address in ('${currency_contracts}')
    and (
       '${token_contracts}' = 'all' or token_address in ('${token_contracts}')
    )
)

, amount_by_minute as (
    select block_date
    , block_minute
    , token_address
    , token_symbol
    , sum(token_value) as token_volume
    , sum(currency_value) as currency_volume
 from uniswap_dex
 group by block_date, block_minute, token_address, token_symbol
)

select block_date
    , block_minute
    , current_timestamp() as updated_time
    , token_address
    , token_symbol
    , token_volume
    , currency_volume
    , currency_volume / token_volume as price
from amount_by_minute