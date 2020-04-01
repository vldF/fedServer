[![Build Status](https://travis-ci.com/vldF/fedServer.svg?branch=master)](https://travis-ci.com/vldF/fedServer)
# fedServer
Simple server for [this client](https://github.com/vldF/fedClient)
1. Simple
2. Full Kotlin!
3. Easy Rest-like API

# API methods
|method   | parameters  | description  |
| ------------ | ------------ | ------------ |
| ping  | *nothing*  | Returns "pong!". Can it be easyer?  |
| message.send  | receiver: Int(User's ID), message: String, token: String  | Send message to `receiver`  |
| message.get  | by: Int, token: String  | Returns all message betwen user and user with ID `by` |
|  messages.getLast | by: Int, token: String, last_time: Long  | Returns all message, that was sent since `last_time` |
| account.register  | nick: String  | Registers new account with `nick`. Returns token of this account. Token couldn't got using other methods (you can't restore that)  |
| users.getUserId  |  token: String, nick: String  |  Returns User's ID |

# Registration
1. Client send `account.register` request with one parameter: `nick`. 
2. If this nickname exist on server, error will return. Else, `token` field will return. Client should to save this `token`.

# Setup
1. Install database. In Ubuntu: `sudo apt-get install postgresql`. After that, you should to create database structure. Use the `database.sql` for this. Do not forget to create custom user and `GRANT ALL` for it!
2. `git clone https://github.com/vldF/fedServer`.
3. Configure the `config.properties`.
4. Start the server. 

Task #2 for Peter the Great Polytechnic University, St. Petersburg
