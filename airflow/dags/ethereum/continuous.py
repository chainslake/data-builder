# The DAG object; we'll need this to instantiate a DAG
from airflow.models.dag import DAG
from datetime import datetime
# Operators; we need this to operate!
from airflow.operators.bash import BashOperator
import os
with DAG(
    "ethereum_continuous",
    # These args will get passed on to each operator
    # You can override them on a per-task basis during operator initialization
    default_args={
        "depends_on_past": True,
        'wait_for_downstream': False,
        "email": ["lakechain.nguyen@gmail.com"],
        "email_on_failure": True,
        "email_on_retry": False,
        "retries": 0
    },
    description="Ethereum continuous",
    start_date=datetime(2024, 9, 20),
    # schedule="@continuous",
    schedule="@once",
    max_active_runs=1,
) as dag:

    RUN_DIR = os.environ.get("CHAINSLAKE_HOME_DIR") + "/jobs/ethereum"

    ########################### ORIGIN ##########################################

    origin_transaction_blocks = BashOperator(
        task_id="origin.transaction_blocks",
        bash_command=f"cd {RUN_DIR} && ./origin/transaction_blocks.sh "
    )

    origin_blocks_receipt = BashOperator(
        task_id="origin.blocks_receipt",
        bash_command=f"cd {RUN_DIR} && ./origin/blocks_receipt.sh "
    )

    origin_traces = BashOperator(
        task_id="origin.traces",
        bash_command=f"cd {RUN_DIR} && ./origin/traces.sh "
    )

    origin_transaction_blocks >> [origin_blocks_receipt, origin_traces]

    ############################################## EXTRACT #############################

    extract_logs = BashOperator(
        task_id="logs",
        bash_command=f"cd {RUN_DIR} && ./extract/logs.sh "
    )

    extract_transactions = BashOperator(
        task_id="transactions",
        bash_command=f"cd {RUN_DIR} && ./extract/transactions.sh "
    )

    origin_blocks_receipt >> [extract_transactions, extract_logs]

    extract_traces = BashOperator(
        task_id="traces",
        bash_command=f"cd {RUN_DIR} && ./extract/traces.sh "
    )


    [origin_traces, extract_transactions] >> extract_traces

    ############################################# DECODED ###########################################

    decoded_erc20 = BashOperator(
        task_id="decoded.erc20",
        bash_command=f"cd {RUN_DIR} && ./extract/decoded.sh erc20"
    )

    decoded_erc721 = BashOperator(
        task_id="decoded.erc721",
        bash_command=f"cd {RUN_DIR} && ./extract/decoded.sh erc721"
    )

    decoded_erc1155 = BashOperator(
        task_id="decoded.erc1155",
        bash_command=f"cd {RUN_DIR} && ./extract/decoded.sh erc1155"
    )

    decoded_uniswap_v2 = BashOperator(
        task_id="decoded.uniswap_v2",
        bash_command=f"cd {RUN_DIR} && ./extract/decoded.sh uniswap_v2"
    )

    decoded_uniswap_v3 = BashOperator(
        task_id="decoded.uniswap_v3",
        bash_command=f"cd {RUN_DIR} && ./extract/decoded.sh uniswap_v3"
    )

    extract_logs >> [decoded_erc20, decoded_erc721, decoded_erc1155, decoded_uniswap_v2, decoded_uniswap_v3]

    ################################################### CONTRACT INFO ##########################################

    contract_erc20_tokens = BashOperator(
        task_id="contract.erc20_tokens",
        bash_command=f"cd {RUN_DIR} && ./extract/contract_info.sh erc20"
    )

    decoded_erc20 >> contract_erc20_tokens

    contract_erc721_tokens = BashOperator(
        task_id="contract.erc721_tokens",
        bash_command=f"cd {RUN_DIR} && ./extract/contract_info.sh erc721"
    )

    decoded_erc721 >> contract_erc721_tokens

    contract_erc1155_tokens = BashOperator(
        task_id="contract.erc1155_tokens",
        bash_command=f"cd {RUN_DIR} && ./extract/contract_info.sh erc1155"
    )

    decoded_erc1155 >> contract_erc1155_tokens

    ######################################################## SQL ############################################

    balances_token_transfer_hour = BashOperator(
        task_id="balances.token_transfer_hour",
        bash_command=f"cd {RUN_DIR} && ./balances/token_transfer_hour.sh "
    )

    balances_token_transfer_day = BashOperator(
        task_id="balances.token_transfer_day",
        bash_command=f"cd {RUN_DIR} && ./balances/token_transfer_day.sh "
    )

    balances_token_latest_day = BashOperator(
        task_id="balances.token_latest_day",
        bash_command=f"cd {RUN_DIR} && ./balances/token_latest_day.sh "
    )

    [contract_erc20_tokens, extract_traces] >> balances_token_transfer_hour >> balances_token_transfer_day >> balances_token_latest_day

    balances_nft_transfer_hour = BashOperator(
        task_id="balances.nft_transfer_hour",
        bash_command=f"cd {RUN_DIR} && ./balances/nft_transfer_hour.sh "
    )

    balances_nft_transfer_day = BashOperator(
        task_id="balances.nft_transfer_day",
        bash_command=f"cd {RUN_DIR} && ./balances/nft_transfer_day.sh "
    )

    balances_nft_latest_day = BashOperator(
        task_id="balances.nft_latest_day",
        bash_command=f"cd {RUN_DIR} && ./balances/nft_latest_day.sh "
    )

    [contract_erc721_tokens, contract_erc1155_tokens] >> balances_nft_transfer_hour >> balances_nft_transfer_day >> balances_nft_latest_day

    contract_uniswap_v2_info = BashOperator(
        task_id="contract.uniswap_v2_info",
        bash_command=f"cd {RUN_DIR} && ./extract/contract_info.sh uniswap_v2"
    )

    decoded_uniswap_v2 >> contract_uniswap_v2_info

    contract_uniswap_v3_info = BashOperator(
        task_id="contract.uniswap_v3_info",
        bash_command=f"cd {RUN_DIR} && ./extract/contract_info.sh uniswap_v3"
    )

    decoded_uniswap_v3 >> contract_uniswap_v3_info

    dex_swap_v2_trades = BashOperator(
        task_id="dex.swap_v2_trades",
        bash_command=f"cd {RUN_DIR} && ./dex/swap_v2_trades.sh "
    )

    [contract_uniswap_v2_info, contract_erc20_tokens] >> dex_swap_v2_trades

    dex_swap_v3_trades = BashOperator(
        task_id="dex.swap_v3_trades",
        bash_command=f"cd {RUN_DIR} && ./dex/swap_v3_trades.sh "
    )

    [contract_uniswap_v3_info, contract_erc20_tokens] >> dex_swap_v3_trades


    prices_weth_usd_minute = BashOperator(
        task_id="prices.weth_usd_minute",
        bash_command=f"cd {RUN_DIR} && ./prices/minute.sh weth_usd_minute 0xdac17f958d2ee523a2206206994597c13d831ec7,0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48 0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2 "
    )

    [dex_swap_v2_trades, dex_swap_v3_trades] >> prices_weth_usd_minute


    prices_erc20_weth_minute = BashOperator(
        task_id="prices.erc20_weth_minute",
        bash_command=f"cd {RUN_DIR} && ./prices/minute.sh erc20_weth_minute 0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2 all "
    )

    [dex_swap_v2_trades, dex_swap_v3_trades] >> prices_erc20_weth_minute

    
    prices_erc20_usd_minute = BashOperator(
        task_id="prices.erc20_usd_minute",
        bash_command=f"cd {RUN_DIR} && ./prices/usd_minute.sh "
    )

    [prices_erc20_weth_minute, prices_weth_usd_minute] >> prices_erc20_usd_minute

    prices_erc20_usd_hour = BashOperator(
        task_id="prices.erc20_usd_hour",
        bash_command=f"cd {RUN_DIR} && ./prices/hour.sh erc20_usd_minute erc20_usd_hour "
    )

    prices_erc20_usd_day = BashOperator(
        task_id="prices.erc20_usd_day",
        bash_command=f"cd {RUN_DIR} && ./prices/day.sh erc20_usd_minute erc20_usd_day"
    )

    prices_erc20_usd_minute >> [prices_erc20_usd_hour, prices_erc20_usd_day]


    dex_token_trades = BashOperator(
        task_id="dex.token_trades",
        bash_command=f"cd {RUN_DIR} && ./dex/token_trades.sh weth_usd_minute 0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2 all "
    )

    [dex_swap_v2_trades, dex_swap_v3_trades, prices_weth_usd_minute] >> dex_token_trades



