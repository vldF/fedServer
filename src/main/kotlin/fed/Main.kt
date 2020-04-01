package fed

import fed.dataBase.*
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import java.io.File
import java.util.*
import kotlin.system.exitProcess

/**
 * Main class for server app.
 */
class Main {
    private var port = -1
    private val database = DataBaseFactory()

    init {
        val properties = Properties()
        File("config.properties").reader().use {
            properties.load(it)
        }
        port = properties.getProperty("port", "35309").toInt()
    }

    fun main() {
        val server = embeddedServer(Netty, port = port) {
            install(ContentNegotiation) {
                gson {
                }
            }
            install(StatusPages) {
                status(HttpStatusCode.NotFound) {
                    call.respond(mapOf("error" to true, "description" to "Unknown method"))
                }
            }
            routing {
                get("ping") {
                    call.respond(mapOf("status" to "pong!"))
                }

                get("messages.send") {
                    val state = isResponseCorrect(call.parameters, listOf("receiver", "message", "token"))
                    if (state.isNotEmpty()) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to true, "description" to state))
                        return@get
                    }
                    val receiverId = call.parameters["receiver"]!!.toInt()
                    val messageStr = call.parameters["message"].toString()
                    val token = call.parameters["token"]!!.toString()

                    if (receiverId <= 0) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to true, "description" to "receiver can't be negative"))
                        return@get
                    }

                    if(!isUserTokenExist(token)) {
                        call.respond(HttpStatusCode.Unauthorized, TOKEN_INCORRECT)
                        return@get
                    }

                    database.dbQuery {
                        Messages.insert {
                            it[sender] = Users.select { Users.token eq token }.single().toUser().id
                            it[receiver] = receiverId
                            it[message] = messageStr
                            it[time] = System.currentTimeMillis()
                        }
                    }
                    call.respond(mapOf("status" to "ok"))
                }

                get("messages.get") {
                    val state = isResponseCorrect(call.parameters, listOf("by", "token"))
                    if (state.isNotEmpty()) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to true, "description" to state))
                        return@get
                    }

                    val senderId = call.parameters["by"]!!.toInt()
                    val token = call.parameters["token"]!!.toString()

                    if(!isUserTokenExist(token)) {
                        call.respond(HttpStatusCode.Unauthorized, TOKEN_INCORRECT)
                        return@get
                    }
                    if (senderId <= 0) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to true, "description" to "by can't be negative"))
                        return@get
                    }

                    val msgList = database.dbQuery {
                        (Messages innerJoin Users).select {
                            val userId = Users.select { Users.token eq token }.single()[Users.id]
                            ((Messages.receiver eq userId) and (Messages.sender eq senderId) or
                                    (Messages.receiver eq senderId) and (Messages.sender eq userId))
                        }.map { it.toMessage() }
                    }
                    call.respond(mapOf("data" to msgList))
                }

                get("messages.getLast") {
                    val state = isResponseCorrect(call.parameters, listOf("receiver", "token", "last_time"))
                    if (state.isNotEmpty()) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to true, "description" to state))
                        return@get
                    }
                    val receiver = call.parameters["receiver"]!!.toInt()
                    val lastTime = call.parameters["last_time"]!!.toLong()
                    val token = call.parameters["token"].toString()

                    if(!isUserTokenExist(token)) {
                        call.respond(HttpStatusCode.Unauthorized, TOKEN_INCORRECT)
                        return@get
                    }

                    if (receiver <= 0) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to true, "description" to "receiver can't be negative"))
                        return@get
                    }

                    val msgList = database.dbQuery {
                        val sender = Users.select { Users.token eq token }.single()[Users.id]
                        (Messages innerJoin Users).select {
                            (((Messages.receiver eq receiver) and (Messages.sender eq sender)) or
                                    ((Messages.sender eq receiver) and (Messages.receiver eq sender))) and
                                    (Messages.time.greater(lastTime))
                        }.map { it.toMessage() }
                    }
                    call.respond(mapOf("data" to msgList))
                }

                get("account.register") {
                    val state = isResponseCorrect(call.parameters, listOf("nick"))
                    if (state.isNotEmpty()) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to true, "description" to state))
                        return@get
                    }
                    val nickName = call.parameters["nick"]!!.toString()

                    // is account exist?
                    if (isUserNameExist(nickName)) {
                        call.respond(USER_ALREADY_EXIST)
                        return@get
                    }

                    // getting new unique token
                    var tokenStr: String
                    while (true) {
                        tokenStr = generateToken()
                        val usersIsEmpty = database.dbQuery {
                            Users.select { Users.token eq tokenStr }.empty()
                        }
                        if (usersIsEmpty)
                            break
                    }

                    database.dbQuery {
                        Users.insert {
                            it[nick] = nickName
                            it[token] = tokenStr
                        }
                    }

                    call.respond(mapOf("status" to "ok", "token" to tokenStr))
                }

                get("users.getUserId") {
                    val state = isResponseCorrect(call.parameters, listOf("token", "nick"))
                    if (state.isNotEmpty()) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to true, "description" to state))
                        return@get
                    }
                    val nickName = call.parameters["nick"]!!.toString()
                    val token = call.parameters["token"]!!.toString()

                    if (!isUserTokenExist(token)) {
                        call.respond(TOKEN_INCORRECT)
                        return@get
                    }

                    val user = database.dbQuery {
                        Users.select { (Users.nick eq nickName) }.singleOrNull()?.toUser()
                    }

                    if (user == null) {
                        call.respond(HttpStatusCode.Unauthorized, USER_NOT_FOUND)
                    } else {
                        call.respond(mapOf("id" to user.id))
                    }

                }
            }
        }
        server.start()
        println("server is ready!")
        println("port $port")
        while (true) {
            val inp = readLine()
            if (inp == "stop") {
                println("Shutting down server...")
                exitProcess(0)
            }
        }

    }

    private suspend fun isUserTokenExist(token: String): Boolean {
        return !database.dbQuery {
            Users.select { Users.token eq token }.empty()
        }
    }

    private suspend fun isUserNameExist(userName: String): Boolean {
        return !database.dbQuery {
            Users.select { Users.nick eq userName }.empty()
        }
    }

    private fun isResponseCorrect(params: Parameters, fields: Collection<String>): String {
        for (f in fields) {
            if (params[f] == null || params[f]!!.isEmpty()) return "Parameter $f is incorrect"
        }
        return ""
    }
}

fun main() {
    Main().main()
}
