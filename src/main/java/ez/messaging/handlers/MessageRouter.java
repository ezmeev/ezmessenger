package ez.messaging.handlers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import ez.connection.data.ConnectionMessage;
import ez.messaging.data.transport.Message;
import ez.messaging.data.transport.MessageType;

public class MessageRouter {

    private ObjectMapper objectMapper = new ObjectMapper();

    private Map<MessageType, MessageHandler> messageHandlers;

    public MessageRouter() {
        this.messageHandlers = new HashMap<>();
    }

    public void route(ConnectionMessage message) {
        String messageData = new String(message.getData());
        System.out.println("[SERVER] Routing message: " + messageData);

        try {

            Message parsedMessage = objectMapper.readValue(messageData, Message.class);

            MessageHandler handler = messageHandlers.get(parsedMessage.getType());

            handler.handleMessage(parsedMessage);

        } catch (IOException e) {
            e.printStackTrace();
            // TODO
        }
    }

    public void addHandlerFor(MessageType messageType, MessageHandler messageHandler) {
        this.messageHandlers.put(messageType, messageHandler);
    }
}
