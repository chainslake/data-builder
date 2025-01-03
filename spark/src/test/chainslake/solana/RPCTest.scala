package chainslake.solana

import com.google.gson.Gson
import org.scalatest.funsuite.AnyFunSuite
import scalaj.http.Http

class RPCTest extends AnyFunSuite {
  val rpcUrl = "https://solana-rpc.publicnode.com"

  test("Get block height") {
    val response = Http(rpcUrl).header("Content-Type", "application/json")
      .postData(s"""{"method":"getSlot","params":[],"id":1,"jsonrpc":"2.0"}""").asString
    println(response.body)
    // {"jsonrpc":"2.0","id":1,"result":311327753}
  }

  test("get blocks") {
    val response = Http(rpcUrl).header("Content-Type", "application/json")
      .postData(s"""{"method":"getBlock","params":[311388006, {"encoding": "jsonParsed","maxSupportedTransactionVersion":0,"transactionDetails":"full","rewards":true}],"id":1,"jsonrpc":"2.0"}""").asString
    val gson = new Gson()
    val extractBlock = gson.fromJson(response.body, classOf[ResponseBlock]).result
    println(extractBlock)
  }
}
