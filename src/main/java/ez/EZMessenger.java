package ez;

import java.io.IOException;

import ez.connection.client.ClientConnectionsListener;
import ez.connection.client.ClientConnectionsRegister;
import ez.connection.queue.messages.ClientConnectionMessageHandler;
import ez.connection.queue.messages.ClientConnectionMessageQueue;
import ez.connection.queue.registration.ClientConnectionRegistrationHandler;
import ez.connection.queue.registration.ClientConnectionRegistrationQueue;
import ez.connection.server.ClientConnectionsServer;
import ez.messaging.data.MessageType;
import ez.messaging.handlers.GetHistoryMessageHandler;
import ez.messaging.handlers.MessageHandlers;
import ez.messaging.handlers.TextMessageHandler;
import ez.messaging.services.MessagePassingService;
import ez.messaging.services.MessageRouter;
import ez.messaging.services.MessageStoringService;
import ez.messaging.services.UserService;
import ez.util.Logger;

public class EZMessenger {

    private ClientConnectionsServer server;

    private ClientConnectionsListener listener;

    private ClientConnectionMessageHandler messagesHandler;

    private ClientConnectionRegistrationHandler registrationsHandler;

    public void start() {
        try {

            ClientConnectionsRegister connections = new ClientConnectionsRegister();
            ClientConnectionMessageQueue messagesQueue = new ClientConnectionMessageQueue();
            ClientConnectionRegistrationQueue registrationsQueue = new ClientConnectionRegistrationQueue();

            UserService userMessageService = new UserService();
            MessagePassingService messagePassingService = new MessagePassingService(connections);
            MessageStoringService messageStoringService = new MessageStoringService();

            GetHistoryMessageHandler getHistoryMessageHandler = new GetHistoryMessageHandler(userMessageService,
                messagePassingService, messageStoringService);

            TextMessageHandler textMessageHandler = new TextMessageHandler(userMessageService,
                messagePassingService, messageStoringService);

            MessageHandlers handlers = new MessageHandlers();
            handlers.addHandlerFor(MessageType.TextMessage, textMessageHandler);
            handlers.addHandlerFor(MessageType.GetHistoryMessage, getHistoryMessageHandler);

            MessageRouter router = new MessageRouter(handlers);

            server = new ClientConnectionsServer(registrationsQueue);
            server.start();

            listener = new ClientConnectionsListener(messagesQueue, connections);
            listener.start();

            messagesHandler = new ClientConnectionMessageHandler(messagesQueue, router);
            messagesHandler.start();

            registrationsHandler = new ClientConnectionRegistrationHandler(registrationsQueue, connections);
            registrationsHandler.start();

        } catch (IOException e) {
            e.printStackTrace();
            // TODO
        }
    }

    public void stop() {
        try {
            Logger.log("Stopping messenger ...");

            server.stop();

            listener.stop();

            messagesHandler.stop();

            registrationsHandler.stop();

        } catch (InterruptedException e) {
            e.printStackTrace();
            // TODO
        }
    }
}
