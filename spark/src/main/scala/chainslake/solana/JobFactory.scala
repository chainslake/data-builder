package chainslake.solana

import chainslake.solana.origin.TransactionBlocks
import chainslake.job.JobInf
import chainslake.solana.extract.{Blocks, Instructions, NativeBalances, Rewards, TokenBalances, Transactions}


object JobFactory {
  def createJob(name: String): JobInf = {
    name match {
      case "solana_origin.transaction_blocks" => TransactionBlocks
      case "solana.blocks" => Blocks
      case "solana.rewards" => Rewards
      case "solana.transactions" => Transactions
      case "solana.native_balances" => NativeBalances
      case "solana.token_balances" => TokenBalances
      case "solana.instructions" => Instructions
    }
  }
}
