package ez.connection.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseConnection implements ClientConnection {

    protected String readDataAsString(SocketChannel channel) throws IOException {
        byte[] data = readData(channel);
        return new String(data);
    }

    protected byte[] readData(SocketChannel channel) throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(4096);

        List<byte[]> chunks = new ArrayList<>();

        int totalReceived = 0;

        while (buffer.hasRemaining()) {
            buffer.clear();
            int read = channel.read(buffer);
            buffer.flip();

            if (read > 0) {
                var chunk = new byte[read];
                buffer.get(chunk);
                chunks.add(chunk);

                totalReceived += read;
            }
        }

        var data = new byte[totalReceived];

        var i = 0;
        for (byte[] chunk : chunks) {
            System.arraycopy(chunk, 0, data, i, chunk.length);
            i += chunk.length;
        }

        return data;
    }

    protected void sendData(SocketChannel channel, String dataAsString) throws IOException {
        channel.write(ByteBuffer.wrap(dataAsString.getBytes(StandardCharsets.UTF_8)));
    }

    protected void sendData(SocketChannel channel, byte[] data) throws IOException {
        channel.write(ByteBuffer.wrap(data));
    }
}
