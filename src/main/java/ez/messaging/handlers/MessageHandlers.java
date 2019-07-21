package ez.messaging.handlers;

import java.util.HashMap;
import java.util.Map;

import ez.messaging.data.MessageType;

public class MessageHandlers {

    private Map<MessageType, MessageHandler> handlers;

    public MessageHandlers() {
        this.handlers = new HashMap<>();
    }

    public MessageHandler getHandler(MessageType messageType) {
        return handlers.get(messageType);
    }

    public void addHandlerFor(MessageType messageType, MessageHandler messageHandler) {
        this.handlers.put(messageType, messageHandler);
    }
}
