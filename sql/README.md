# Building businees table using SQL

You can build new data tables by combining and aggregating data from multiple existing tables, using the SQL language.

## Step by step

1. Create SQL file in `sql` folder. An sql file consists of two components separated by the symbol `===`. The top part is the configuration for the table, the bottom part is the SQL that defines the table. We will go into more detail later in this document.
2. Create a job to execute table creation in the `jobs` folder, see details [here](/jobs/README.md)
3. Add your SQL job to the pipeline, see instructions [here](/airflow/README.md).
4. Commit and push your work and you're done.

## Configuration

- __frequent_type__: Frequency of data update for table, there are some options as follows: `block`, `minute`, `hour`, `day`
- __list_input_tables__: List of input tables to build this table.
- __output_table__: Name of the output table
- __write_mode__: The configuration determines whether the table will be `Overwritte` or `Append`
- __re_partition_by_range__: Sort data according to these columns before writing to the table.
- __number_index_columns__: Number of columns with index counting from the first column
- __partition_by__: Columns are used to partition data.
- __merge_by__: Columns are used to update results into the table if the table already exists, Note: always accompanied by write_mote=Overwrite
- __is_vacuum__: `true` or `false`, default is `false`, used to delete unused files in the table, used with __merge_by__ or write_mote=Overwrite
- Other configurations can be defined for use in bellow SQL queries, such as input table names.

## SQL

The sql statement can use some variables as follows:

- __${from}__, __${to}__: Determine the range of data to be processed, avoid having to read the entire data table, instead only process a certain segment of data in each execution. The way these two variables are handled is different for different __frequent_type__. See examples in the files `evm_dex/swap_v2_trades.sql` and `evm_balances/nft_transfer_day.sql`.
- __${table_existed}__: The return variable `true` or `false` indicates whether the output table exists or not, often used with __merge_by__ config.
- Other variables are defined in the configuration section above, usually input table names.

