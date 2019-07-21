package ez.connection.data;

public class ConnectionMessage {
    private byte[] data;

    public ConnectionMessage(byte[] data) {

        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
