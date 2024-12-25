# Protocol Decoded

Chainslake can decode any protocol, you just need to provide the abi.json file of that protocol.

## Step by step

- First you need to copy the protocol's ABI into this `evm/abi` folder.
- Add your decode job to the pipeline, see instructions [here](/airflow/README.md).
- Commit and push your work and you're done.

## Result

Once your decoded job is executed, you will see your decoded tables in the Chainslake database [here](https://metabase.chainslake.io/browse/databases/3/schema/ethereum_decoded). The naming convention is as follows:
- [protocol_name]\_evt\_[event_name]: Event decoded table from log data
- [protocol_name]\_call\_[function_name]: Internal transactions decoded table from trace data