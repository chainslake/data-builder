frequent_type=day
list_input_tables=${chain_name}_nft.seaport_trades
output_table=${chain_name}_nft.trade_day
re_partition_by_range=block_date
write_mode=Append
number_index_columns=2
partition_by=block_date

===

SELECT
    block_date,
    type_token,
    token_address,
    name,
    symbol,
    sum(VALUE) AS volume,
    count(tx_hash) AS number_tx,
    min(price) AS lowest_price,
    min_by(token_id, price) as lowest_price_token,
    max(price) AS highest_price,
    max_by(token_id, price) as highest_price_token
FROM
    ${list_input_tables}
WHERE
    block_date >= cast(${from} as timestamp)
    AND 
    block_date < cast(${to} as timestamp)
    AND (
    type_token = 'ERC721'
    OR type_token = 'ERC1155'
    )
    AND price > 0
GROUP BY
    block_date,
    type_token,
    token_address,
    name,
    symbol
