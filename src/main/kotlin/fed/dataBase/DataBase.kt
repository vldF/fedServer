package fed.dataBase

import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toUser() = User(
    this[Users.id],
    this[Users.nick],
    this[Users.token]
)

fun ResultRow.toMessage() = Message(
    this[Messages.sender],
    this[Messages.receiver],
    this[Users.nick],
    this[Messages.message],
    this[Messages.time]
)