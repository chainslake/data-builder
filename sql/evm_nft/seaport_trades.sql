frequent_type=minute
list_input_tables=${chain_name}_decoded.seaport_evt_orderfulfilled,cex_binance.trade_minute,${chain_name}_contract.erc20_tokens,${chain_name}_contract.erc721_tokens,${chain_name}_contract.erc1155_tokens
seaport_table=${chain_name}_decoded.seaport_evt_orderfulfilled
trade_minute_table=cex_binance.trade_minute
erc20_token_table=${chain_name}_contract.erc20_tokens
erc721_token_table=${chain_name}_contract.erc721_tokens
erc1155_token_table=${chain_name}_contract.erc1155_tokens
native_coin=${native_coin}
wrap_native_address=${wrap_native_address}
output_table=${chain_name}_nft.seaport_trades
re_partition_by_range=block_date,block_time
partition_by=block_date
write_mode=Append
number_index_columns=4

===

with order as (
    select block_date
        , block_number
        , block_time
        , date_trunc('minute', block_time) as block_minute
        , contract_address
        , tx_hash
        , evt_index
        , offer
        , consideration
    from ${seaport_table}
    where block_time >= cast(${from} as timestamp)
    and block_time < cast(${to} as timestamp)
)
, flatten_order as (
    select block_date
        , block_number
        , block_time
        , block_minute
        , contract_address
        , tx_hash
        , evt_index
        , 'offer' as order_type
        , explode(offer) as data
    from order
    union all
    select block_date
        , block_number
        , block_time
        , block_minute
        , contract_address
        , tx_hash
        , evt_index
        , 'consideration' as order_type
        , explode(consideration) as data
    from order
)
, extracted_order as (
    select block_date
        , block_number
        , block_time
        , block_minute
        , contract_address
        , tx_hash
        , evt_index
        , order_type
        , get_json_object(data, '$$.itemType') as item_type
        , get_json_object(data, '$$.token') as token_address
        , get_json_object(data, '$$.identifier') as token_id
        , get_json_object(data, '$$.amount') as amount
    from flatten_order
)
, native_price_by_usd as (
    select block_date
        , block_minute
        , open_price as price
    from ${trade_minute_table}
    where block_minute >= cast(${from} as timestamp)
    and block_minute < cast(${to} as timestamp)
    and base_asset = '${native_coin}'

)
, order_with_price as (
    select order.block_date
        , order.block_number
        , order.block_time
        , order.block_minute
        , order.contract_address
        , order.tx_hash
        , order.evt_index
        , order.order_type
        , order.item_type
        , order.token_address
        , order.token_id
        , order.amount
        , case 
            when native_price.price is not Null then native_price.price
            else 0
        end as price
    from extracted_order order
    left join native_price_by_usd native_price
    on order.block_minute = native_price.block_minute 
)
, order_with_price_remind as (
    select block_date
        , block_number
        , block_time
        , block_minute
        , contract_address
        , tx_hash
        , evt_index
        , order_type
        , case 
            when item_type = 0 then 'Native'
            when item_type = 1 then 'ERC20'
            when item_type = 2 or item_type = 4 then 'ERC721'
            when item_type = 3 or item_type = 5 then 'ERC1155'
        end as type_token
        , token_address
        , token_id
        , amount
        , case 
            when item_type = 0 then price
            when item_type = 1 and token_address = '${wrap_native_address}' then price
            else 0
        end as price
    from order_with_price
)
, token_info as (
    select '0x0000000000000000000000000000000000000000' as contract_address
        , 'Ethereum' as name
        , 'ETH' as symbol
        , 18 as decimals
        , 'Native' as type_token
    union all
    select contract_address 
        , name
        , symbol
        , decimals
        , 'ERC20' as type_token
    from ${erc20_token_table}
    union all
    select contract_address 
        , name
        , symbol
        , 0 as decimals
        , 'ERC721' as type_token
    from ${erc721_token_table}
    union all 
    select contract_address 
        , name
        , symbol
        , 0 as decimals
        , 'ERC1155' as type_token
    from ${erc1155_token_table}
)
, order_enrichment as (
    select order.block_date
        , order.block_number
        , order.block_time
        , order.block_minute
        , order.contract_address
        , order.tx_hash
        , order.evt_index
        , order.order_type
        , order.type_token
        , order.token_address
        , order.token_id
        , case 
            when info.decimals is not Null and info.decimals != 0 then order.amount / pow(10, info.decimals)
            else order.amount
        end as amount
        , order.price
        , info.name
        , info.symbol 
        , info.decimals
    from order_with_price_remind order
    left join token_info info
    on (
        order.type_token = info.type_token
        and order.token_address = info.contract_address
    )
)
, order_with_value as (
        select block_date
        , block_number
        , block_time
        , block_minute
        , contract_address
        , tx_hash
        , evt_index
        , order_type
        , type_token
        , token_address
        , token_id
        , amount
        , price
        , name
        , symbol
        , decimals
        , case 
            when price != 0 then amount * price
            else 0
        end as value
    from order_enrichment
)
, order_aggreate as (
    select block_number
        , evt_index
        , sum(
            case 
                when order_type = 'offer' then value
                else -1 * value
            end
        ) as delta_value
        , sum(
            case 
                when price = 0 then 1
                else 0
            end
        ) as count_no_price
    from order_with_value 
    group by block_number, evt_index
)
, order_aggreate_filter as (
    select block_number
        , evt_index
        , abs(delta_value) as value
    from order_aggreate
    where count_no_price = 1
)

, order_fill_value as (
    select order.block_date
        , order.block_number
        , order.block_time
        , order.block_minute
        , order.contract_address
        , order.tx_hash
        , order.evt_index
        , order.order_type
        , order.type_token
        , order.token_address
        , order.token_id
        , order.amount
        , order.price
        , order.name
        , order.symbol
        , order.decimals
        , case 
            when miss_value.value is not null and order.value = 0 then miss_value.value
            else order.value
        end as value
    from order_with_value order
    left join order_aggreate_filter miss_value
    on (
        order.block_number = miss_value.block_number
        and order.evt_index = miss_value.evt_index
    )
)

select block_date
    , block_number
    , block_time
    , block_minute
    , contract_address
    , tx_hash
    , evt_index
    , order_type
    , type_token
    , token_address
    , token_id
    , amount
    , case 
        when price = 0 then value / amount 
        else price
    end as price
    , name
    , symbol
    , decimals
    , value
from order_fill_value