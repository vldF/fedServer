# fedServer
Simple server for [this client](https://github.com/vldF/fedClient)
1. Simple
2. Full Kotlin!
3. Easy Rest-like API

# API methods
|method   | parameters  | description  |
| ------------ | ------------ | ------------ |
| ping  | *nothing*  | Returns "pong!". Can it be easyer?  |
| message.send  | sender: Int (User's ID), receiver: Int(User's ID), message: String, token: String  | Send message from `sender` to `receiver`  |
| message.get  | userid: Int, by: Int, token: String  | Returns all message betwen user with `userid` and user with ID `by` |
|  messages.getLast | userid: Int, by: Int, token: String, last_time: Long  | Returns all message, that was sent since `last_time` |
| account.register  | nick: String  | Registers new account with `nick`. Returns token of this account. Token couldn't got using other methods (you can't restore that)  |
| account.getOwnInfo  | token: String, nick: String  | Returns User's object (JSON with fields: id: Int, nick: String, token: String)  |
| users.getUserId  |  token: String, nick: String, userid: Int  |  Returns User's ID. `userid` is ID of user, that send this requests |

# Registration
1. Client send account.register with `nick` parameter. 
2. If this nickname exist on server, error will return. Else, `token` will return. Client should to save this `token`.

# Setup
1. Create database. In Ubuntu: `sudo apt-get install postgresql`. After that, you should to create database structure. Use the `database.sql` for this. Do not forget to create custom user!
2. `git clone https://github.com/vldF/fedServer`.
3. Configure the `config.properties`.
4. Start the server. 

You can use `--databaseDebug true` (or `-dd true`, `-d true`) to enable additional debuging.

Task #2 for Peter the Great Polytech, St. Petersburg
