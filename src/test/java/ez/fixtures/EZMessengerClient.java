package ez.fixtures;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import ez.messaging.data.transport.Message;
import ez.util.JsonConvert;
import ez.util.Logger;

public class EZMessengerClient implements AutoCloseable {

    private ObjectMapper objectMapper = new ObjectMapper();

    private BufferedInputStream reader = null;

    private BufferedOutputStream writer = null;

    private Socket socket = null;

    private int port;

    private String host;

    private String identity;

    public EZMessengerClient(String clientId, String host, int port) {
        this.port = port;
        this.host = host;
        this.identity = clientId;
    }

    private void connect() throws IOException {
        socket = new Socket(host, port);
        writer = new BufferedOutputStream(socket.getOutputStream());
        reader = new BufferedInputStream(socket.getInputStream());
    }

    public boolean sendMessage(Message message) throws IOException {
        if (socket == null) {
            connect();
        }

        String payload = objectMapper.writeValueAsString(message);

        Logger.log("[CLIENT-" + identity + "] ---> " + payload);

        writer.write(payload.getBytes());
        writer.flush();

        waitReaderDataAvailable(3);

        byte[] data = reader.readNBytes(3);
        String ack = new String(data);

        Logger.log("[CLIENT-" + identity + "] <--- " + ack);

        return ack.equals("ack");
    }

    public Message getMessage() throws IOException {
        if (socket == null) {
            connect();
        }
        if (reader.available() > 0) {
            byte[] data = reader.readNBytes(reader.available());
            String messageData = new String(data);
            Logger.log("[CLIENT-" + identity + "] <---" + messageData);
            return JsonConvert.deserialize(messageData, Message.class);
        }
        return null;
    }

    public Message waitMessage() throws IOException {
        return waitMessage(5);
    }

    public Message waitMessage(int timeoutSeconds) throws IOException {
        if (socket == null) {
            connect();
        }
        waitReaderDataAvailable(timeoutSeconds);
        return getMessage();
    }

    @Override
    public void close() throws Exception {
        if (socket != null) {
            socket.close();
        }
    }

    private void waitReaderDataAvailable(int timeoutSeconds) throws IOException {
        long startedAt = System.currentTimeMillis();
        while (reader.available() == 0) {
            long current = System.currentTimeMillis();

            if (current - startedAt > 1000 * timeoutSeconds) {
                throw new RuntimeException("WaitMessage timed out");
            }

            Thread.yield();
        }
    }

    public void readMessage(Message message) throws IOException {
        sendMessage(Message.createReadMessage(message.getSenderId(), message.getReceiverId(),
            message.getMessageId()));
    }

    public void ackMessage(Message receiverMsg) throws IOException {
        sendMessage(Message.createAckByReceiverMessage(receiverMsg.getReceiverId(), receiverMsg.getSenderId(),
            receiverMsg.getMessageId()));
    }
}
