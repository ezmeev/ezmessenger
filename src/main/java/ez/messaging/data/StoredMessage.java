package ez.messaging.data;

public class StoredMessage {

    private long userId;

    private String text;

    private long timestamp;

    private String messgeId;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessgeId() {
        return messgeId;
    }

    public void setMessgeId(String messgeId) {
        this.messgeId = messgeId;
    }
}
