package co.vgw.lnd.http

import co.vgw.lnd.wallet.domain.QueryResponse
import co.vgw.lnd.wallet.domain.TransactionPayload
import co.vgw.lnd.wallet.domain.WalletManager
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Application.configureRouting() {
    val walletManager = WalletManager()

    routing {
        route("/wallets/{id}") {
            post("/credit") {
                val walletId =
                    call.parameters["id"] ?: return@post call.respond(
                        HttpStatusCode.BadRequest,
                        "No Wallet Id provided"
                    )
                val payload = call.receive<TransactionPayload>()

                when (val result =
                    walletManager.creditWallet(UUID.fromString(walletId), payload.coins, payload.transactionId)) {
                    is QueryResponse.Success -> call.respond(HttpStatusCode.Created, result)
                    is QueryResponse.Error.DuplicateTransaction -> call.respond(
                        HttpStatusCode.Accepted,
                        result
                    )

                    else -> {
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                }
            }
            post("/debit") {
                val walletId =
                    call.parameters["id"] ?: return@post call.respond(
                        HttpStatusCode.BadRequest,
                        "No Wallet Id provided"
                    )
                val payload = call.receive<TransactionPayload>()

                when (val result =
                    walletManager.debitWallet(UUID.fromString(walletId), payload.coins, payload.transactionId)) {
                    is QueryResponse.Success -> call.respond(HttpStatusCode.Created, result)
                    is QueryResponse.Error.DuplicateTransaction -> call.respond(
                        HttpStatusCode.Accepted,
                        result
                    )

                    is QueryResponse.Error.InsufficientFunds -> call.respond(HttpStatusCode.BadRequest, result.msg)
                    else -> {
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                }
            }
            get {
                val walletId =
                    call.parameters["id"] ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        "No Wallet Id provided"
                    )

                when (val result = walletManager.doesWalletExist(UUID.fromString(walletId))) {
                    is QueryResponse.Success -> call.respond(HttpStatusCode.OK, result)
                    is QueryResponse.Error.WalletNotFound -> call.respond(HttpStatusCode.NotFound)
                    else -> {
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                }
            }
        }
    }
}