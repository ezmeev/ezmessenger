package ez.messaging.handlers;

import ez.connection.data.ConnectionMessage;
import ez.messaging.data.transport.Message;

public interface MessageHandler {

    void handleMessage(Message message, ConnectionMessage connectionMessage);
}
