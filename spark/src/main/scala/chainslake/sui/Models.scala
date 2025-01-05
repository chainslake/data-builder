package chainslake.sui

import java.sql.{Date, Timestamp}

case class ResponseRawString(
                              var jsonrpc: String,
                              var result: String
                            )

case class ResponseRawBlock(
                             var jsonrpc: String,
                             var result: RawBlock
                           )
case class RawBlock(var timestampMs: String,
                    var transactions: Array[String])

case class ResponseRawTransactions(
                                    var jsonrpc: String,
                                    var result: Array[Any]
                                  )

case class OriginBlock(
                        var block_date: Date,
                        var block_number: Long,
                        var block_time: Timestamp,
                        var block: String,
                        var transactions: String
                      )

case class ResponseBlock(var jsonrpc: String,
                         var result: Block)

case class Block(
                var epoch: String,
                var sequenceNumber: String,
                var digest: String,
                var networkTotalTransactions: String,
                var previousDigest: String,
                var epochRollingGasCostSummary: EpochRollingGasCostSummary,
                var timestampMs: String,
                var transactions: Array[String],
                var validatorSignature: String
                )

case class EpochRollingGasCostSummary(
                                     var computationCost: String,
                                     var storageCost: String,
                                     var storageRebate: String,
                                     var nonRefundableStorageFee: String
                                     )

case class Transaction(
                      var digest: String,
                      var transaction: TransactionDetail,
                      var effects: Effects,
                      var events: Array[Event],
                      var objectChanges: Array[ObjectChange],
                      var balanceChanges: Array[BalanceChange],
                      var timestampMs: String,
                      var checkpoint: String
                      )

case class Effects(
                  var messageVersion: String,
                  var status: Any,
                  var executedEpoch: String,
                  var gasUsed: GasUsed,
                  var modifiedAtVersions: Array[Any],
                  var sharedObjects: Array[Any],
                  var transactionDigest: String,
                  var created: Array[Any],
                  var mutated: Array[Any],
                  var deleted: Array[Any],
                  var gasObject: Any,
                  var eventsDigest: String,
                  var dependencies: Array[String]
                  )

case class GasUsed(
                  var computationCost: String,
                  var storageCost: String,
                  var storageRebate: String,
                  var nonRefundableStorageFee: String
                  )

case class TransactionDetail(
                            var data: TransactionData,
                            var txSignatures: Array[String]
                            )

case class TransactionData(
                          var messageVersion: String,
                          var transaction: TransactionExecute,
                          var sender: String,
                          var gasData: GasData
                          )

case class GasData(var payment: Array[Any],
                   var owner: String,
                   var price: String,
                   var budget: String
                  )

case class TransactionExecute(
                             var kind: String,
                             var inputs: Array[Any],
                             var transactions: Array[Any]
                             )

case class Event(
                var id: Id,
                var packageId: String,
                var transactionModule: String,
                var sender: String,
                var `type`: String,
                var parsedJson: Any,
                var bcsEncoding: String,
                var bcs: String
                )

case class Id(
             var txDigest: String,
             var eventSeq: String
             )

case class ObjectChange(
                       var `type`: String,
                       var sender: String,
                       var owner: Any,
                       var objectType: String,
                       var objectId: String,
                       var version: String,
                       var previousVersion: String,
                       var digest: String
                       )

case class Owner(
                var AddressOwner: String
                )

case class BalanceChange(
                        var owner: Owner,
                        var coinType: String,
                        var amount: String
                        )

case class ExtractedBlock(
                           var block_date: Date,
                           var block_number: Long,
                           var block_time: Timestamp,
                           var digest: String,
                           var epoch: Long,
                           var network_total_transactions: Long,
                           var previous_digest: String,
                           var computation_cost: Long,
                           var storage_cost: Long,
                           var storage_rebate: Long,
                           var non_refundable_storage_fee: Long,
                           var signature: String,
                           var total_transactions: Long
                         )

case class ExtractedTransaction(
                                 var block_date: Date,
                                 var block_number: Long,
                                 var block_time: Timestamp,
                                 var digest: String,
                                 var transaction_kind: String,
                                 var tx_signatures: Array[String],
                                 var message_version: String,
                                 var sender: String,
                                 var gas_owner: String,
                                 var gas_price: Long,
                                 var gas_budget: Long,
                                 var gas_payment: Array[String],
                                 var inputs: Array[String],
                                 var internal_calls: Array[String],
                                 var status: String,
                                 var executed_epoch: Long,
                                 var computation_cost: Long,
                                 var storage_cost: Long,
                                 var storage_rebate: Long,
                                 var non_refundable_storage_fee: Long,
                                 var modified_at_versions: Array[String],
                                 var shared_objects: Array[String],
                                 var created: Array[String],
                                 var mutated: Array[String],
                                 var deleted: Array[String],
                                 var event_digest: String,
                                 var dependencies: Array[String]
                               )

case class ExtractedEvent(
                           var block_date: Date,
                           var block_number: Long,
                           var block_time: Timestamp,
                           var tx_digest: String,
                           var event_Seq: Long,
                           var package_id: String,
                           var transaction_module: String,
                           var sender: String,
                           var `type`: String,
                           var parsed_json: String,
                           var bcs_encoding: String,
                           var bcs: String
                         )

case class ExtractedObjectChange(
                                  var block_date: Date,
                                  var block_number: Long,
                                  var block_time: Timestamp,
                                  var tx_digest: String,
                                  var digest: String,
                                  var `type`: String,
                                  var sender: String,
                                  var owner_address: String,
                                  var object_type: String,
                                  var object_id: String,
                                  var version: Long,
                                  var previous_version: Long
                                )

case class ExtractedBalanceChange(
                                   var block_date: Date,
                                   var block_number: Long,
                                   var block_time: Timestamp,
                                   var tx_digest: String,
                                   var owner_address: String,
                                   var coin_type: String,
                                   var amount: Double
                                 )