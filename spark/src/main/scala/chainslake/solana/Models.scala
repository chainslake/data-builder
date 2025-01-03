package chainslake.solana

import java.sql.{Date, Timestamp}

case class ResponseRawNumber(
                              var jsonrpc: String,
                              var result: Long
                            )

case class ResponseRawString(
                              var jsonrpc: String,
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
                             var error: RPCError,
                             var result: RawBlock
                           )
case class RPCError(
                   var code: Long
                   )
case class RawBlock(
                     var blockTime: Long
                   )

case class ResponseBlock(var jsonrpc: String,
                         var result: TransactionBlock)

case class TransactionBlock(
                             var blockHeight: Long,
                             var blockTime: Long,
                             var blockhash: String,
                             var parentSlot: Long,
                             var previousBlockhash: String,
                             var rewards: Array[Reward],
                             var transactions: Array[Transaction],
                             var signatures: Array[String]
                           )

case class Reward(
                  var commission: Long,
                  var lamports: Long,
                  var postBalance: Long,
                  var pubkey: String,
                  var rewardType: String
                  )

case class Transaction(
                      var meta: Meta,
                      var transaction: DetailTransaction,
                      var version: String
                      )

case class DetailTransaction(
                            var message: Message,
                            var signatures: Array[String]
                            )

case class Message(var accountKeys: Array[AccountKey],
                   var header: Header,
                  var instructions: Array[Instruction],
                   var recentBlockhash: String,
                   var addressTableLookups: Array[AddressTableLookup]
                  )

case class AccountKey(
                     var pubkey: String,
                     var signer: Boolean,
                     var source: String,
                     var writable: Boolean
                     )

case class AddressTableLookup(var accountKey: String,
                              var writableIndexes: Array[Long],
                              var readOnlyIndexs: Array[Long])

case class Header(var numRequiredSignatures: Long,
                  var numReadonlySignedAccounts: Long,
                  var numReadonlyUnsignedAccounts: Long)


case class Instruction(
                     var programIdIndex: Long,
                     var programId: String,
                     var program: String,
                     var parsed: Any,
                     var accounts: Array[String],
                     var data: String
                      )


case class Meta(
                 var computeUnitsConsumed: Long,
                 var err: Any,
                 var fee: Long,
                 var innerInstructions: Array[InnerInstruction],
                 var logMessages: Array[String],
                 var postBalances: Array[Long],
                 var preBalances: Array[Long],
                 var postTokenBalances: Array[TokenBalance],
                 var preTokenBalances: Array[TokenBalance],
                 var rewards: Array[Reward],
                 var loadedAddresses: LoadedAddress,
                 var returnData: ReturnData,
               )

case class ReturnData(var programId: String,
                      var data: Any)

case class LoadedAddress(var writable: Array[String],
                         var readonly: Array[String])


case class InnerInstruction(
                           var index: Long,
                           var instruction: Array[Instruction]
                           )

case class TokenBalance(
                       var accountIndex: Long,
                       var mint: String,
                       var owner: String,
                       var programId: String,
                       var uiTokenAmount: UITokenAmount
                       )

case class UITokenAmount(
                        var amount: String,
                        var decimals: Long,
                        var uiAmount: Double,
                        var uiAmountString: String
                        )

case class ExtractedBlock(
                           var block_date: Date,
                           var block_number: Long,
                           var block_time: Timestamp,
                           var block_hash: String,
                           var block_height: Long,
                           var parent_slot: Long,
                           var previous_block_hash: String
                         )

case class ExtractedReward(
                            var block_date: Date,
                            var block_number: Long,
                            var block_time: Timestamp,
                            var commission: Long,
                            var lamports: Long,
                            var post_balance: Long,
                            var pubkey: String,
                            var reward_type: String
                          )

case class ExtractedTransaction(
                                 var block_date: Date,
                                 var block_number: Long,
                                 var block_time: Timestamp,
                                 var signature: String,
                                 var signers: Array[String],
                                 var version: String,
                                 var computer_units_consumed: Long,
                                 var err: String,
                                 var fee: Long,
                                 var log_messages: Array[String],
                                 var recent_block_hash: String
                               )

case class ExtractedNativeBalanceChange(
                                         var block_date: Date,
                                         var block_number: Long,
                                         var block_time: Timestamp,
                                         var signature: String,
                                         var pubkey: String,
                                         var pre_balance: Long,
                                         var post_balance: Long
                                       )

case class ExtractedTokenBalanceChange(
                                        var block_date: Date,
                                        var block_number: Long,
                                        var block_time: Timestamp,
                                        var signature: String,
                                        var pubkey: String,
                                        var mint: String,
                                        var owner: String,
                                        var program_id: String,
                                        var decimals: Long,
                                        var pre_amount: String,
                                        var pre_str_ui_amount: String,
                                        var pre_ui_amount: Double,
                                        var post_amount: String,
                                        var post_str_ui_amount: String,
                                        var post_ui_amount: Double
                                      )

case class ExtractedInstruction(
                                 var block_date: Date,
                                 var block_number: Long,
                                 var block_time: Timestamp,
                                 var signature: String,
                                 var program_id_index: Long,
                                 var program_id: String,
                                 var program: String,
                                 var parsed: String,
                                 var accounts: Array[String],
                                 var data: String
                               )
