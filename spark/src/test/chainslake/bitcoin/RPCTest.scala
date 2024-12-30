package chainslake.bitcoin

import chainslake.bitcoin.origin.TransactionBlocks
import org.scalatest.funsuite.AnyFunSuite
import scalaj.http.Http

import java.sql.Timestamp
import java.util.Properties

class RPCTest extends AnyFunSuite {
  val rpcUrl = "https://bitcoin.drpc.org/"

  test("Get block height") {
    val response = Http(rpcUrl).header("Content-Type", "application/json")
      .postData(s"""{"method":"getblockcount","params":[],"id":"curltest","jsonrpc":"1.0"}""").asString
    println(response.body)
    // {"id":"curltest","jsonrpc":"2.0","result":876818}
  }

  test("Get block hash") {
    val blockNumber = 876818
    val response = Http(rpcUrl).header("Content-Type", "application/json")
      .postData(s"""{"method":"getblockhash","params":[$blockNumber],"id":"curltest","jsonrpc":"1.0"}""").asString
    println(response.body)
    // {"id":"curltest","jsonrpc":"2.0","result":"0000000000000000000114036b4c6aff5d21d097ec3620539e7e40cae729b794"}
  }

  test("Get block by block hash") {
    val blockHash = "0000000000000000000114036b4c6aff5d21d097ec3620539e7e40cae729b794"
    val response = Http(rpcUrl).header("Content-Type", "application/json")
      .postData(s"""{"method":"getblock","params":["$blockHash", 2],"id":"curltest","jsonrpc":"1.0"}""").asString
    println(response.body)
    // {
    //    "id": "curltest",
    //    "jsonrpc": "2.0",
    //    "result": {
    //        "hash": "0000000000000000000114036b4c6aff5d21d097ec3620539e7e40cae729b794",
    //        "confirmations": 1,
    //        "height": 876818,
    //        "version": 538263552,
    //        "versionHex": "20154000",
    //        "merkleroot": "d346c2e20e8e8e862bea595b2161d9784c6d6b49f5f30666b892722ac7c4846d",
    //        "time": 1735431666,
    //        "mediantime": 1735430437,
    //        "nonce": 3460091701,
    //        "bits": "170297fa",
    //        "difficulty": 108522647629298.2,
    //        "chainwork": "0000000000000000000000000000000000000000a39e7f2ad9ffb7895c331191",
    //        "nTx": 1693,
    //        "previousblockhash": "000000000000000000017247404a119a5474df9342f95174ec920fc0a1fabe35",
    //        "strippedsize": 650214,
    //        "size": 2043078,
    //        "weight": 3993720,
    //        "tx": [
    //            {
    //                "txid": "7bf7fdb69547f837334247f9ab75e9f41951e6cf859bf3907ac8ac9f2f5bfd20",
    //                "hash": "fa124307bcf9d44794bdecdb203b8af83e441eb17b7beab28c491174f608b555",
    //                "version": 1,
    //                "size": 421,
    //                "vsize": 394,
    //                "weight": 1576,
    //                "locktime": 0,
    //                "vin": [
    //                    {
    //                        "coinbase": "0312610d194d696e656420627920416e74506f6f6c20d40003072acd79e9fabe6d6d7560998d842d25de37a1aeb9ca4f9dd875a961caa0fd829cfc64b47b3695d212100000000000000000005511596c000000000000",
    //                        "txinwitness": [
    //                            "0000000000000000000000000000000000000000000000000000000000000000"
    //                        ],
    //                        "sequence": 4294967295
    //                    }
    //                ],
    //                "vout": [
    //                    {
    //                        "value": 0.00000546,
    //                        "n": 0,
    //                        "scriptPubKey": {
    //                            "asm": "OP_HASH160 42402a28dd61f2718a4b27ae72a4791d5bbdade7 OP_EQUAL",
    //                            "desc": "addr(37jKPSmbEGwgfacCr2nayn1wTaqMAbA94Z)#avhxp88d",
    //                            "hex": "a91442402a28dd61f2718a4b27ae72a4791d5bbdade787",
    //                            "address": "37jKPSmbEGwgfacCr2nayn1wTaqMAbA94Z",
    //                            "type": "scripthash"
    //                        }
    //                    },
    //                    {
    //                        "value": 3.14994304,
    //                        "n": 1,
    //                        "scriptPubKey": {
    //                            "asm": "OP_HASH160 5249bdf2c131d43995cff42e8feee293f79297a8 OP_EQUAL",
    //                            "desc": "addr(39C7fxSzEACPjM78Z7xdPxhf7mKxJwvfMJ)#vjljy0jc",
    //                            "hex": "a9145249bdf2c131d43995cff42e8feee293f79297a887",
    //                            "address": "39C7fxSzEACPjM78Z7xdPxhf7mKxJwvfMJ",
    //                            "type": "scripthash"
    //                        }
    //                    },
    //                    {
    //                        "value": 0.00000000,
    //                        "n": 2,
    //                        "scriptPubKey": {
    //                            "asm": "OP_RETURN aa21a9edb924c436c3ef132f6ea6ff809cf3cd0bb02b0e3fa82eceba83e44ca6b02d4cd6",
    //                            "desc": "raw(6a24aa21a9edb924c436c3ef132f6ea6ff809cf3cd0bb02b0e3fa82eceba83e44ca6b02d4cd6)#vesplnr2",
    //                            "hex": "6a24aa21a9edb924c436c3ef132f6ea6ff809cf3cd0bb02b0e3fa82eceba83e44ca6b02d4cd6",
    //                            "type": "nulldata"
    //                        }
    //                    },
    //                    {
    //                        "value": 0.00000000,
    //                        "n": 3,
    //                        "scriptPubKey": {
    //                            "asm": "OP_RETURN 434f5245012953559db5cc88ab20b1960faa9793803d0703374e3ecda72cb7961caa4b541b1e322bcfe0b5a030",
    //                            "desc": "raw(6a2d434f5245012953559db5cc88ab20b1960faa9793803d0703374e3ecda72cb7961caa4b541b1e322bcfe0b5a030)#jcuzcwf4",
    //                            "hex": "6a2d434f5245012953559db5cc88ab20b1960faa9793803d0703374e3ecda72cb7961caa4b541b1e322bcfe0b5a030",
    //                            "type": "nulldata"
    //                        }
    //                    },
    //                    {
    //                        "value": 0.00000000,
    //                        "n": 4,
    //                        "scriptPubKey": {
    //                            "asm": "OP_RETURN 455853415401000d130f0e0e0b041f120013",
    //                            "desc": "raw(6a12455853415401000d130f0e0e0b041f120013)#39a8xnqr",
    //                            "hex": "6a12455853415401000d130f0e0e0b041f120013",
    //                            "type": "nulldata"
    //                        }
    //                    },
    //                    {
    //                        "value": 0.00000000,
    //                        "n": 5,
    //                        "scriptPubKey": {
    //                            "asm": "OP_RETURN 52534b424c4f434b3a0b405a48345013166182eafb7c769aa9b1ec150f4692f18d557a0e12006be388",
    //                            "desc": "raw(6a2952534b424c4f434b3a0b405a48345013166182eafb7c769aa9b1ec150f4692f18d557a0e12006be388)#mc09jllr",
    //                            "hex": "6a2952534b424c4f434b3a0b405a48345013166182eafb7c769aa9b1ec150f4692f18d557a0e12006be388",
    //                            "type": "nulldata"
    //                        }
    //                    }
    //                ],
    //                "hex": "010000000001010000000000000000000000000000000000000000000000000000000000000000ffffffff560312610d194d696e656420627920416e74506f6f6c20d40003072acd79e9fabe6d6d7560998d842d25de37a1aeb9ca4f9dd875a961caa0fd829cfc64b47b3695d212100000000000000000005511596c000000000000ffffffff06220200000000000017a91442402a28dd61f2718a4b27ae72a4791d5bbdade787806ec6120000000017a9145249bdf2c131d43995cff42e8feee293f79297a8870000000000000000266a24aa21a9edb924c436c3ef132f6ea6ff809cf3cd0bb02b0e3fa82eceba83e44ca6b02d4cd600000000000000002f6a2d434f5245012953559db5cc88ab20b1960faa9793803d0703374e3ecda72cb7961caa4b541b1e322bcfe0b5a0300000000000000000146a12455853415401000d130f0e0e0b041f12001300000000000000002b6a2952534b424c4f434b3a0b405a48345013166182eafb7c769aa9b1ec150f4692f18d557a0e12006be3880120000000000000000000000000000000000000000000000000000000000000000000000000"
    //            }
    //        ]
    //    }
    //}
  }

  test("Convert number to time") {
    val timeNumber = 1735431666 * 1000L
    val time = new Timestamp(timeNumber)
    println(time)
  }

  test("Get latest input number") {
    val properties = new Properties()
    properties.setProperty("rpc_list", rpcUrl)
    properties.setProperty("max_retry", "2")
    val latestBlock = TransactionBlocks.getLatestInput(null, properties)
    println(latestBlock)
  }

  test("TransactionBlocks.getOriginBlock") {
    val result = TransactionBlocks.getOriginBlock(Array(rpcUrl), 876830, 1)
    println(result)
  }

}
