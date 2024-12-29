package chainslake.bitcoin

import chainslake.bitcoin.origin.TransactionBlocks
import chainslake.job.JobInf


object JobFactory {
  def createJob(name: String): JobInf = {
    name match {
      case "bitcoin_origin.transaction_blocks" => TransactionBlocks
    }
  }
}
