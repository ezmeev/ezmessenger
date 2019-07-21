package ez.messaging.services;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import ez.connection.data.ConnectionMessage;
import ez.messaging.data.Message;
import ez.messaging.handlers.MessageHandler;
import ez.messaging.handlers.MessageHandlers;

public class MessageRouter {

    private ObjectMapper objectMapper = new ObjectMapper();

    private MessageHandlers messageHandlers;

    public MessageRouter(MessageHandlers messageHandlers) {
        this.messageHandlers = messageHandlers;
    }

    public void route(ConnectionMessage message) {
        String messageData = new String(message.getData());
        System.out.println("[SERVER] Routing message: " + messageData);

        try {

            Message parsedMessage = objectMapper.readValue(messageData, Message.class);

            MessageHandler handler = messageHandlers.getHandler(parsedMessage.getType());

            handler.handleMessage(parsedMessage);

        } catch (IOException e) {
            e.printStackTrace();
            // TODO
        }
    }
}
