package ez.messaging.data.transport;

public enum MessageType {

    HelloMessage,
    ByeMessage,

    GetHistoryMessage,
    HistoryMessage,

    StartTyping,
    StopTyping,

    TextMessage,
    AckByServerMessage,
    Received,
    AckByReceiverMessage,
    Read,

//    InitMessage,
//    TextMessage,
//    MessageAcknowledgedByServer,
//    MessageAcknowledgedByClient,
}
