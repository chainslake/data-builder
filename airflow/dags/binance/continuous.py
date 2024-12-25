# The DAG object; we'll need this to instantiate a DAG
from airflow.models.dag import DAG
from datetime import datetime
# Operators; we need this to operate!
from airflow.operators.bash import BashOperator
import os
with DAG(
    "binance_continuous",
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
    description="Binance continuous",
    start_date=datetime(2024, 11, 7),
    schedule="@continuous",
    max_active_runs=1,
) as dag:

    RUN_DIR = os.environ.get("CHAINSLAKE_HOME_DIR") + "/jobs/binance"

    cex_exchange_info = BashOperator(
        task_id="cex.exchange_info",
        bash_command=f"cd {RUN_DIR} && ./cex/exchange_info.sh "
    )

    cex_trade_minute = BashOperator(
        task_id="cex.trade_minute",
        bash_command=f"cd {RUN_DIR} && ./cex/trade_minute.sh "
    )

    cex_trade_minute_agg_volume = BashOperator(
        task_id="cex.trade_minute_agg_volume",
        bash_command=f"cd {RUN_DIR} && ./cex/trade_minute_agg_volume.sh "
    )
    cex_exchange_info >> cex_trade_minute >> cex_trade_minute_agg_volume