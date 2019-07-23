package ez.messaging.handlers;

import ez.messaging.data.transport.Message;

public interface MessageHandler {

    void handleMessage(Message message);
}
