frequent_type=day
list_input_tables=${chain_name}_balances.nft_transfer_hour
output_table=${chain_name}_balances.nft_transfer_day
re_partition_by_range=block_date,key_partition
partition_by=block_date
write_mode=Append
number_index_columns=7

===

with nft_transfer_day as (
    select block_date
        , key_partition
        , nft_type
        , wallet_address
        , token_address
        , token_id
        , symbol
        , sum(amount) as amount
    from ${list_input_tables}
    where block_date >= cast(${from} as timestamp)
    and block_date < cast(${to} as timestamp)
    group by block_date, key_partition, nft_type, wallet_address, token_address, token_id, symbol
)

select * from nft_transfer_day
where amount != 0
