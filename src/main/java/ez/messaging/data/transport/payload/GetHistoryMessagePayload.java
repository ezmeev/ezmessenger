package ez.messaging.data.transport.payload;

public class GetHistoryMessagePayload {

    private String lastMessageId;

    private int historyDepth;

    public String getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(String lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public int getHistoryDepth() {
        return historyDepth;
    }

    public void setHistoryDepth(int historyDepth) {
        this.historyDepth = historyDepth;
    }
}
