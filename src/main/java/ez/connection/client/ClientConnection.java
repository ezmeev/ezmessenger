package ez.connection.client;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import ez.connection.data.ConnectionMessage;

public interface ClientConnection {

    SocketChannel getChannel();

    boolean isAlive();

    // TODO seems like this interface is messed up a bit, read/sent together with implementation details :/

    ConnectionMessage readMessage() throws IOException;

    void sendMessage(ConnectionMessage message);

}
