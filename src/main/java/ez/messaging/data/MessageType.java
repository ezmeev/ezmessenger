package ez.messaging.data;

public enum MessageType {

    HelloMessage,

    GetHistoryMessage,
    HistoryMessage,

    StartTyping,
    StopTyping,

    TextMessage,
    AckByServer,
    Received,
    AckByReceiver,
    Read,

//    InitMessage,
//    TextMessage,
//    MessageAcknowledgedByServer,
//    MessageAcknowledgedByClient,
}
