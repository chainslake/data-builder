package chainslake.bitcoin

import chainslake.bitcoin.extract.{Blocks, Inputs, Outputs, Transactions}
import chainslake.bitcoin.origin.TransactionBlocks
import chainslake.job.JobInf


object JobFactory {
  def createJob(name: String): JobInf = {
    name match {
      case "bitcoin_origin.transaction_blocks" => TransactionBlocks
      case "bitcoin.blocks" => Blocks
      case "bitcoin.transactions" => Transactions
      case "bitcoin.inputs" => Inputs
      case "bitcoin.outputs" => Outputs
    }
  }
}
