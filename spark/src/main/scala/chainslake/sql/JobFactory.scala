package chainslake.sql

import chainslake.job.JobInf

object JobFactory {
  def createJob(name: String): JobInf = {
    name match {
      case "sql.transformer" => Transformer

    }
  }
}
