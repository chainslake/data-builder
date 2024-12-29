# Data pipeline

Chainslake uses [Airflow](https://airflow.apache.org/) to manage, schedule, and automate data processing jobs.

![Data pipeline](/airflow/pipeline.png)

## Add job to pipeline

You need to add jobs to the pipeline according to each chain. For example:

```python
contract_uniswap_v2_info = BashOperator(
    task_id="contract.uniswap_v2_info",
    bash_command=f"cd {RUN_DIR} && ./extract/contract_info.sh uniswap_v2"
)

decoded_uniswap_v2 >> contract_uniswap_v2_info
```