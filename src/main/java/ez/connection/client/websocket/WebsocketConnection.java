package ez.connection.client.websocket;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ez.connection.client.BaseConnection;
import ez.connection.client.ClientConnection;
import ez.connection.data.ConnectionMessage;
import ez.messaging.data.transport.Message;
import ez.messaging.helpers.MessagePayloadHelper;

public class WebsocketConnection extends BaseConnection implements ClientConnection {

    private static final Pattern webSocketKeyRegExp = Pattern.compile("Sec-WebSocket-Key: (.*)");

    private Socket connection;

    private SocketChannel channel;

    public WebsocketConnection(SocketChannel channel) {
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

        var dataFrame = new IncomingWebsocketDataFrame(data);

        switch (dataFrame.opCode) {
            case CLOSE:
                return new ConnectionMessage(this, MessagePayloadHelper.toBytes(Message.createByeMessage(null)));
            case PING:
            case PONG:
            case CONTINUE:
            case BINARY:
                throw new RuntimeException("Unsupported opCode: " + dataFrame.opCode);
                // TODO introduce ErrorMessage type
        }

        // This "ack" response serves as a marker for client, that server now ready for next incoming message,
        // if it's allowed by protocol. As side effect it's also provides kind of back-pressure - client not
        // supposed to send any new data until it received "ack" from server.
        sendData(channel, new OutgoingWebsocketDataFrame("ack").frameData);

        return new ConnectionMessage(this, dataFrame.payload);
    }

    public void sendMessage(ConnectionMessage message) {
        try {
            sendData(channel, new OutgoingWebsocketDataFrame(message.getData()).frameData);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO
        }
    }

    public void doHandshake() {
        try {
            var message = readDataAsString(channel);
            var webSocketKey = extractWebSocketKey(message);
            var serversHandshake = prepareServerHandshake(webSocketKey);
            sendData(channel, serversHandshake);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO
        }
    }

    private String prepareServerHandshake(String webSocketKey) throws NoSuchAlgorithmException {
        String serverKey = webSocketKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] hashedKey = sha1.digest(serverKey.getBytes(StandardCharsets.UTF_8));
        var base64OfHashedKey = Base64.getEncoder().encodeToString(hashedKey);
        return "HTTP/1.1 101 Switching Protocols\r\n"
            + "Upgrade: websocket\r\n"
            + "Connection: Upgrade\r\n"
            + "Sec-WebSocket-Accept: " + base64OfHashedKey + "\r\n\r\n";
    }

    private String extractWebSocketKey(String clientHandshake) {
        Matcher match = webSocketKeyRegExp.matcher(clientHandshake);
        if (match.find()) {
            return match.group(1);
        } else {
            throw new RuntimeException("Handshake failed: unable to extract 'Sec-WebSocket-Key' value");
        }
    }
}
