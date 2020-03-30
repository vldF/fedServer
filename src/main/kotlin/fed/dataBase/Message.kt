package fed.dataBase

data class Message(val sender: Int, val receiver: Int, val receiverNick: String, val message: String, val time: Long)