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

case class ResponseBlock(var jsonrpc: String,
                         var id: String,
                         var result: TransactionBlock)

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
                      var time: Long,
                      var fee: Double
                      )

case class Vin(
                         var txid: String,
                         var coinbase: String,
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
                       var addresses: Array[String],
                       var address: String
                       )

case class ExtractedBlock(
                           var block_date: Date,
                           var block_number: Long,
                           var block_time: Timestamp,
                           var hash: String,
                           var size: Long,
                           var stripped_size: Long,
                           var weight: Long,
                           var version: Long,
                           var version_hex: String,
                           var merkle_root: String,
                           var median_time: Timestamp,
                           var nonce: Long,
                           var bits: String,
                           var difficulty: Double,
                           var chain_work: String,
                           var n_tx: Long,
                           var previous_block_hash: String
                         )

case class ExtractedTransaction(
                                 var block_date: Date,
                                 var block_number: Long,
                                 var block_time: Timestamp,
                                 var in_active_chain: Boolean,
                                 var hex: String,
                                 var tx_id: String,
                                 var hash: String,
                                 var size: Long,
                                 var v_size: Long,
                                 var weight: Long,
                                 var version: Long,
                                 var lock_time: Long,
                                 var block_hash: String,
                                 var fee: Double
                               )

case class ExtractedInput(
                           var block_date: Date,
                           var block_number: Long,
                           var block_time: Timestamp,
                           var burn_tx_id: String,
                           var tx_id: String,
                           var coinbase: String,
                           var v_out: Long,
                           var script_sig_asm: String,
                           var script_sig_hex: String,
                           var sequence: Long,
                           var tx_in_witness: Array[String]
                         )

case class ExtractedOutput(
                            var block_date: Date,
                            var block_number: Long,
                            var block_time: Timestamp,
                            var tx_id: String,
                            var n: Long,
                            var value: Double,
                            var script_pub_key_asm: String,
                            var script_pub_key_hex: String,
                            var script_pub_key_req_sigs: Long,
                            var `type`: String,
                            var addresses: Array[String],
                            var address: String
                          )