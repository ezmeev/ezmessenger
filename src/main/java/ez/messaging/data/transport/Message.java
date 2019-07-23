package ez.messaging.data.transport;

import java.nio.charset.StandardCharsets;

import ez.messaging.data.transport.payload.GetHistoryMessagePayload;
import ez.messaging.data.transport.payload.TextMessagePayload;
import ez.messaging.helpers.MessagePayloadHelper;

public class Message {

    private MessageType type;

    private byte[] data;

    private String senderId;

    private String receiverId;

    private String messageId;

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
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

    public static Message createGetHistoryMessage(String senderId, String lastMessageId) {
        var payload = new GetHistoryMessagePayload();
        payload.setHistoryDepth(10);
        payload.setLastMessageId(lastMessageId);

        Message message = new Message();
        message.setType(MessageType.GetHistoryMessage);
        message.setSenderId(senderId);
        message.setData(MessagePayloadHelper.toBytes(payload));
        return message;
    }

    public static Message createHistoryMessage(String senderId, String history) {
        Message message = new Message();
        message.setType(MessageType.HistoryMessage);
        message.setSenderId(senderId);
        message.setData(history.getBytes(StandardCharsets.UTF_8));
        return message;
    }

    public static Message createTextMessage(String senderId, String receiverId, String text) {
        var payload = new TextMessagePayload();
        payload.setText(text);

        Message message = new Message();
        message.setType(MessageType.TextMessage);
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setData(MessagePayloadHelper.toBytes(payload));
        return message;
    }
}
