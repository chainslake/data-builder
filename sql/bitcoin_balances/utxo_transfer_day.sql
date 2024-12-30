frequent_type=day
list_input_tables=${chain_name}_balances.utxo_transfer_hour
output_table=${chain_name}_balances.utxo_transfer_day
re_partition_by_range=block_date,key_partition
partition_by=block_date
write_mode=Append
number_index_columns=2

===

with utxo_transfer_day as (
    select block_date
        , max(key_partition) as key_partition
        , tx_id
        , n
        , max(value) as value
        , max(address) as address
        , sum(utxo) as utxo
    from ${list_input_tables}
    where block_date >= cast(${from} as timestamp)
        and block_date < cast(${to} as timestamp)
        group by block_date, tx_id, n
)

select * from utxo_transfer_day
where utxo != 0