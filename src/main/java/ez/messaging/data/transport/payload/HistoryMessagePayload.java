package ez.messaging.data.transport.payload;

import java.util.List;

import ez.messaging.data.StoredMessage;

public class HistoryMessagePayload {

    private List<StoredMessage> newMessages;

    private List<StoredMessage> history;

    public List<StoredMessage> getNewMessages() {
        return newMessages;
    }

    public void setNewMessages(List<StoredMessage> newMessages) {
        this.newMessages = newMessages;
    }

    public List<StoredMessage> getHistory() {
        return history;
    }

    public void setHistory(List<StoredMessage> history) {
        this.history = history;
    }
}
