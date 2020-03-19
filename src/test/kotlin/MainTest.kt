package fed

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import org.junit.*
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

internal class MainTest {
    private val client = OkHttpClient()
    private var serverPort = -1
    private var userId = -1
    private lateinit var userName: String
    private lateinit var userToken: String
    private lateinit var message: String

    private fun doGet(method: String, params: Map<String, Any>): JsonObject {
        val url = "http://localhost:$serverPort/$method?${params.map { "${it.key}=${it.value}" }.joinToString(separator="&")}"
        val req = Request.Builder()
            .url(url)
            .get()
            .build()
        return JsonParser.parseString(client.newCall(req).execute().body().string()).asJsonObject
    }

    init {
        client.setConnectTimeout(10000L, TimeUnit.SECONDS)
        client.setReadTimeout(10000L, TimeUnit.SECONDS)
        client.setWriteTimeout(10000L, TimeUnit.SECONDS)
        val properties = Properties()
        properties.load(File("config.properties").reader())
        serverPort = properties.getProperty("port")?.toInt() ?: throw IllegalStateException()
        main(arrayOf())
    }

    @Test
    fun testAll() {
        isServerAlive()
        register()
        getUsersId()
        val time = System.currentTimeMillis()
        messageSendAndGetLast()
        getOwnInfo()
        getLast(time)
    }

    private fun isServerAlive() {
        val resp = doGet("ping", mapOf())
        Assert.assertNotNull(resp)
        val status = resp["status"]
        Assert.assertEquals("pong!", status.asString)
    }

    private fun register() {
        userName = generateToken()

        val resp = doGet("account.register", mapOf(
            "nick" to userName
        ))
        Assert.assertTrue(resp.has("status") && resp["status"].asString == "ok")
        userToken = resp["token"].asString
        Assert.assertTrue(userToken.isNotEmpty())
    }

    private fun getUsersId() {
        val resp = doGet("users.getUserId", mapOf(
            "nick" to userName,
            "token" to userToken
        ))
        Assert.assertTrue(resp.has("id"))
        userId = resp["id"].asInt
    }

    private fun messageSendAndGetLast() {
        val rnd = generateToken()
        val respSend = doGet("message.send", mapOf(
            "sender" to userId,
            "receiver" to userId,
            "message" to "test$rnd",
            "token" to userToken
        ))

        Assert.assertTrue(respSend.has("status") && respSend["status"].asString == "ok")

        val respGet = doGet("message.get", mapOf(
            "userid" to userId,
            "by" to userId,
            "token" to userToken
        ))

        Assert.assertTrue(respGet.has("data"))
        Assert.assertEquals("test$rnd", respGet["data"].asJsonArray.last().asJsonObject["message"].asString)

        message = "test$rnd"
    }

    private fun getOwnInfo() {
        val resp = doGet("account.getOwnInfo", mapOf(
            "token" to userToken,
            "nick" to userName
        ))

        Assert.assertEquals(userName, resp["nick"].asString)
        Assert.assertEquals(userId, resp["id"].asInt)
        Assert.assertEquals(userToken, resp["token"].asString)
    }

    private fun getLast(lastTime: Long) {
        val resp = doGet("messages.getLast", mapOf(
                "userid" to userId,
                "by" to userId,
                "token" to userToken,
                "last_time" to lastTime
            ))
        Assert.assertTrue(resp.has("data"))
        Assert.assertTrue(resp["data"].asJsonArray.last().asJsonObject["message"].asString == message)
    }
}