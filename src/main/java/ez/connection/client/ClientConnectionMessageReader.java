package ez.connection.client;

import ez.connection.data.ConnectionMessage;

public class ClientConnectionMessageReader {

    public ConnectionMessage readMessage(ClientConnection connection) {
        if (connection.hasMessage()) {
            return connection.getMessage();
        } else {
            // TODO should be done in separate thread
            connection.fetchMessage();
        }

        return null;
    }

    public ConnectionMessage waitMessage(ClientConnection connection) {
        while (!connection.newDataAvailable()) {
        }
        connection.fetchMessage();
        return connection.getMessage();
    }
}
