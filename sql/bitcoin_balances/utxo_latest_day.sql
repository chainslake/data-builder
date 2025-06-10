frequent_type=day
repair_mode=false
list_input_tables=${chain_name}_balances.utxo_transfer_day
output_table=${chain_name}_balances.utxo_latest_day
re_partition_by_range=version,key_partition
write_mode=Append
number_index_columns=3
partition_by=version
is_vacuum=true

===

with new_balances as (
    select max(address) as address 
        , tx_id
        , n
        , max(value) as value
        , sum(utxo) as utxo
        , max(key_partition) as key_partition
    from ${list_input_tables}
    where block_date >= cast(${from} as timestamp)
    and block_date < cast(${to} as timestamp)
    group by tx_id, n
)

${if table_existed} 

, all_balances as (
    -- Get the current balance in this table and union with new balance
    select * from new_balances
    where utxo != 0
    union all 
    select address
        , tx_id
        , n
        , value
        , utxo
        , key_partition
    from ${output_table}
    where version = ${current_version}
)
, balance_agg as (
    select max(address) as address
        , tx_id
        , n
        , max(value) as value
        , sum(utxo) as utxo
        , max(key_partition) as key_partition
    from all_balances
    group by tx_id, n
)

select ${next_version} as version -- Increase to next version
    , * from balance_agg
    where utxo != 0 -- Filter spent UTXOs

${else}

SELECT 1 as version -- version = 1 in the first run
    , * FROM new_balances
    where utxo != 0 -- Filter spent UTXOs

${endif}