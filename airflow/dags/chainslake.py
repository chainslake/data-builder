# The DAG object; we'll need this to instantiate a DAG
from airflow.models.dag import DAG
from datetime import datetime
# Operators; we need this to operate!
from airflow.operators.bash import BashOperator
import os
with DAG(
    "Chainslake",
    # These args will get passed on to each operator
    # You can override them on a per-task basis during operator initialization
    default_args={
        "depends_on_past": True,
        'wait_for_downstream': False,
        "email": ["lakechain.nguyen@gmail.com"],
        "email_on_failure": True,
        "email_on_retry": False,
        "retries": 2
    },
    description="Chainslake pipeline",
    start_date=datetime(2025, 1, 3, 1),
    # schedule="@continuous",
    schedule="@hourly",
    # schedule="@once",
    max_active_runs=1,
    max_active_tasks=2,
) as dag:

    RUN_DIR = os.environ.get("CHAINSLAKE_HOME_DIR") + "/jobs/solana"

    solana_origin_transaction_blocks = BashOperator(
        task_id="solana_origin.transaction_blocks",
        bash_command=f"cd {RUN_DIR} && ./origin/transaction_blocks.sh "
    )

    solana_extract_blocks = BashOperator(
        task_id="solana.blocks",
        bash_command=f"cd {RUN_DIR} && ./extract/blocks.sh "
    )

    solana_extract_transactions = BashOperator(
        task_id="solana.transactions",
        bash_command=f"cd {RUN_DIR} && ./extract/transactions.sh "
    )

    solana_extract_rewards = BashOperator(
        task_id="solana.rewards",
        bash_command=f"cd {RUN_DIR} && ./extract/rewards.sh "
    )

    solana_extract_instructions = BashOperator(
        task_id="solana.instructions",
        bash_command=f"cd {RUN_DIR} && ./extract/instructions.sh "
    )

    solana_extract_native_balances = BashOperator(
        task_id="solana.native_balances",
        bash_command=f"cd {RUN_DIR} && ./extract/native_balances.sh "
    )

    solana_extract_token_balances = BashOperator(
        task_id="solana.token_balances",
        bash_command=f"cd {RUN_DIR} && ./extract/token_balances.sh "
    )


    solana_origin_transaction_blocks >> [solana_extract_blocks, solana_extract_instructions, solana_extract_transactions, solana_extract_rewards, solana_extract_native_balances, solana_extract_token_balances]


    RUN_DIR = os.environ.get("CHAINSLAKE_HOME_DIR") + "/jobs/bitcoin"

    bitcoin_origin_transaction_blocks = BashOperator(
        task_id="bitcoin_origin.transaction_blocks",
        bash_command=f"cd {RUN_DIR} && ./origin/transaction_blocks.sh "
    )

    bitcoin_extract_blocks = BashOperator(
        task_id="bitcoin.blocks",
        bash_command=f"cd {RUN_DIR} && ./extract/blocks.sh "
    )

    bitcoin_extract_transactions = BashOperator(
        task_id="bitcoin.transactions",
        bash_command=f"cd {RUN_DIR} && ./extract/transactions.sh "
    )

    bitcoin_extract_inputs = BashOperator(
        task_id="bitcoin.inputs",
        bash_command=f"cd {RUN_DIR} && ./extract/inputs.sh "
    )

    bitcoin_extract_outputs = BashOperator(
        task_id="bitcoin.outputs",
        bash_command=f"cd {RUN_DIR} && ./extract/outputs.sh "
    )

    bitcoin_origin_transaction_blocks >> [bitcoin_extract_blocks, bitcoin_extract_transactions, bitcoin_extract_inputs, bitcoin_extract_outputs]

    bitcoin_balances_utxo_transfer_hour = BashOperator(
        task_id="bitcoin_balances.utxo_transfer_hour",
        bash_command=f"cd {RUN_DIR} && ./balances/utxo_transfer_hour.sh "
    )

    bitcoin_balances_utxo_transfer_day = BashOperator(
        task_id="bitcoin_balances.utxo_transfer_day",
        bash_command=f"cd {RUN_DIR} && ./balances/utxo_transfer_day.sh "
    )

    bitcoin_balances_utxo_latest_day = BashOperator(
        task_id="bitcoin_balances.utxo_latest_day",
        bash_command=f"cd {RUN_DIR} && ./balances/utxo_latest_day.sh "
    )

    [bitcoin_extract_inputs, bitcoin_extract_outputs] >> bitcoin_balances_utxo_transfer_hour >> bitcoin_balances_utxo_transfer_day >> bitcoin_balances_utxo_latest_day

    RUN_DIR = os.environ.get("CHAINSLAKE_HOME_DIR") + "/jobs/binance"

    # binance_cex_exchange_info = BashOperator(
    #     task_id="binance_cex.exchange_info",
    #     bash_command=f"cd {RUN_DIR} && ./cex/exchange_info.sh "
    # )

    binance_cex_trade_minute = BashOperator(
        task_id="binance_cex.trade_minute",
        bash_command=f"cd {RUN_DIR} && ./cex/trade_minute.sh "
    )

    binance_cex_trade_minute_agg_volume = BashOperator(
        task_id="binance_cex.trade_minute_agg_volume",
        bash_command=f"cd {RUN_DIR} && ./cex/trade_minute_agg_volume.sh "
    )
    # binance_cex_exchange_info >> binance_cex_trade_minute >> binance_cex_trade_minute_agg_volume

    binance_cex_trade_minute >> binance_cex_trade_minute_agg_volume

    ########################### ORIGIN ##########################################

    RUN_DIR = os.environ.get("CHAINSLAKE_HOME_DIR") + "/jobs/ethereum"

    ethereum_origin_transaction_blocks = BashOperator(
        task_id="ethereum_origin.transaction_blocks",
        bash_command=f"cd {RUN_DIR} && ./origin/transaction_blocks.sh "
    )

    ethereum_origin_blocks_receipt = BashOperator(
        task_id="ethereum_origin.blocks_receipt",
        bash_command=f"cd {RUN_DIR} && ./origin/blocks_receipt.sh "
    )

    ethereum_origin_traces = BashOperator(
        task_id="ethereum_origin.traces",
        bash_command=f"cd {RUN_DIR} && ./origin/traces.sh "
    )

    ethereum_origin_transaction_blocks >> [ethereum_origin_blocks_receipt, ethereum_origin_traces]

    ############################################## EXTRACT #############################

    ethereum_logs = BashOperator(
        task_id="ethereum.logs",
        bash_command=f"cd {RUN_DIR} && ./extract/logs.sh "
    )

    ethereum_transactions = BashOperator(
        task_id="ethereum.transactions",
        bash_command=f"cd {RUN_DIR} && ./extract/transactions.sh "
    )

    ethereum_origin_blocks_receipt >> [ethereum_transactions, ethereum_logs]

    ethereum_traces = BashOperator(
        task_id="ethereum.traces",
        bash_command=f"cd {RUN_DIR} && ./extract/traces.sh "
    )


    [ethereum_origin_traces, ethereum_transactions] >> ethereum_traces

    ############################################# DECODED ###########################################

    ethereum_decoded_erc20 = BashOperator(
        task_id="ethereum_decoded.erc20",
        bash_command=f"cd {RUN_DIR} && ./extract/decoded.sh erc20"
    )

    ethereum_decoded_erc721 = BashOperator(
        task_id="ethereum_decoded.erc721",
        bash_command=f"cd {RUN_DIR} && ./extract/decoded.sh erc721"
    )

    ethereum_decoded_erc1155 = BashOperator(
        task_id="ethereum_decoded.erc1155",
        bash_command=f"cd {RUN_DIR} && ./extract/decoded.sh erc1155"
    )

    ethereum_decoded_uniswap_v2 = BashOperator(
        task_id="ethereum_decoded.uniswap_v2",
        bash_command=f"cd {RUN_DIR} && ./extract/decoded.sh uniswap_v2"
    )

    ethereum_decoded_uniswap_v3 = BashOperator(
        task_id="ethereum_decoded.uniswap_v3",
        bash_command=f"cd {RUN_DIR} && ./extract/decoded.sh uniswap_v3"
    )

    ethereum_logs >> [ethereum_decoded_erc20, ethereum_decoded_erc721, ethereum_decoded_erc1155, ethereum_decoded_uniswap_v2, ethereum_decoded_uniswap_v3]

    ################################################### CONTRACT INFO ##########################################

    ethereum_contract_erc20_tokens = BashOperator(
        task_id="ethereum_contract.erc20_tokens",
        bash_command=f"cd {RUN_DIR} && ./extract/contract_info.sh erc20"
    )

    ethereum_decoded_erc20 >> ethereum_contract_erc20_tokens

    ethereum_contract_erc721_tokens = BashOperator(
        task_id="ethereum_contract.erc721_tokens",
        bash_command=f"cd {RUN_DIR} && ./extract/contract_info.sh erc721"
    )

    ethereum_decoded_erc721 >> ethereum_contract_erc721_tokens

    ethereum_contract_erc1155_tokens = BashOperator(
        task_id="ethereum_contract.erc1155_tokens",
        bash_command=f"cd {RUN_DIR} && ./extract/contract_info.sh erc1155"
    )

    ethereum_decoded_erc1155 >> ethereum_contract_erc1155_tokens

    ######################################################## SQL ############################################

    ethereum_balances_token_transfer_hour = BashOperator(
        task_id="ethereum_balances.token_transfer_hour",
        bash_command=f"cd {RUN_DIR} && ./balances/token_transfer_hour.sh "
    )

    ethereum_balances_token_transfer_day = BashOperator(
        task_id="ethereum_balances.token_transfer_day",
        bash_command=f"cd {RUN_DIR} && ./balances/token_transfer_day.sh "
    )

    ethereum_balances_token_latest_day = BashOperator(
        task_id="ethereum_balances.token_latest_day",
        bash_command=f"cd {RUN_DIR} && ./balances/token_latest_day.sh "
    )

    [ethereum_contract_erc20_tokens, ethereum_traces] >> ethereum_balances_token_transfer_hour >> ethereum_balances_token_transfer_day >> ethereum_balances_token_latest_day

    ethereum_balances_nft_transfer_hour = BashOperator(
        task_id="ethereum_balances.nft_transfer_hour",
        bash_command=f"cd {RUN_DIR} && ./balances/nft_transfer_hour.sh "
    )

    ethereum_balances_nft_transfer_day = BashOperator(
        task_id="ethereum_balances.nft_transfer_day",
        bash_command=f"cd {RUN_DIR} && ./balances/nft_transfer_day.sh "
    )

    ethereum_balances_nft_latest_day = BashOperator(
        task_id="ethereum_balances.nft_latest_day",
        bash_command=f"cd {RUN_DIR} && ./balances/nft_latest_day.sh "
    )

    [ethereum_contract_erc721_tokens, ethereum_contract_erc1155_tokens] >> ethereum_balances_nft_transfer_hour >> ethereum_balances_nft_transfer_day >> ethereum_balances_nft_latest_day

    ethereum_contract_uniswap_v2_info = BashOperator(
        task_id="ethereum_contract.uniswap_v2_info",
        bash_command=f"cd {RUN_DIR} && ./extract/contract_info.sh uniswap_v2"
    )

    ethereum_decoded_uniswap_v2 >> ethereum_contract_uniswap_v2_info

    ethereum_contract_uniswap_v3_info = BashOperator(
        task_id="ethereum_contract.uniswap_v3_info",
        bash_command=f"cd {RUN_DIR} && ./extract/contract_info.sh uniswap_v3"
    )

    ethereum_decoded_uniswap_v3 >> ethereum_contract_uniswap_v3_info

    ethereum_dex_swap_v2_trades = BashOperator(
        task_id="ethereum_dex.swap_v2_trades",
        bash_command=f"cd {RUN_DIR} && ./dex/swap_v2_trades.sh "
    )

    [ethereum_contract_uniswap_v2_info, ethereum_contract_erc20_tokens] >> ethereum_dex_swap_v2_trades

    ethereum_dex_swap_v3_trades = BashOperator(
        task_id="ethereum_dex.swap_v3_trades",
        bash_command=f"cd {RUN_DIR} && ./dex/swap_v3_trades.sh "
    )

    [ethereum_contract_uniswap_v3_info, ethereum_contract_erc20_tokens] >> ethereum_dex_swap_v3_trades


    ethereum_prices_weth_usd_minute = BashOperator(
        task_id="ethereum_prices.weth_usd_minute",
        bash_command=f"cd {RUN_DIR} && ./prices/minute.sh weth_usd_minute 0xdac17f958d2ee523a2206206994597c13d831ec7,0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48 0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2 "
    )

    [ethereum_dex_swap_v2_trades, ethereum_dex_swap_v3_trades] >> ethereum_prices_weth_usd_minute


    ethereum_prices_erc20_weth_minute = BashOperator(
        task_id="ethereum_prices.erc20_weth_minute",
        bash_command=f"cd {RUN_DIR} && ./prices/minute.sh erc20_weth_minute 0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2 all "
    )

    [ethereum_dex_swap_v2_trades, ethereum_dex_swap_v3_trades] >> ethereum_prices_erc20_weth_minute

    
    ethereum_prices_erc20_usd_minute = BashOperator(
        task_id="ethereum_prices.erc20_usd_minute",
        bash_command=f"cd {RUN_DIR} && ./prices/usd_minute.sh "
    )

    [ethereum_prices_erc20_weth_minute, ethereum_prices_weth_usd_minute] >> ethereum_prices_erc20_usd_minute

    ethereum_prices_erc20_usd_hour = BashOperator(
        task_id="ethereum_prices.erc20_usd_hour",
        bash_command=f"cd {RUN_DIR} && ./prices/hour.sh erc20_usd_minute erc20_usd_hour "
    )

    ethereum_prices_erc20_usd_day = BashOperator(
        task_id="ethereum_prices.erc20_usd_day",
        bash_command=f"cd {RUN_DIR} && ./prices/day.sh erc20_usd_minute erc20_usd_day"
    )

    ethereum_prices_erc20_usd_minute >> [ethereum_prices_erc20_usd_hour, ethereum_prices_erc20_usd_day]


    ethereum_dex_token_trades = BashOperator(
        task_id="ethereum_dex.token_trades",
        bash_command=f"cd {RUN_DIR} && ./dex/token_trades.sh weth_usd_minute 0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2 all "
    )

    [ethereum_dex_swap_v2_trades, ethereum_dex_swap_v3_trades, ethereum_prices_weth_usd_minute] >> ethereum_dex_token_trades



