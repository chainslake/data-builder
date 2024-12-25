frequent_type=hour
list_input_tables=${chain_name}_decoded.erc20_evt_transfer,${chain_name}.traces,${chain_name}.transactions,${chain_name}_contract.erc20_tokens
output_table=${chain_name}_balances.token_transfer_hour
erc20_event_table=${chain_name}_decoded.erc20_evt_transfer
erc20_token_table=${chain_name}_contract.erc20_tokens
transactions_table=${chain_name}.transactions
traces_table=${chain_name}.traces
re_partition_by_range=block_date,key_partition
partition_by=block_date
write_mode=Append
number_index_columns=7

===

with erc20_transfer_tx as (
    select block_date
        , date_trunc('hour', block_time) as block_hour
        , `from`
        , to
        , contract_address as token_address
        , cast(value as double) as value
        from ${erc20_event_table}
        where block_time >= cast(${from} as timestamp)
        and block_time < cast(${to} as timestamp)
),

erc20_transfer_wallet as (
    select block_date
        , block_hour
        , `from` as wallet_address
        , token_address
        , -1 * value as value
    from erc20_transfer_tx
    union all
    select block_date
        , block_hour
        , to as wallet_address
        , token_address
        , value
    from erc20_transfer_tx
)

, erc20_wallet_hour as (
    select block_date
        , block_hour
        , wallet_address
        , token_address
        , sum(value) as value
    from erc20_transfer_wallet
    group by block_date, block_hour, wallet_address, token_address
)

, erc20_wallet_hour_token as (
    select block_date
        , block_hour
        , concat(
            substring(token_address, 1, 4),
            substring(wallet_address, 1, 5)
        ) AS key_partition
        , wallet_address
        , token_address
        , symbol
        , value / pow(10, decimals) as amount
    from erc20_wallet_hour
    inner join ${erc20_token_table}
    on token_address = contract_address
    where value != 0
)

, native_transfer_tx as (
    select block_date
        , date_trunc('hour', block_time) as block_hour
        , `from`
        , to
        , cast(value as double) as value
    from ${traces_table}
    where block_time >= cast(${from} as timestamp)
    and block_time < cast(${to} as timestamp)
    and success = true
    and `type` = 'call'

)

, native_transfer_wallet as (
    select block_date
        , block_hour
        , `from` as wallet_address
        , -1 * value as value
    from native_transfer_tx
    union all 
    select block_date
        , block_hour
        , to as wallet_address
        , value
    from native_transfer_tx
    union all
    select block_date
        , date_trunc('hour', block_time) as block_hour
        , `from` as wallet_address
        , -1 * gas_used * gas_price as value
    from ${transactions_table}
    where block_time >= cast(${from} as timestamp)
    and block_time < cast(${to} as timestamp)
)

, native_wallet_hour as (
    select block_date
        , block_hour
        , wallet_address
        , sum(value) as value
    from native_transfer_wallet
    group by block_date, block_hour, wallet_address
)

, native_wallet_hour_token as (
    select block_date
        , block_hour
        , concat('0x', substring(wallet_address, 1, 5)) AS key_partition
        , wallet_address
        , '0x' as token_address
        , '${native_symbol}' as symbol
        , value / pow(10, 18) as amount
    from native_wallet_hour
    where value != 0
)

select * from erc20_wallet_hour_token
union all 
select * from native_wallet_hour_token

