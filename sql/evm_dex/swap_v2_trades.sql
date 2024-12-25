frequent_type=block
list_input_tables=${chain_name}_decoded.uniswap_v2_evt_swap,${chain_name}_contract.uniswap_v2_info,${chain_name}_contract.erc20_tokens
output_table=${chain_name}_dex.uniswap_v2_trades
swap_event_table=${chain_name}_decoded.uniswap_v2_evt_swap
erc20_token_table=${chain_name}_contract.erc20_tokens
swap_info_table=${chain_name}_contract.uniswap_v2_info
re_partition_by_range=block_date,block_time
partition_by=block_date
write_mode=Append

===

with swap_table_info as (
    select swap.*
        , erc20_swap.name as swap_protocol_name
        , swap_info.token0 as token0_address
        , erc20_token0.symbol as token0_symbol
        , erc20_token0.decimals as token0_decimals
        , swap_info.token1 as token1_address
        , erc20_token1.symbol as token1_symbol
        , erc20_token1.decimals as token1_decimals
    from ${swap_event_table} swap
    left join ${swap_info_table} swap_info
        on swap.contract_address = swap_info.contract_address
    left join ${erc20_token_table} erc20_swap
        on swap_info.contract_address = erc20_swap.contract_address
    left join ${erc20_token_table} erc20_token0
        on swap_info.token0 = erc20_token0.contract_address
    left join ${erc20_token_table} erc20_token1
        on swap_info.token1 = erc20_token1.contract_address
    where block_number >= ${from} and block_number <= ${to}

)

, token_trades as (
    select block_date
        , block_number
        , block_time
        , current_timestamp() as updated_time
        , tx_hash
        , evt_index
        , to as taker
        , swap_protocol_name
        , contract_address as pair_contract
        , 'sell' as trade_type
        , case
            when amount0in != '0' then token0_address
            else token1_address
        end as token_address
        , case
            when amount0in != '0' then token0_symbol
            else token1_symbol
        end as token_symbol
        , case
            when amount0in != '0' then token0_decimals
            else token1_decimals
        end as token_decimals
        , case
            when amount0in != '0' then amount0in
            else amount1in
        end as token_wei
        , case
            when amount0in != '0' then token1_address
            else token0_address
        end as currency_address
        , case
            when amount0in != '0' then token1_symbol
            else token0_symbol
        end as currency_symbol
        , case
            when amount0in != '0' then token1_decimals
            else token0_decimals
        end as currency_decimals
        , case
            when amount0in != '0' then amount1out
            else amount0out
        end as currency_wei
    from swap_table_info
    union all
    select block_date
        , block_number
        , block_time
        , current_timestamp() as updated_time
        , tx_hash
        , evt_index
        , to as taker
        , swap_protocol_name
        , contract_address as pair_contract
        , 'buy' as trade_type
        , case
            when amount1in != '0' then token0_address
            else token1_address
        end as token_address
        , case
            when amount1in != '0' then token0_symbol
            else token1_symbol
        end as token_symbol
        , case
            when amount1in != '0' then token0_decimals
            else token1_decimals
        end as token_decimals
        , case
            when amount1in != '0' then amount0out
            else amount1out
        end as token_wei
        , case
            when amount1in != '0' then token1_address
            else token0_address
        end as currency_address
        , case
            when amount1in != '0' then token1_symbol
            else token0_symbol
        end as currency_symbol
        , case
            when amount1in != '0' then token1_decimals
            else token0_decimals
        end as currency_decimals
        , case
            when amount1in != '0' then amount1in
            else amount0in
        end as currency_wei
    from swap_table_info
)

select *
    , token_wei / power(10, token_decimals) as token_value
    , currency_wei / power(10, currency_decimals) as currency_value
    , (currency_wei / power(10, currency_decimals)) / (token_wei / power(10, token_decimals)) as token_price
from token_trades