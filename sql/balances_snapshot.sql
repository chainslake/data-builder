frequent_type=day
repair_mode=false
list_input_tables=${chain_name}_balances.nft_latest_day_snapshot
output_table=${chain_name}_balances.nft_latest_day
re_partition_by_range=key_partition
write_mode=Append
number_index_columns=3
partition_by=version

===

select 1 as version, wallet_address, token_address, token_id, symbol, balance, nft_type, key_partition
from ${list_input_tables}
where balance != 0