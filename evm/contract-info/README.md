## Get contract info

Chainslake can help you get the necessary information from contracts and save them in the corresponding table. 

## Step by step

- First you need to create a configuration file in the `contract-info` folder. The configuration file consists of two parts separated by `===`. In the first part, you need to provide a `list_input_tables` names, these are usually decoded tables and `output_table` name. In part 2 you need to provide the information fields you need to get from the contract in json format.
- Add your get contract info job to the pipeline, see instructions [here](/airflow/README.md).
- Commit and push your work and you're done.

## Result

Once your get contract info job is executed, you will see your contract info table in the Chainslake database [here](https://metabase.chainslake.io/browse/databases/3/schema/ethereum_contract){:target="_blank"}
