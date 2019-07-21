package ez.messaging.handlers;

import ez.messaging.data.Message;

public interface MessageHandler {

    void handleMessage(Message message);
}
