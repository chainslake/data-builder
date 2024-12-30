frequent_type=day
repair_mode=false
list_input_tables=${chain_name}_balances.utxo_transfer_day
output_table=${chain_name}_balances.utxo_latest_day
re_partition_by_range=is_spent,key_partition
write_mode=Overwrite
number_index_columns=4
partition_by=is_spent,key_partition
merge_by=key_partition,tx_id,n
is_vacuum=true

===

with new_balances as (
    select max(key_partition) as key_partition
        , tx_id
        , n
        , max(value) as value
        , max(address) as address
        , sum(utxo) as utxo
    from ${list_input_tables}
    where block_date >= cast(${from} as timestamp)
    and block_date < cast(${to} as timestamp)
    group by tx_id, n
)

${if table_existed} 

, all_balances as (
    select * from new_balances
    where utxo != 0
    union all 
    select key_partition
        , tx_id
        , n
        , value
        , address
        , utxo
    from ${output_table}
    where is_spent = 0
)
, balance_agg as (
    select max(key_partition) as key_partition
        , tx_id
        , n
        , max(value) as value
        , max(address) as address
        , sum(utxo) as utxo
    from all_balances
    group by tx_id, n
)

select case 
    when utxo = 0 then 1
    else 0
    end as is_spent
    , * from balance_agg

${else}

SELECT 0 as is_spent, * FROM new_balances
where utxo != 0

${endif}