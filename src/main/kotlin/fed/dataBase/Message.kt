package fed.dataBase

import com.google.gson.*
import java.lang.reflect.Type

data class Message(val sender: Int, val senderNick: String, val receiver: Int, val message: String, val time: Long) : JsonSerializer<Message> {
    override fun serialize(src: Message?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        val res = JsonObject()
        res.add("by", JsonPrimitive(sender))
        res.add("senderNick", JsonPrimitive(senderNick))
        res.add("msg", JsonPrimitive(message))
        res.add("receiver", JsonPrimitive(receiver))
        res.add("time", JsonPrimitive(time))

        return res
    }

}