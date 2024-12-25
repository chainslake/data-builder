frequent_type=minute
list_input_tables=${chain_name}_prices.${price_erc20_table_name},${chain_name}_prices.${price_wrap_native_table_name}

output_table=${chain_name}_prices.erc20_usd_minute
erc20_price_table=${chain_name}_prices.${price_erc20_table_name}
wrap_native_price_table=${chain_name}_prices.${price_wrap_native_table_name}

re_partition_by_range=block_date,block_minute
partition_by=block_date
write_mode=Append

===

with erc20_by_native as (
    select block_date
        , current_timestamp() as updated_time
        , block_minute
        , token_address
        , token_symbol
        , token_volume
        , currency_volume
        , price
    from ${erc20_price_table}
    where block_minute >= cast(${from} as timestamp)
    and block_minute < cast(${to} as timestamp)
)
, wrap_native_by_usd as (
    select block_date
        , current_timestamp() as updated_time
        , block_minute
        , lead(block_minute) over (order by block_minute) as next_minute
        , token_address
        , token_symbol
        , token_volume
        , currency_volume
        , price
    from ${wrap_native_price_table}
    where block_minute >= cast(${from} as timestamp)
    and block_minute < cast(${to} as timestamp)
)

, erc20_by_usd as (
    select erc20.block_date
        , erc20.updated_time
        , erc20.block_minute
        , erc20.token_address
        , erc20.token_symbol
        , erc20.token_volume
        , erc20.currency_volume * wn.price as currency_volume
        , erc20.price * wn.price as price
    from erc20_by_native erc20
    inner join wrap_native_by_usd wn
    on (
        erc20.block_minute >= wn.block_minute
        and
        (wn.next_minute is null or erc20.block_minute < wn.next_minute)
    )
)

select block_date
    , updated_time
    , block_minute
    , token_address
    , token_symbol
    , token_volume
    , currency_volume
    , price
    from wrap_native_by_usd
union all
select * from erc20_by_usd