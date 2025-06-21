package chainslake.sui_origin

import chainslake.ChainslakeTest
import chainslake.sui.ResponseRawString
import chainslake.sui.origin.TransactionBlocks
import com.google.gson.Gson
import org.apache.spark.sql.SaveMode
import scalaj.http.HttpResponse

class TransactionBlocksTest extends ChainslakeTest {

  def mockRpcCall(rpc: String, body: String): HttpResponse[String] = {
    var result = ""
    if (body.contains("sui_getLatestCheckpointSequenceNumber")) {
      result = """{"jsonrpc":"2.0","id":1,"result":"159190582"}"""
    } else if (body.contains("sui_getCheckpoint")) {
      result = """{"jsonrpc":"2.0","id":1,"result":{"epoch":"800","sequenceNumber":"159190582","digest":"5n6T7hqMacwg4ZjKihTc1q8X544cH7TAbunkb7cW1ssj","networkTotalTransactions":"3716787075","previousDigest":"ChJu4f6hY3hXPg8Pjcso1hEyRWJBEQSfc9R9VDkyWS7e","epochRollingGasCostSummary":{"computationCost":"4645120360783","storageCost":"110753278776400","storageRebate":"108202148740224","nonRefundableStorageFee":"1092950997376"},"timestampMs":"1750524478197","transactions":["Fdkk374KX1xXEbZ9kVvwPhCoH1MhCMYP8qpexbbKkfpC"],"checkpointCommitments":[],"validatorSignature":"t30ezw9ay5oh5XOEj5M9hoF4OKYgJwv60XTpoUFdtKPqBC3Y1P+PiO32MYI3bMcG"}}"""
    } else {
      result = """{"jsonrpc":"2.0","id":1,"result":[{"digest":"Fdkk374KX1xXEbZ9kVvwPhCoH1MhCMYP8qpexbbKkfpC","transaction":{"data":{"messageVersion":"v1","transaction":{"kind":"ConsensusCommitPrologueV4","epoch":"800","round":"1256627","sub_dag_index":null,"commit_timestamp_ms":"1750524478062","consensus_commit_digest":"CpDnrpdDH9znKAUsDgZAfcZfyQfjgomstjuoNmEDtbnw","consensus_determined_version_assignments":{"CancelledTransactionsV2":[]},"additional_state_digest":"F3DqSvx25wJff2rzAQDBBm9z382WNuvK5JyvKNtT63fh"},"sender":"0x0000000000000000000000000000000000000000000000000000000000000000","gasData":{"payment":[{"objectId":"0x0000000000000000000000000000000000000000000000000000000000000000","version":0,"digest":"11111111111111111111111111111111"}],"owner":"0x0000000000000000000000000000000000000000000000000000000000000000","price":"1","budget":"0"}},"txSignatures":["AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=="]},"effects":{"messageVersion":"v1","status":{"status":"success"},"executedEpoch":"800","gasUsed":{"computationCost":"0","storageCost":"0","storageRebate":"0","nonRefundableStorageFee":"0"},"modifiedAtVersions":[{"objectId":"0x0000000000000000000000000000000000000000000000000000000000000006","sequenceNumber":"437860195"}],"sharedObjects":[{"objectId":"0x0000000000000000000000000000000000000000000000000000000000000006","version":437860195,"digest":"GJ9HNUuUbcFkrT2NXHeCmJc3ciWqs8C9iyhHk8M2j1AB"}],"transactionDigest":"Fdkk374KX1xXEbZ9kVvwPhCoH1MhCMYP8qpexbbKkfpC","mutated":[{"owner":{"Shared":{"initial_shared_version":1}},"reference":{"objectId":"0x0000000000000000000000000000000000000000000000000000000000000006","version":437860196,"digest":"CVoVUduSKGugcd3fgaLmfdt3b2266hj1GbpMmoCYBQTY"}}],"gasObject":{"owner":{"AddressOwner":"0x0000000000000000000000000000000000000000000000000000000000000000"},"reference":{"objectId":"0x0000000000000000000000000000000000000000000000000000000000000000","version":0,"digest":"11111111111111111111111111111111"}},"dependencies":["AG7j6e5wCJvc4mWt3bAJsKWLrFAqhCDnJVxa6zAEPJ1T"]},"events":[],"objectChanges":[{"type":"mutated","sender":"0x0000000000000000000000000000000000000000000000000000000000000000","owner":{"Shared":{"initial_shared_version":1}},"objectType":"0x2::clock::Clock","objectId":"0x0000000000000000000000000000000000000000000000000000000000000006","version":"437860196","previousVersion":"437860195","digest":"CVoVUduSKGugcd3fgaLmfdt3b2266hj1GbpMmoCYBQTY"}],"balanceChanges":[],"timestampMs":"1750524478197","checkpoint":"159190582"}]}"""
    }

    HttpResponse[String](
      body = result,
      code = 200,
      headers = Map()
    )
  }

  test("processCrawlBlocks") {
    TransactionBlocks.rpcCall = mockRpcCall
    val result = TransactionBlocks.processCrawlBlocks(spark, 159190582, 159190582, properties)
    assert(result.count() == 1)
//    result.show()
//    spark.sql(s"create database if not exists sui_origin")
//    result.write
//      .mode(SaveMode.Overwrite).format("delta")
//      .saveAsTable("sui_origin.transaction_blocks")
  }

  test("getLatestInput") {
    TransactionBlocks.rpcCall = mockRpcCall
    val result = TransactionBlocks.getLatestInput(spark, properties)
    assert(result == 159190582)
  }

  ignore("defaultRpcCall") {
    val rpc = "https://go.getblock.io/141a2a1692df4f7a8be9958ece0e2ae4"
    val gson = new Gson()

    // Get latest block
    var result = TransactionBlocks.defaultRpcCall(rpc, s"""{"jsonrpc":"2.0","method":"sui_getLatestCheckpointSequenceNumber","params":[],"id":1}""")
    println(result.body)
    val latestBlock = gson.fromJson(result.body, classOf[ResponseRawString]).result.toLong

    // Get block
    result = TransactionBlocks.defaultRpcCall(rpc, s"""{"jsonrpc":"2.0","method":"sui_getCheckpoint","params":["${latestBlock}"],"id":1}""")
    println(result.body)


    // Get transaction
    val txHash = "Fdkk374KX1xXEbZ9kVvwPhCoH1MhCMYP8qpexbbKkfpC"
    result = TransactionBlocks.defaultRpcCall(rpc, s"""{"method":"sui_multiGetTransactionBlocks","params":[["${txHash}"], {
                                                      |      "showInput": true,
                                                      |      "showRawInput": false,
                                                      |      "showEffects": true,
                                                      |      "showEvents": true,
                                                      |      "showObjectChanges": true,
                                                      |      "showBalanceChanges": true,
                                                      |      "showRawEffects": false
                                                      |    }],"id":1,"jsonrpc":"2.0"}""".stripMargin)
    println(result.body)

  }
}
