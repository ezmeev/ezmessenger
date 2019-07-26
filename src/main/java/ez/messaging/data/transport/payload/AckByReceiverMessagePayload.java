package ez.messaging.data.transport.payload;

public class AckByReceiverMessagePayload {
    private String messageId;

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageId() {
        return messageId;
    }
}
