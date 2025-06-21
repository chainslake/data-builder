package bitcoin_origin

import chainslake.ChainslakeTest
import chainslake.bitcoin.{ResponseRawNumber, ResponseRawString}
import chainslake.bitcoin.origin.TransactionBlocks
import com.google.gson.Gson
import scalaj.http.{Http, HttpResponse}

class TransactionBlocksTest extends ChainslakeTest {

  def mockRpcCall(rpc: String, body: String): HttpResponse[String] = {
    var result = ""
    if (body.contains("getblockcount")) {
      result = """{"result":902068,"error":null,"id":"curltest"}"""
    } else if (body.contains("getblockhash")) {
      result = """{"result":"000000000000000000007ba8c6fa15abc381c10dc26968b15d7fb5937bece047","error":null,"id":"curltest"}"""
    } else {
      result = """{"result":{"hash":"000000000000000000007ba8c6fa15abc381c10dc26968b15d7fb5937bece047","confirmations":1,"height":902068,"version":664272896,"versionHex":"27980000","merkleroot":"7ab6ee3124ecb3d42d228734a64fd3c3fdb497c97c3d7ac1bf0eb374c5d96ed2","time":1750434033,"mediantime":1750431352,"nonce":810937354,"bits":"17023a04","target":"000000000000000000023a040000000000000000000000000000000000000000","difficulty":126411437451912.2,"chainwork":"0000000000000000000000000000000000000000cc5f032b43f45c2b9638175c","nTx":3172,"previousblockhash":"0000000000000000000088355ae10f4c926cb9b5dc5e84dd47f7f4278be1686d","strippedsize":798587,"size":1597998,"weight":3993759,"tx":[{"txid":"c4bba7f31794683c5a90f17d3a83c5fe2b718bd14e6f28ef48f9bbebd5b07785","hash":"5d6e1dabfa5296bb4fa53b2c0a8c7cc17ab455c32acd1ac7d359a1fb555154fa","version":1,"size":421,"vsize":394,"weight":1576,"locktime":0,"vin":[{"coinbase":"03b4c30d194d696e656420627920416e74506f6f6c206e00510477e09e6ffabe6d6d23340a65c7881151a796454b8b77bb8ee10f099b7767ac00c99f19fa0a7755fd100000000000000000001c0f2912010000000000","txinwitness":["0000000000000000000000000000000000000000000000000000000000000000"],"sequence":4294967295}],"vout":[{"value":0.00000546,"n":0,"scriptPubKey":{"asm":"OP_HASH160 42402a28dd61f2718a4b27ae72a4791d5bbdade7 OP_EQUAL","desc":"addr(37jKPSmbEGwgfacCr2nayn1wTaqMAbA94Z)#avhxp88d","hex":"a91442402a28dd61f2718a4b27ae72a4791d5bbdade787","address":"37jKPSmbEGwgfacCr2nayn1wTaqMAbA94Z","type":"scripthash"}},{"value":3.16796707,"n":1,"scriptPubKey":{"asm":"OP_HASH160 5249bdf2c131d43995cff42e8feee293f79297a8 OP_EQUAL","desc":"addr(39C7fxSzEACPjM78Z7xdPxhf7mKxJwvfMJ)#vjljy0jc","hex":"a9145249bdf2c131d43995cff42e8feee293f79297a887","address":"39C7fxSzEACPjM78Z7xdPxhf7mKxJwvfMJ","type":"scripthash"}},{"value":0,"n":2,"scriptPubKey":{"asm":"OP_RETURN aa21a9ed1f3df06f2fc331f9dfa91aa42ebff2ec4a9efe6a8fc8c094b177c9d5114badb0","desc":"raw(6a24aa21a9ed1f3df06f2fc331f9dfa91aa42ebff2ec4a9efe6a8fc8c094b177c9d5114badb0)#e3tjnw4f","hex":"6a24aa21a9ed1f3df06f2fc331f9dfa91aa42ebff2ec4a9efe6a8fc8c094b177c9d5114badb0","type":"nulldata"}},{"value":0,"n":3,"scriptPubKey":{"asm":"OP_RETURN 434f524501a37cf4faa0758b26dca666f3e36d42fa15cc01064e3ecda72cb7961caa4b541b1e322bcfe0b5a030","desc":"raw(6a2d434f524501a37cf4faa0758b26dca666f3e36d42fa15cc01064e3ecda72cb7961caa4b541b1e322bcfe0b5a030)#djrqagdj","hex":"6a2d434f524501a37cf4faa0758b26dca666f3e36d42fa15cc01064e3ecda72cb7961caa4b541b1e322bcfe0b5a030","type":"nulldata"}},{"value":0,"n":4,"scriptPubKey":{"asm":"OP_RETURN 455853415401000d130f0e0e0b041f120013","desc":"raw(6a12455853415401000d130f0e0e0b041f120013)#39a8xnqr","hex":"6a12455853415401000d130f0e0e0b041f120013","type":"nulldata"}},{"value":0,"n":5,"scriptPubKey":{"asm":"OP_RETURN 52534b424c4f434b3a22b5b181f4a8fd9aa1cce8e87b5d2e1b90cab5787628209f2232b9100075599c","desc":"raw(6a2952534b424c4f434b3a22b5b181f4a8fd9aa1cce8e87b5d2e1b90cab5787628209f2232b9100075599c)#m85x8q55","hex":"6a2952534b424c4f434b3a22b5b181f4a8fd9aa1cce8e87b5d2e1b90cab5787628209f2232b9100075599c","type":"nulldata"}}],"hex":"010000000001010000000000000000000000000000000000000000000000000000000000000000ffffffff5603b4c30d194d696e656420627920416e74506f6f6c206e00510477e09e6ffabe6d6d23340a65c7881151a796454b8b77bb8ee10f099b7767ac00c99f19fa0a7755fd100000000000000000001c0f2912010000000000ffffffff06220200000000000017a91442402a28dd61f2718a4b27ae72a4791d5bbdade78723efe1120000000017a9145249bdf2c131d43995cff42e8feee293f79297a8870000000000000000266a24aa21a9ed1f3df06f2fc331f9dfa91aa42ebff2ec4a9efe6a8fc8c094b177c9d5114badb000000000000000002f6a2d434f524501a37cf4faa0758b26dca666f3e36d42fa15cc01064e3ecda72cb7961caa4b541b1e322bcfe0b5a0300000000000000000146a12455853415401000d130f0e0e0b041f12001300000000000000002b6a2952534b424c4f434b3a22b5b181f4a8fd9aa1cce8e87b5d2e1b90cab5787628209f2232b9100075599c0120000000000000000000000000000000000000000000000000000000000000000000000000"},{"txid":"d98e61bb65ef32660477494a5493a92525ed14dc663f07ca6e54c38b3ca93108","hash":"52c75b7477d17aa60c6e8de7d1537abdae03baae23c1291cb6b9cabc3a22ab9d","version":1,"size":223,"vsize":141,"weight":562,"locktime":0,"vin":[{"txid":"9ddb9b820fa0561a9b08a7d248cd980c1034189d30a4548d3178a37e9043a512","vout":0,"scriptSig":{"asm":"","hex":""},"txinwitness":["3045022100d102103823d8a7bec6884969f1921e9bf5a98ffb2cfa29ce1ea5ab9ccdcfe46a02205f2f80dcfa348a0ee92864dfa5d503d82243404bf7b4589c72f642c76924cc1901","0382214dc8a66d83e5a6954c4eb11af1425a7c0d11acdec99168d9c647d33f8042"],"sequence":4294967295}],"vout":[{"value":0.88151848,"n":0,"scriptPubKey":{"asm":"0 5e4a808a98c8c093a883361ba6b0c4c9c711c53f","desc":"addr(bc1qte9gpz5cerqf82yrxcd6dvxye8r3r3flqqr6xw)#7p6xla6z","hex":"00145e4a808a98c8c093a883361ba6b0c4c9c711c53f","address":"bc1qte9gpz5cerqf82yrxcd6dvxye8r3r3flqqr6xw","type":"witness_v0_keyhash"}},{"value":0.0081727,"n":1,"scriptPubKey":{"asm":"0 37ed8302f84c4f30bca43d36a5681170824b38a7","desc":"addr(bc1qxlkcxqhcf38np09y85m226q3wzpykw98r08jc2)#vzrtpg64","hex":"001437ed8302f84c4f30bca43d36a5681170824b38a7","address":"bc1qxlkcxqhcf38np09y85m226q3wzpykw98r08jc2","type":"witness_v0_keyhash"}}],"fee":0.000355,"hex":"0100000000010112a543907ea378318d54a4309d1834100c98cd48d2a7089b1a56a00f829bdb9d0000000000ffffffff0228174105000000001600145e4a808a98c8c093a883361ba6b0c4c9c711c53f76780c000000000016001437ed8302f84c4f30bca43d36a5681170824b38a702483045022100d102103823d8a7bec6884969f1921e9bf5a98ffb2cfa29ce1ea5ab9ccdcfe46a02205f2f80dcfa348a0ee92864dfa5d503d82243404bf7b4589c72f642c76924cc1901210382214dc8a66d83e5a6954c4eb11af1425a7c0d11acdec99168d9c647d33f804200000000"}]},"error":null,"id":"curltest"}"""
    }

    HttpResponse[String](
      body = result,
      code = 200,
      headers = Map()
    )
  }

