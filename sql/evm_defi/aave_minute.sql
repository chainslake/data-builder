frequent_type=minute
list_input_tables=${chain_name}_decoded.aave_evt_liquidationcall,${chain_name}_decoded.aave_evt_withdraw,${chain_name}_decoded.aave_v2_evt_borrow,${chain_name}_decoded.aave_v2_evt_deposit,${chain_name}_decoded.aave_v2_evt_flashloan,${chain_name}_decoded.aave_v2_evt_repay,${chain_name}_decoded.aave_v3_evt_borrow,${chain_name}_decoded.aave_v3_evt_flashloan,${chain_name}_decoded.aave_v3_evt_repay,${chain_name}_decoded.aave_v3_evt_supply,${chain_name}_prices.erc20_usd_minute,${chain_name}_contract.erc20_tokens
aave_evt_liquidationcall=${chain_name}_decoded.aave_evt_liquidationcall
aave_evt_withdraw=${chain_name}_decoded.aave_evt_withdraw
aave_v2_evt_borrow=${chain_name}_decoded.aave_v2_evt_borrow
aave_v2_evt_deposit=${chain_name}_decoded.aave_v2_evt_deposit
aave_v2_evt_flashloan=${chain_name}_decoded.aave_v2_evt_flashloan
aave_v2_evt_repay=${chain_name}_decoded.aave_v2_evt_repay
aave_v3_evt_borrow=${chain_name}_decoded.aave_v3_evt_borrow
aave_v3_evt_flashloan=${chain_name}_decoded.aave_v3_evt_flashloan
aave_v3_evt_repay=${chain_name}_decoded.aave_v3_evt_repay
aave_v3_evt_supply=${chain_name}_decoded.aave_v3_evt_supply
erc20_usd_minute=${chain_name}_prices.erc20_usd_minute
erc20_tokens=${chain_name}_contract.erc20_tokens
output_table=${chain_name}_defi.aave_minute
re_partition_by_range=block_date
write_mode=Append
number_index_columns=2
partition_by=block_date

===

with all_event as (
    select block_date
        , block_number
        , block_time
        , date_trunc('minute', block_time) as block_minute
        , contract_address
        , tx_hash
        , evt_index
        , 'deposit' as event_type
        , reserve as token_address
        , user as wallet_address
        , amount
        , 0 as apr 
    from ${aave_v2_evt_deposit}
    union all 
    select block_date
        , block_number
        , block_time
        , date_trunc('minute', block_time) as block_minute
        , contract_address
        , tx_hash
        , evt_index
        , 'deposit' as event_type
        , reserve as token_address
        , user as wallet_address
        , amount
        , 0 as apr 
    from ${aave_v3_evt_supply}
    union all
    select block_date
        , block_number
        , block_time
        , date_trunc('minute', block_time) as block_minute
        , contract_address
        , tx_hash
        , evt_index
        , 'borrow' as event_type
        , reserve as token_address
        , user as wallet_address
        , amount
        , borrowRate / pow(10,27) * 100 as apr
    from ${aave_v2_evt_borrow}
    union all
    select block_date
        , block_number
        , block_time
        , date_trunc('minute', block_time) as block_minute
        , contract_address
        , tx_hash
        , evt_index
        , 'borrow' as event_type
        , reserve as token_address
        , user as wallet_address
        , amount
        , borrowRate / pow(10,27) * 100 as apr
    from ${aave_v3_evt_borrow}
    union all
    select block_date
        , block_number
        , block_time
        , date_trunc('minute', block_time) as block_minute
        , contract_address
        , tx_hash
        , evt_index
        , 'repay' as event_type
        , reserve as token_address
        , user as wallet_address
        , amount
        , 0 as apr
    from ${aave_v2_evt_repay}
    union all
    select block_date
        , block_number
        , block_time
        , date_trunc('minute', block_time) as block_minute
        , contract_address
        , tx_hash
        , evt_index
        , 'repay' as event_type
        , reserve as token_address
        , user as wallet_address
        , amount
        , 0 as apr
    from ${aave_v3_evt_repay}
    union all
    select block_date
        , block_number
        , block_time
        , date_trunc('minute', block_time) as block_minute
        , contract_address
        , tx_hash
        , evt_index
        , 'withdraw' as event_type
        , reserve as token_address
        , user as wallet_address
        , amount
        , 0 as apr
    from ${aave_evt_withdraw}
    union all
    select block_date
        , block_number
        , block_time
        , date_trunc('minute', block_time) as block_minute
        , contract_address
        , tx_hash
        , evt_index
        , 'liquidation' as event_type
        , collateralAsset as token_address
        , user as wallet_address
        , liquidatedCollateralAmount as amount
        , 0 as apr
    from ${aave_evt_liquidationcall}
    union all
    select block_date
        , block_number
        , block_time
        , date_trunc('minute', block_time) as block_minute
        , contract_address
        , tx_hash
        , evt_index
        , 'flashloan' as event_type
        , asset as token_address
        , initiator as wallet_address
        , amount as amount
        , 0 as apr
    from ${aave_v2_evt_flashloan}
    union all
    select block_date
        , block_number
        , block_time
        , date_trunc('minute', block_time) as block_minute
        , contract_address
        , tx_hash
        , evt_index
        , 'flashloan' as event_type
        , asset as token_address
        , initiator as wallet_address
        , amount as amount
        , 0 as apr
    from ${aave_v3_evt_flashloan}
)
, event_with_symbol as (
    select event.block_date
        , event.block_number
        , event.block_time
        , event.block_minute
        , event.contract_address
        , event.tx_hash
        , event.evt_index
        , event.event_type
        , info.symbol
        , event.token_address
        , event.wallet_address
        , event.apr
        , case 
            when info.decimals is not null then event.amount / pow(10, info.decimals)
            else event.amount
        end as amount
    from all_event event
    left join ${erc20_tokens} info
    on event.token_address = info.contract_address
)
, erc20_price_by_usd as (
    select block_date
        , block_minute
        , lead(block_minute) over (partition by token_address order by block_minute) as next_minute
        , token_address
        , price
    from ${erc20_usd_minute}
    where block_minute >= cast(${from} as timestamp)
    and block_minute < cast(${to} as timestamp)
)
, event_with_price as (
    select event.block_date
        , event.block_number
        , event.block_time
        , event.block_minute
        , event.contract_address
        , event.tx_hash
        , event.evt_index
        , event.event_type
        , event.symbol
        , event.token_address
        , event.wallet_address
        , event.apr
        , event.amount
        , case 
            when minute_price.price is not null then minute_price.price
            else 0
        end as price
    from event_with_symbol event
    left join erc20_price_by_usd minute_price
    on (
        event.token_address = minute_price.token_address
        and event.block_minute >= minute_price.block_minute
        and (minute_price.next_minute is null or event.block_minute < minute_price.next_minute)
    )
)

select block_date
    , block_number
    , block_time
    , block_minute
    , contract_address
    , tx_hash
    , evt_index
    , event_type
    , symbol
    , token_address
    , wallet_address
    , apr
    , amount
    , price
    , amount * price as value
from event_with_price