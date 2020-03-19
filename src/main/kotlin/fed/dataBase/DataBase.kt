package fed.dataBase

import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toUser() = User(
    this[Users.id],
    this[Users.nick],
    this[Users.token]
)

fun ResultRow.toMessage() = Message(
    this[Messages.sender],
    this[Messages.senderNick],
    this[Messages.receiver],
    this[Messages.message],
    this[Messages.time]
)