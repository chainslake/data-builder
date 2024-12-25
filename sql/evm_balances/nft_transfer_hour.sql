frequent_type=hour
list_input_tables=${chain_name}_decoded.erc721_evt_transfer,${chain_name}_contract.erc721_tokens,${chain_name}_decoded.erc1155_evt_transferbatch,${chain_name}_decoded.erc1155_evt_transfersingle,${chain_name}_contract.erc1155_tokens
output_table=${chain_name}_balances.nft_transfer_hour
erc721_event_table=${chain_name}_decoded.erc721_evt_transfer
erc721_token_table=${chain_name}_contract.erc721_tokens
erc1155_tranfersingle_table=${chain_name}_decoded.erc1155_evt_transfersingle
erc1155_tranferbatch_table=${chain_name}_decoded.erc1155_evt_transferbatch
erc1155_token_table=${chain_name}_contract.erc1155_tokens
re_partition_by_range=block_date,key_partition
partition_by=block_date
write_mode=Append
number_index_columns=7

===

with erc721_transfer_tx as (
    select block_date
        , date_trunc('hour', block_time) as block_hour
        , `from`
        , to
        , contract_address as token_address
        , tokenid as token_id
        from ${erc721_event_table}
        where block_time >= cast(${from} as timestamp)
        and block_time < cast(${to} as timestamp)
),

erc721_transfer_wallet as (
    select block_date
        , block_hour
        , `from` as wallet_address
        , token_address
        , token_id
        , -1 as value
    from erc721_transfer_tx
    union all
    select block_date
        , block_hour
        , to as wallet_address
        , token_address
        , token_id
        , 1 as value
    from erc721_transfer_tx
)

, erc721_wallet_hour as (
    select block_date
        , block_hour
        , wallet_address
        , token_address
        , token_id
        , sum(value) as amount
    from erc721_transfer_wallet
    group by block_date, block_hour, wallet_address, token_address, token_id
)

, erc721_wallet_hour_token as (
    select block_date
        , block_hour
        , concat(
            substring(token_address, 1, 3),
            substring(wallet_address, 1, 3)
        ) AS key_partition
        , 'ERC721' as nft_type
        , wallet_address
        , token_address
        , token_id
        , symbol
        , amount
    from erc721_wallet_hour
    inner join ${erc721_token_table}
    on token_address = contract_address
    where amount != 0
)

, erc1155_transfersingle_tx as (
    select block_date
        , date_trunc('hour', block_time) as block_hour
        , `from`
        , to
        , contract_address as token_address
        , id as token_id
        , cast(value as double) as value
        from ${erc1155_tranfersingle_table}
        where block_time >= cast(${from} as timestamp)
        and block_time < cast(${to} as timestamp)
)

, erc1155_tranferbatch_explode as (
    select block_date
        , date_trunc('hour', block_time) as block_hour
        , `from`
        , to
        , contract_address as token_address
        , explode(arrays_zip(values, ids)) as ids_and_count
    from ${erc1155_tranferbatch_table}
    where block_time >= cast(${from} as timestamp)
    and block_time < cast(${to} as timestamp)
)

, erc1155_transfer_tx as (
    select block_date
        , block_hour
        , `from`
        , to
        , token_address
        , ids_and_count.ids as token_id
        , cast(ids_and_count.values as double) as value
    from erc1155_tranferbatch_explode
    union all 
    select * from erc1155_transfersingle_tx
)

, erc1155_transfer_wallet as (
    select block_date
        , block_hour
        , `from` as wallet_address
        , token_address
        , token_id
        , -1 * value as value
    from erc1155_transfer_tx
    union all
    select block_date
        , block_hour
        , to as wallet_address
        , token_address
        , token_id
        , value
    from erc1155_transfer_tx
)

, erc1155_wallet_hour as (
    select block_date
        , block_hour
        , wallet_address
        , token_address
        , token_id
        , sum(value) as amount
    from erc1155_transfer_wallet
    group by block_date, block_hour, wallet_address, token_address, token_id
)

, erc1155_wallet_hour_token as (
    select block_date
        , block_hour
        , concat(
            substring(token_address, 1, 3),
            substring(wallet_address, 1, 3)
        ) AS key_partition
        , 'ERC1155' as nft_type
        , wallet_address
        , token_address
        , token_id
        , symbol
        , amount
    from erc1155_wallet_hour
    inner join ${erc1155_token_table}
    on token_address = contract_address
    where amount != 0
)



select * from erc721_wallet_hour_token
union all 
select * from erc1155_wallet_hour_token

