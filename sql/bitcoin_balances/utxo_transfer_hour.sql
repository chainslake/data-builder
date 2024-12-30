frequent_type=hour
list_input_tables=${chain_name}.inputs,${chain_name}.outputs
output_table=${chain_name}_balances.utxo_transfer_hour
input_coin_table=${chain_name}.inputs
output_coin_table=${chain_name}.outputs
re_partition_by_range=block_date,key_partition
partition_by=block_date
write_mode=Append
number_index_columns=2

===

with input_output as (
    select block_date
        , date_trunc('hour', block_time) as block_hour
        , tx_id
        , v_out as n
        , 0 as value
        , "" as address
        , -1 as utxo
    from ${input_coin_table}
    where block_time >= cast(${from} as timestamp)
    and block_time < cast(${to} as timestamp) 
    and tx_id is not null
    union all
    select block_date
        , date_trunc('hour', block_time) as block_hour
        , tx_id
        , n
        , value
        , address
        , 1 as utxo
    from ${output_coin_table}
    where block_time >= cast(${from} as timestamp)
    and block_time < cast(${to} as timestamp)
    and address is not null 
)

, input_output_hour as (
    select block_date
        , block_hour
        , tx_id
        , n
        , max(value) as value
        , max(address) as address
        , sum(utxo) as utxo
    from input_output
    group by block_date, block_hour, tx_id, n
)

select block_date
    , case 
        when address like 'bc1%' then concat(substring(tx_id, 1, 1), substring(address, 1, 5))
        else concat(substring(tx_id, 1, 1), substring(address, 1, 2))
    end as key_partition
    , block_hour
    , tx_id
    , n
    , value
    , address
    , utxo
from input_output_hour where utxo != 0
