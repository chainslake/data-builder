package chainslake.bitcoin

import java.sql.{Date, Timestamp}

case class ResponseRawNumber(
                                   var jsonrpc: String,
                                   var id: String,
                                   var result: Long
                                 )

case class ResponseRawString(
                              var jsonrpc: String,
                              var id: String,
                              var result: String
                            )

case class OriginBlock(
                  var block_date: Date,
                  var block_number: Long,
                  var block_time: Timestamp,
                  var block: String
                )

case class ResponseRawBlock(
                             var jsonrpc: String,
                             var id: String,
                             var result: RawBlock
                        )
case class RawBlock(
                   var time: Long
                   )

// Source: https://developer.bitcoin.org/reference/rpc/getblock.html
case class TransactionBlock(
                var hash: String,
                var confirmations: Long,
                var size: Long,
                var strippedsize: Long,
                var weight: Long,
                var height: Long,
                var version: Long,
                var versionHex: String,
                var merkleroot: String,
                var tx: Array[Transaction],
                var time: Long,
                var mediantime: Long,
                var nonce: Long,
                var bits: String,
                var difficulty: Double,
                var chainwork: String,
                var nTx: Long,
                var previousblockhash: String
                )

// Source: https://developer.bitcoin.org/reference/rpc/getrawtransaction.html
case class Transaction(
                      var in_active_chain: Boolean,
                      var hex: String,
                      var txid: String,
                      var hash: String,
                      var size: Long,
                      var vsize: Long,
                      var weight: Long,
                      var version: Long,
                      var locktime: Long,
                      var vin: Array[Vin],
                      var vout: Array[Vout],
                      var blockhash: String,
                      var confirmations: Long,
                      var blocktime: Long,
                      var time: Long
                      )

case class Vin(
                         var txid: String,
                         var vout: Long,
                         var scriptSig: ScripSig,
                         var sequence: Long,
                         var txinwitness: Array[String]
                         )

case class ScripSig(
                   var asm: String,
                   var hex: String
                   )

case class Vout(
               var value: Double,
               var n: Long,
               var scriptPubKey: ScriptPubKey
               )

case class ScriptPubKey(
                       var asm: String,
                       var hex: String,
                       var reqSigs: Long,
                       var `type`: String,
                       var addresses: Array[String]
                       )