  test("processCrawlBlocks") {
    TransactionBlocks.rpcCall = mockRpcCall
    val result = TransactionBlocks.processCrawlBlocks(spark, 902068, 902068, properties).collect()
    assert(result.length == 1)
  }

  test("getLatestInput") {
    TransactionBlocks.rpcCall = mockRpcCall
    val result = TransactionBlocks.getLatestInput(spark, properties)
    assert(result == 902068)
  }

  ignore("rpcCall") {
    val rpc = ""
    val gson = new Gson()
    def rpcCall(url: String, body: String): HttpResponse[String] = {
      Http(url).header("Content-Type", "application/json")
        .postData(body)
        .timeout(50000, 50000)
        .asString
    }

    // Get latest block
    var result = rpcCall(rpc, s"""{"method":"getblockcount","params":[],"id":"curltest","jsonrpc":"1.0"}""")
    println(result.body)
    val latestBlock = gson.fromJson(result.body, classOf[ResponseRawNumber]).result

    // Get block hash
    result = rpcCall(rpc, s"""{"method":"getblockhash","params":[$latestBlock],"id":"curltest","jsonrpc":"1.0"}""")
    println(result.body)

    val blockHash = gson.fromJson(result.body, classOf[ResponseRawString]).result

    // Get block
    result = rpcCall(rpc, s"""{"method":"getblock","params":["$blockHash", 2],"id":"curltest","jsonrpc":"1.0"}""")
    println(result.body)

  }
}
