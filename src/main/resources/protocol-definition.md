# EZ Messenger protocol
Bla-bla-bla intent bla-bla-bla reliable bla-bla-bla scalable bla-bla-bla will be implemented.

## client (re-)initiating connection
1. [client] -> [Hello]      -> [server]
2. [client] <- ["ack"]      <- [server]
3. [client] -> [GetHistory] -> [server]
4. [client] <- ["ack"]      <- [server]
5. [client] <- [History]    <- [server]

##### [Hello] fields:
- "senderId" - client identity, received earlier and known to server
- "type" - Hello
###### example:
```
{
  "type": "Hello",
  "data": null,
  "senderId": "1_1",
  "receiverId": null,
  "messageId": null
}
```

##### [GetHistory] fields:
- "lastMessageId" - ID of the last received message
- "historyDepth" - number of messages prior to "lastMessageId"
- "receiverId" - client identity
- "type" - GetHistoryMessage
###### example:
```
{
  "type": "GetHistoryMessage",
  "data": null,
  "senderId": "1_1",
  "receiverId": null,
  "messageId": null
}
```

##### [History] fields:
- "receiverId" - client identity
- "newMessages" - array of messages, which was not yet delivered to client
- "history" - array of messages, should contain up to "historyDepth" messages prior to "lastMessageId"
- "type" - HistoryMessage
###### example:
```
{
  "type": "HistoryMessage",
  "data": null,
  "senderId": "1_1",
  "receiverId": null,
  "messageId": null
}
```

## client typing / not typing
0. [client1] -> [StartTyping] -> [server]
1. [client1] <- ["ack"]       <- [server]
2. [client2] <- [StartTyping] <- [server]
3. [client1] -> [StopTyping]  -> [server]
4. [client1] <- ["ack"]       <- [server]
5. [client2] <- [StopTyping]  <- [server]

##### [StartTyping] fields:
- "type" - StartTyping
###### example:
```
{
  "type": "StartTyping",
  "data": null,
  "senderId": "1_1",
  "receiverId": null,
  "messageId": null
}
```

##### [StopTyping] fields:
- "type" - StopTyping
###### example:
```
{
  "type": "StopTyping",
  "data": null,
  "senderId": "1_1",
  "receiverId": null,
  "messageId": null
}
```

## client sending text message
1.  [client1] ->  [TextMessage]   -> [server]
2.  [client1] <-  ["ack"]         <- [server]
3.  [client1] <-  [AckByServer]   <- [server]
4.  [client2] <-  [TextMessage]   <- [server]
5.  [client2] ->  [Received]      -> [server]
6.  [client2] <-  ["ack"]         <- [server]
7.  [client1] <-  [AckByReceiver] <- [server]
8.  [client2] ->  [Read]          -> [server]
9.  [client2] <-  ["ack"]         <- [server]
10. [client1] <-  [Read]          <- [server]

##### [TextMessage] fields:
- "type" - TextMessage
###### example:
```
{
  "type": "TextMessage",
  "data": null,
  "senderId": "1_1",
  "receiverId": null,
  "messageId": null
}
```

##### [AckByServer] fields:
- "type" - AckByServer
###### example:
```
{
  "type": "AckByServer",
  "data": null,
  "senderId": "1_1",
  "receiverId": null,
  "messageId": null
}
```

##### [Received] fields:
- "type" - Received
###### example:
```
{
  "type": "Received",
  "data": null,
  "senderId": "1_1",
  "receiverId": null,
  "messageId": null
}
```

##### [AckByReceiver] fields:
- "type" - AckByReceiver
###### example:
```
{
  "type": "AckByReceiver",
  "data": null,
  "senderId": "1_1",
  "receiverId": null,
  "messageId": null
}
```

##### [Read] fields:
- "type" - Read
###### example:
```
{
  "type": "Read",
  "data": null,
  "senderId": "1_1",
  "receiverId": null,
  "messageId": null
}
```
