package ez.connection.data;

import java.nio.charset.StandardCharsets;

import ez.connection.client.ClientConnection;

public class ConnectionMessage {

    private ClientConnection connection;

    private byte[] data;

    public ConnectionMessage(ClientConnection connection, byte[] data) {
        this.connection = connection;
        this.data = data;
    }

    public ConnectionMessage(ClientConnection connection, String data) {
        this.connection = connection;
        this.data = data.getBytes(StandardCharsets.UTF_8);
    }

    public ClientConnection getConnection() {
        return connection;
    }

    public byte[] getData() {
        return data;
    }

    public String getDataAsString() {
        return new String(data);
    }
}
