package fed

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import java.util.concurrent.TimeUnit

class Request(private val serverPort: Int = 35309) {
    private val client = OkHttpClient()
    init {
        client.setConnectTimeout(10000L, TimeUnit.SECONDS)
        client.setReadTimeout(10000L, TimeUnit.SECONDS)
        client.setWriteTimeout(10000L, TimeUnit.SECONDS)
    }
    fun doGet(method: String, params: Map<String, Any>): JsonObject {
        val url = "http://localhost:$serverPort/$method?${params.map { "${it.key}=${it.value}" }
            .joinToString(separator = "&")}"
        val req = Request.Builder()
            .url(url)
            .get()
            .build()
        return JsonParser.parseString(client.newCall(req).execute().body().string()).asJsonObject
    }
}