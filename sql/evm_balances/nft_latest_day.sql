frequent_type=day
repair_mode=false
list_input_tables=${chain_name}_balances.nft_transfer_day
output_table=${chain_name}_balances.nft_latest_day
re_partition_by_range=is_change,key_partition
write_mode=Overwrite
number_index_columns=5
partition_by=is_change,key_partition
merge_by=key_partition,nft_type,wallet_address,token_address,token_id
is_vacuum=true

===

with new_balances as (
    select key_partition 
        , nft_type
        , wallet_address
        , token_address
        , token_id
        , symbol
        , sum(amount) as balance
    from ${list_input_tables}
    where block_date >= cast(${from} as timestamp)
    and block_date < cast(${to} as timestamp)
    group by key_partition, nft_type, wallet_address, token_address, token_id, symbol
)

${if table_existed}


, all_balances as (
    select 2 as sign, * from new_balances
    where balance != 0
    union all 
    select is_change as sign
        , key_partition
        , nft_type
        , wallet_address
        , token_address
        , token_id
        , symbol
        , balance
    from ${output_table}
)

, balance_agg as (
    select sum(sign) as sign
        , key_partition
        , nft_type
        , wallet_address
        , token_address
        , token_id
        , symbol
        , sum(balance) as balance
    from all_balances
    group by key_partition, nft_type, wallet_address, token_address, token_id, symbol
)

select case 
    when sign = 3 or sign = 2 then 1
    when sign = 1 then 0
    end as is_change
    , key_partition
    , nft_type
    , wallet_address
    , token_address
    , token_id
    , symbol
    , balance
from balance_agg
where sign > 0

${else}

SELECT 0 as is_change, * FROM new_balances
where balance != 0

${endif}
