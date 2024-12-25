frequent_type=minute
list_input_tables=${chain_name}_prices.${input_table_name}
output_table=${chain_name}_dex.token_trades
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
        , tx_hash
        , taker
        , pair_contract
        , trade_type
        , token_address
        , token_symbol
        , token_value
        , currency_value
    from ${trade_v2_table}
    where block_time >= cast(${from} as timestamp)
    and block_time < cast(${to} as timestamp)
    and currency_address in ('${currency_contracts}')
    and (
       '$token_contracts' = 'all' or token_address in ('${token_contracts}')
    )
    union all
    select block_date
        , block_time
        , date_trunc('minute', block_time) as block_minute
        , tx_hash
        , taker
        , pair_contract
        , trade_type
        , token_address
        , token_symbol
        , token_value
        , currency_value
    from ${trade_v3_table}
    where block_time >= cast(${from} as timestamp)
    and block_time < cast(${to} as timestamp)
    and currency_address in ('${currency_contracts}')
    and (
       '$token_contracts' = 'all' or token_address in ('${token_contracts}')
    )
)

, wrap_token_prices as (
    select block_date
    , block_minute
    , token_address
    , token_symbol
    , price
 from ${list_input_tables}
 where block_minute >= cast(${from} as timestamp)
    and block_minute < cast(${to} as timestamp)
)

select d.block_date
    , d.block_minute
    , d.block_time
    , d.tx_hash
    , d.taker
    , d.pair_contract
    , d.trade_type
    , d.token_address
    , d.token_symbol
    , d.token_value
    , d.currency_value as wrap_native_value
    , p.price as wrap_native_price
    , d.currency_value * p.price as volume
    , (d.currency_value * p.price) / d.token_value as token_price
from uniswap_dex d
left join wrap_token_prices p
on (
   d.block_date = p.block_date
   and d.block_minute = p.block_minute
)