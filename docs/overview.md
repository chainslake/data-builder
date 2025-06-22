# Chainslake

## Introduction

This is Chainslake's data catalog document, it will help you look up information, descriptions of data tables, transformation relationships between tables, how they are created (with source code).

## Naming convention

Tables are named meaningfully and organized in folders (schema). 

```
Table name = [schema_name].[table_name]
```

Each schema contains tables that share the same business or the same creation method, typically:

```
Schema name = [chainname]_[business_name]
```

Example: 
- *bitcoin_origin*, *ethereum_origin*, *sui_origin*, *solana_origin*: Contains the original data tables, fetched directly from the RPC node
- *bitcoin*, *ethereum*, *sui*, *solana*: Contains data tables extracted from original data, usually transactions, blocks...
- *ethereum_decoded*: The schema contains tables decoded from ethereum's logs or traces tables using the ABI. The tables in this schema have the following naming convention:

```
[protocolname]_evt_[eventname]: For event tables decoded from the logs table
[protocolname]_call_[functionname]: For call tables decoded from the traces table
```


