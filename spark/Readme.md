# Chainslake App

Chainslake App is a Data Application on Chainslake platform.

# Author
[Lake Nguyen](mailto:lakechain.nguyen@gmail.com) founder of [Chainslake](https://docs.chainslake.io)

## Setting up the development environment
You need to install the following dependencies:

- Scala 2.12.18
- Spark 3.5.1
- SBT 1.9.0

You can use IDE Intellij or any one you are familiar with

## How to build

```sh
$ ./build.sh
```

## How to run test

```sh
$ cd test
$ ./build.sh
$ ./run_test.sh
```

## Check result data

```shell
$ cd test
$ ./spark-sql.sh
```

```sql
spark-sql> select * from binance_cex.exchange_info;
spark-sql> select * from binance_cex.trade_minute.sh;
```

## License 
Copyright 2024 Chainslake


