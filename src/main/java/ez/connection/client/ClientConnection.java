package ez.connection.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

import ez.connection.data.ConnectionMessage;
import ez.util.Logger;

public class ClientConnection {

    private Socket connection;

    private ConnectionMessage lastMessage; // volotile ???

    private BufferedInputStream reader;

    private BufferedOutputStream writer;

    private boolean queued = false;

    public ClientConnection(Socket connection) throws IOException {
        this.connection = connection;
        this.reader = new BufferedInputStream(connection.getInputStream());
        this.writer = new BufferedOutputStream(connection.getOutputStream());
    }

    public boolean isAlive() {
        return !connection.isClosed();
    }

    public boolean isQueued() {
        return queued;
    }

    // read

    public boolean newDataAvailable() {
        try {
            int available = reader.available();
            return available > 0;
        } catch (IOException e) {
            e.printStackTrace();
            // TODO
        }
        return false;
    }

    public boolean hasMessage() {
        return lastMessage != null;
    }

    public ConnectionMessage getMessage() {
        ConnectionMessage message = lastMessage;
        lastMessage = null;
        Logger.log("[CLIENT_CONNECTION]: Returning message");
        return message;
    }

    public void fetchMessage() {
        try {
            int available = reader.available();

            if (available == 0) {
                return;
            }

            Logger.log("[CLIENT_CONNECTION]: Reading " + available + " bytes");
            var message = reader.readNBytes(available);
            Logger.log("[CLIENT_CONNECTION]: Done reading");
            lastMessage = new ConnectionMessage(message);
            // This "ack" response serves as a marker for client, that server now ready for next incoming message,
            // if it's allowed by protocol. As side effect it's also provides kind of back-pressure - client not
            // supposed to send any new data until it received "ack" from server.
            writer.write("ack".getBytes());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO
        }
    }

    // write

    public void sendMessage(ConnectionMessage message) {
        try {
            writer.write(message.getData());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO
        }
    }

    public void markQueued() {
        queued = true;
    }

    public void markUnqueued() {
        queued = false;
    }
}
