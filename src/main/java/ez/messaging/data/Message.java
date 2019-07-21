package ez.messaging.data;

public class Message {

    private MessageType type;

    private String data;

    private String senderId;

    private String receiverId;

    private String messageId;

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessageId() {
        return messageId;
    }

    public static Message createHelloMessage(String senderId) {
        Message message = new Message();
        message.setType(MessageType.HelloMessage);
        message.setSenderId(senderId);
        return message;
    }

    public static Message createGetHistoryMessage(String senderId) {
        Message message = new Message();
        message.setType(MessageType.GetHistoryMessage);
        message.setSenderId(senderId);
        return message;
    }

    public static Message createHistoryMessage(String senderId, String history) {
        Message message = new Message();
        message.setType(MessageType.HistoryMessage);
        message.setSenderId(senderId);
        message.setData(history);
        return message;
    }

    public static Message createTextMessage(String senderId, String receiverId, String payload) {
        Message message = new Message();
        message.setType(MessageType.TextMessage);
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setData(payload);
        return message;
    }
}
