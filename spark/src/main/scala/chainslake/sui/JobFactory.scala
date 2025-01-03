package chainslake.sui

import chainslake.job.JobInf
import chainslake.sui.extract.{Blocks, Transactions, Events, ObjectChanges, BalanceChanges}
import chainslake.sui.origin.TransactionBlocks



object JobFactory {
  def createJob(name: String): JobInf = {
    name match {
      case "sui_origin.transaction_blocks" => TransactionBlocks
      case "sui.blocks" => Blocks
      case "sui.transactions" => Transactions
      case "sui.events" => Events
      case "sui.object_changes" => ObjectChanges
      case "sui.balance_changes" => BalanceChanges
    }
  }
}
