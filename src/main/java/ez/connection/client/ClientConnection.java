package ez.connection.client;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import ez.connection.data.ConnectionMessage;

public class ClientConnection {

    private Socket connection;

    private SocketChannel channel;

    public ClientConnection(SocketChannel channel) {
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

        ByteBuffer buffer = ByteBuffer.allocate(4096);

        List<byte[]> chunks = new ArrayList<>();

        int totalReceived = 0;

        while (buffer.hasRemaining()) {
            buffer.clear();
            int read = channel.read(buffer);
            buffer.flip();

            var chunk = new byte[read];
            buffer.get(chunk);
            chunks.add(chunk);

            totalReceived += read;

        }

        var data = new byte[totalReceived];

        var i = 0;
        for (byte[] chunk : chunks) {
            System.arraycopy(chunk, 0, data, i, chunk.length);
            i += chunk.length;
        }

        // This "ack" response serves as a marker for client, that server now ready for next incoming message,
        // if it's allowed by protocol. As side effect it's also provides kind of back-pressure - client not
        // supposed to send any new data until it received "ack" from server.
        channel.write(ByteBuffer.wrap("ack".getBytes(StandardCharsets.UTF_8)));

        return new ConnectionMessage(this, data);
    }

    public void sendMessage(ConnectionMessage message) {
        try {

            channel.write(ByteBuffer.wrap(message.getData()));

        } catch (IOException e) {
            e.printStackTrace();
            // TODO
        }
    }
}
