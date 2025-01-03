package chainslake.solana.extract

import chainslake.job.TaskRun
import chainslake.solana.{ExtractedTokenBalanceChange, OriginBlock, ResponseBlock, TokenBalance}
import com.google.gson.Gson
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions.col

import java.util.Properties

object TokenBalances extends TaskRun {
  override def run(spark: SparkSession, properties: Properties): Unit = {
    val chainName = properties.getProperty("chain_name")
    properties.setProperty("frequent_type", "block")
    properties.setProperty("list_input_tables", chainName + "_origin.transaction_blocks")
    val database = chainName
    try {
      spark.sql(s"create database if not exists $database")
    } catch {
      case e: Exception => e.getMessage
    }
    processTable(spark, chainName + ".token_balances", properties)
  }

  override protected def onProcess(spark: SparkSession, outputTable: String, from: Long, to: Long, properties: Properties): Unit = {
    import spark.implicits._
    val inputTableName = properties.getProperty("list_input_tables")
    spark.read.table(inputTableName).where(col("block_number") >= from && col("block_number") <= to)
      .as[OriginBlock].flatMap(block => {
        val gson = new Gson()
        val extractBlock = gson.fromJson(block.block, classOf[ResponseBlock]).result
        extractBlock.transactions.flatMap(transaction => {
          val accounts = transaction.transaction.message.accountKeys.map(accountKey => accountKey.pubkey)
          val preBalances = transaction.meta.preTokenBalances
          var mapIndexPreBalance: Map[Long, TokenBalance] = Map.empty[Long, TokenBalance]
          preBalances.foreach(balance => {
            mapIndexPreBalance = mapIndexPreBalance + (balance.accountIndex -> balance)
          })
          transaction.meta.postTokenBalances.map(postBalance => {
              val extractedTokenBalance = ExtractedTokenBalanceChange(
                block.block_date,
                block.block_number,
                block.block_time,
                transaction.transaction.signatures(0),
                accounts(postBalance.accountIndex.toInt),
                postBalance.mint,
                postBalance.owner,
                postBalance.programId,
                postBalance.uiTokenAmount.decimals,
                "0",
                "0",
                0,
                postBalance.uiTokenAmount.amount,
                postBalance.uiTokenAmount.uiAmountString,
                postBalance.uiTokenAmount.uiAmount
              )
            if (mapIndexPreBalance.contains(postBalance.accountIndex)) {
              val preBalance = mapIndexPreBalance(postBalance.accountIndex)
              extractedTokenBalance.pre_amount = preBalance.uiTokenAmount.amount
              extractedTokenBalance.pre_str_ui_amount = preBalance.uiTokenAmount.uiAmountString
              extractedTokenBalance.pre_ui_amount = preBalance.uiTokenAmount.uiAmount
            }
            extractedTokenBalance
          }).filter(balance => balance.pre_ui_amount != balance.post_ui_amount)
        })
      }).repartitionByRange(col("block_date"), col("block_time"))
      .write.partitionBy("block_date")
      .mode(SaveMode.Append).format("delta")
      .saveAsTable(outputTable)
  }
}
