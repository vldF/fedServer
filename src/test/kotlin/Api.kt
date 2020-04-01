package fed

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import java.util.concurrent.TimeUnit

/**
 * Api for server
 * @param nickname: username on server.
 * @param serverAddress: server address. If it doesn't contains port, default will be added (35309)
 */
class Api(private val nickname: String, private val serverAddress: String) {
    private val baseUrl = "http://${if (serverAddress.contains(':')) serverAddress else "$serverAddress:35309"}/"
    private val client = OkHttpClient()
    private val token: String

    init {
        client.setConnectTimeout(10L, TimeUnit.SECONDS)
        token = register()["token"].asString
    }

    /**
     * Base function for server's REST API.
     * @param method: servers' API method name.
     * @param params: map like {"key" to value}. Key should be String, value can be String, Int, Boolean or Double.
     */
    private fun execute(method: String, params: Map<String, Any>): JsonObject {
        val paramsString = params.map { "${it.key}=${it.value}" }.joinToString("&")

        val req = Request.Builder()
            .url("$baseUrl$method?$paramsString&token=$token")
            .build()
        val resp = client.newCall(req).execute().body().string()
        try {
            return JsonParser.parseString(resp).asJsonObject
        } catch (e: Exception) {
            // message for debugging. It will shown only if program run in emulation mode
            System.err.println(resp)
            System.err.println("$baseUrl$method?$paramsString&token=$token")
            throw e
        }
    }

    /**
     * Get all message between user with fromId and current user, that was sent since time.
     * @param fromId: second user ID.
     * @param time: UNIX time. Set this param to 0, if you would to get all messages.
     */
    fun getLastMessages(fromId: Int, time: Long): JsonObject = execute("messages.getLast", mapOf(
        "receiver" to fromId,
        "last_time" to time
    ))

    /**
     * Sent new message.
     * @param toId: ID of user, that receive this message.
     * @param msg: message.
     */
    fun messageSend(toId: Int, msg: String): JsonObject = execute("messages.send", mapOf(
            "receiver" to toId,
            "message" to msg,
            "token" to token
        ))

    /**
     * Register new account.
     * If this account exist on the server, error will return. Else new secret token will return.
     */
    private fun register(): JsonObject = execute("account.register", mapOf(
            "nick" to nickname
        ))

    /**
     * Get user's ID.
     * @param nick: user's nick, whose ID you would to get.
     */
    fun getUserId(nick: String): JsonObject = execute("users.getUserId", mapOf(
        "nick" to nick
    ))
}