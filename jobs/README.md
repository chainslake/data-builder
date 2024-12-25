# Job execution

Jobs are Chainslake's data processing execution units, which are scripts placed in the `jobs` directory and called by Airflow (see data pipeline section [here](/airflow/README.md)).


## Configuration

The job configurations placed after the `spark.app_properties` property include:
- __app_name__: The names of the apps used, currently include the following apps:
    - `sql.transformer`: Create and update data tables using sql, used with `sql_file` configuration to define sql file name
    - `evm.decoded`: Create and update data for decoded tables, used with `abi_file` configuration to define abi of protocol
    - `evm.contract_info`: Create and update data for contract info table, used with `contract_info_file` configuration to define contract info of contract.
    - Other jobs create and update data for tables according to specific use cases.
        - `evm_origin.transaction_blocks`: Get raw transaction block data from EVM blockchain node, use with `rpc_list` configuration to define list of RPC urls.
        - `evm_origin.blocks_receipt`: Get raw transaction receipt of each block from EVM blockchain node
        - `evm_origin.traces`: Get raw internal transaction (trace) of each block from EVM blockchain node
        - `evm.transactions`: Extract transaction data from raw block transaction
        - `evm.logs`: Extract log data from raw block receipt data
        - `evm.traces`: Extract trace data from raw traces data
        - `binance_cex.exchange_info`: Job get all Exchanges from Binance CEX
        - `binance_cex.trade_minute`: Job get trade data by minute from Binance CEX
- __config_file__: config file contains general config of the job
- __chain_name__: 
- __number_block_per_partition__: s defined so that one partition processes about 1 hour of data
- __max_number_partition__: Maximum number of partition each time process
- __max_time_run__: Miximum number of time process, infinite if equal to 0
- __run_mode__: `forward` or `backward`
- __start_number__, __end_number__: data limit, default unlimit
- __repair_mode__: `true` or `false`, default `false`. Use in case of data repair


