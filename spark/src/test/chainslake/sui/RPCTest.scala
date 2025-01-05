package chainslake.sui

import com.google.gson.Gson
import org.scalatest.funsuite.AnyFunSuite
import scalaj.http.Http

class RPCTest extends AnyFunSuite {
  val rpcUrl = "http://node-prod.chainslake:1800/sui3"

  test("Get latest block") {
    val response = Http(rpcUrl).header("Content-Type", "application/json")
      .postData(s"""{"jsonrpc":"2.0","method":"sui_getLatestCheckpointSequenceNumber","params":[],"id":1}""").asString
    println(response.body)
    // {"jsonrpc":"2.0","result":"97362177","id":1}
  }

  test("Get checkpoint") {
    val blockNumber = 97937484
    val response = Http(rpcUrl).header("Content-Type", "application/json")
      .postData(s"""{"jsonrpc":"2.0","method":"sui_getCheckpoint","params":["${blockNumber}"],"id":1}""").asString
    val gson = new Gson()
    val responseRawBlock = gson.fromJson(response.body, classOf[ResponseRawBlock])
    responseRawBlock.result.transactions.grouped(10).foreach(trans => {
      println(trans.mkString(","))
    })
//    println(response.body)
    // {
    //    "jsonrpc": "2.0",
    //    "result": {
    //        "epoch": "631",
    //        "sequenceNumber": "97362177",
    //        "digest": "Gu3YqCr8McW8ygv1gVaevBMudK1v1jf5ZV8DNjKD6GAg",
    //        "networkTotalTransactions": "2907438061",
    //        "previousDigest": "DjMztgrgwPMUQJ8wSs32FCJLqbtM3UDzuaKsuTv4qpqZ",
    //        "epochRollingGasCostSummary": {
    //            "computationCost": "4420072527899",
    //            "storageCost": "40105761260000",
    //            "storageRebate": "39326163796260",
    //            "nonRefundableStorageFee": "397233977740"
    //        },
    //        "timestampMs": "1735872769797",
    //        "transactions": [
    //            "FwwPTGGiAMUw584mLn595W75qJzTLdAuL78H3Vm2Zz3V",
    //            "7VoztVstqYucmJjGaWMcmas6NMURTW2Kv5BPgZbgDDuj",
    //            "2bFgcpKVkgq75559pyKnym6daA5cEANJfrgcZ9ES1WWJ",
    //            "76b96HEw3Mr2fX6LEZrkC28JqrtYn8UvmtGWwecCQNQb",
    //            "3EYegU7uM84uVzdLSWk8YKq5XqLJCqHe4dqsFBrMpgRk",
    //            "4K6oTJavzSPpoKPhHdwkxU4UmngKi1VL8us6Rc71SHsm",
    //            "6eLWiCXgys43gjErYmX8bxKJkajKbRKFXejfGLN6t7Sz",
    //            "13FehD6J3SbSubyQV8iPHBWVgSWcQHy1cv6CGMShHQdd",
    //            "BmMyDXurM8nYyfocmCdpTkA9dYdCYbZfieDcgVdMxcTd"
    //        ],
    //        "checkpointCommitments": [],
    //        "validatorSignature": "hi5Py+WCFzYqM3XUZ/uOH1bjb/tefhUURfVNhOzKiHXnqHb0+eCNZltQvTEf/e1t"
    //    },
    //    "id": 1
    //}
  }

  test("Get transaction ") {
    val response = Http(rpcUrl).header("Content-Type", "application/json")
      .postData(s"""{"method":"sui_multiGetTransactionBlocks","params":[["FTefC9gvFWAZaDLRCjtnwbCwiYPVY2A2K7QGhKbxcETR"], {
                   |      "showInput": true,
                   |      "showRawInput": false,
                   |      "showEffects": true,
                   |      "showEvents": true,
                   |      "showObjectChanges": true,
                   |      "showBalanceChanges": true,
                   |      "showRawEffects": false
                   |    }],"id":1,"jsonrpc":"2.0"}""".stripMargin).asString

//    val gson = new Gson()

//    gson.fromJson(block.transactions, classOf[Array[Transaction]])
    println(response.body)
  }


}
