package ez.connection.data;

import ez.connection.client.ClientConnection;

public class ConnectionMessage {

    private ClientConnection connection;

    private byte[] data;

    public ConnectionMessage(ClientConnection connection, byte[] data) {
        this.connection = connection;
        this.data = data;
    }

    public ClientConnection getConnection() {
        return connection;
    }

    public byte[] getData() {
        return data;
    }
}
