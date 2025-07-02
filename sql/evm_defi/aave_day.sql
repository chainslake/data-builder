frequent_type=day
list_input_tables=${chain_name}_defi.aave_minute
output_table=${chain_name}_defi.aave_day
re_partition_by_range=block_date
write_mode=Append
number_index_columns=3
partition_by=block_date

===

with aave_aggreate as (
    select block_date
        , event_type
        , token_address
        , symbol
        , min(apr) as apr
        , sum(value) as volume
    from ${list_input_tables}
    where 
        block_date >= cast(${from} as timestamp)
        AND 
        block_date < cast(${to} as timestamp)
    group by block_date, event_type, token_address, symbol
)

, aave_pivot as (
    SELECT *
    FROM (
        SELECT
            block_date,
            symbol,
            token_address,
            event_type,
            volume
        FROM aave_aggreate
    )
    PIVOT (
        FIRST(volume) FOR event_type IN ('deposit', 'withdraw', 'borrow', 'repay', 'liquidation', 'flashloan')
    )
)

, aave_with_apr as (
    select aave.block_date, 
        aave.symbol,
        aave.token_address,
        aave.deposit,
        aave.withdraw,
        aave.borrow,
        aave.repay,
        aave.liquidation,
        aave.flashloan,
        borrow.apr
    from aave_pivot aave
    left join (
        select block_date, token_address, apr
        from aave_aggreate
        where event_type = 'borrow'
    ) borrow
    on (
        aave.block_date = borrow.block_date
        and
        aave.token_address = borrow.token_address
    )
)

select block_date
    , symbol
    , token_address
    , deposit
    , withdraw
    , borrow
    , repay
    , liquidation
    , flashloan
    , apr
    , COALESCE(deposit, 0) + COALESCE(withdraw, 0) + COALESCE(borrow, 0) + COALESCE(repay, 0) + COALESCE(liquidation, 0) + COALESCE(flashloan, 0) AS total_volume
from aave_with_apr


