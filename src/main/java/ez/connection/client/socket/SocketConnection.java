package ez.connection.client.socket;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

import ez.connection.client.BaseConnection;
import ez.connection.client.ClientConnection;
import ez.connection.data.ConnectionMessage;

public class SocketConnection extends BaseConnection implements ClientConnection {

    private Socket connection;

    private SocketChannel channel;

    public SocketConnection(SocketChannel channel) {
        this.channel = channel;
        this.connection = channel.socket();
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public boolean isAlive() {
        return !connection.isClosed();
    }

    public ConnectionMessage readMessage() throws IOException {
        var data = readData(channel);

        // This "ack" response serves as a marker for client, that server now ready for next incoming message,
        // if it's allowed by protocol. As side effect it's also provides kind of back-pressure - client not
        // supposed to send any new data until it received "ack" from server.
        channel.write(ByteBuffer.wrap("ack".getBytes(StandardCharsets.UTF_8)));

        return new ConnectionMessage(this, data);
    }

    public void sendMessage(ConnectionMessage message) {
        try {
            sendData(channel, message.getData());
        } catch (IOException e) {
            e.printStackTrace();
            // TODO
        }
    }
}
