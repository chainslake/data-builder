frequent_type=day
repair_mode=false
list_input_tables=${chain_name}_balances.nft_transfer_day
output_table=${chain_name}_balances.nft_latest_day
re_partition_by_range=version,key_partition
write_mode=Append
number_index_columns=3
partition_by=version
is_vacuum=true

===

with new_balances as (
    select wallet_address
        , token_address
        , token_id
        , symbol
        , sum(amount) as balance
        , nft_type
        , key_partition
    from ${list_input_tables}
    where block_date >= cast(${from} as timestamp)
    and block_date < cast(${to} as timestamp)
    group by key_partition, nft_type, wallet_address, token_address, token_id, symbol
)

${if table_existed}


, all_balances as (
    select * from new_balances
    where balance != 0
    union all 
    select wallet_address
        , token_address
        , token_id
        , symbol
        , balance
        , nft_type
        , key_partition
    from ${output_table}
    where version = ${current_version} -- Get current balance in this table and union with new balance
)

, balance_agg as (
    select wallet_address
        , token_address
        , token_id
        , symbol
        , sum(balance) as balance
        , nft_type
        , key_partition
    from all_balances
    group by key_partition, nft_type, wallet_address, token_address, token_id, symbol
)

select ${next_version} as version -- Increase to next version 
    , wallet_address
    , token_address
    , token_id
    , symbol
    , balance
    , nft_type
    , key_partition
from balance_agg
where balance != 0 -- Filter out records if balance = 0

${else}

SELECT 1 as version, * FROM new_balances -- Set version = 1 in first run time
where balance != 0

${endif}
