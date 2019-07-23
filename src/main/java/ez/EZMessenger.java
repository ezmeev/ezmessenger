package ez;

import java.io.IOException;

import ez.connection.client.ClientConnectionsListener;
import ez.connection.client.ClientsRegistry;
import ez.connection.queue.QueueServer;
import ez.connection.queue.messages.ClientConnectionMessageHandler;
import ez.connection.queue.registration.ClientConnectionRegistrationHandler;
import ez.connection.server.ClientConnectionsServer;
import ez.messaging.data.transport.MessageType;
import ez.messaging.handlers.GetHistoryMessageHandler;
import ez.messaging.handlers.MessageRouter;
import ez.messaging.handlers.TextMessageHandler;
import ez.messaging.services.MessagePassingService;
import ez.messaging.services.InMemoryMessageStoringService;
import ez.messaging.services.UserService;
import ez.util.Logger;

public class EZMessenger {

    private ClientConnectionsServer server;

    private ClientConnectionsListener listener;

    private ClientConnectionMessageHandler messagesHandler;

    private ClientConnectionRegistrationHandler registrationsHandler;

    private QueueServer queueServer;

    private ClientsRegistry connections;

    private MessageRouter messageRouter;

    private EZMessenger(QueueServer queueServer, ClientsRegistry connectionsRegister, MessageRouter messageRouter) {
        this.queueServer = queueServer;
        this.connections = connectionsRegister;
        this.messageRouter = messageRouter;
    }

    public void start() {
        try {

            server = new ClientConnectionsServer(queueServer.getRegistrationsQueue());
            server.start();

            listener = new ClientConnectionsListener(queueServer.getMessagesQueue(), connections);
            listener.start();

            messagesHandler = new ClientConnectionMessageHandler(queueServer.getMessagesQueue(), messageRouter);
            messagesHandler.start();

            registrationsHandler = new ClientConnectionRegistrationHandler(queueServer.getRegistrationsQueue(), connections);
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

    public static class Configurator {

        private ClientsRegistry clientRegistry;

        private QueueServer queueServer;

        private MessageRouter messageRouter;

        private Configurator() {
        }

        public static EZMessenger configureDefault() {

            var clientsRegistry = new ClientsRegistry();
            var queueServer = new QueueServer();

            var userService = new UserService();
            var messageStoringService = new InMemoryMessageStoringService();
            var messagePassingService = new MessagePassingService(clientsRegistry);

            var getHistoryMessageHandler = new GetHistoryMessageHandler(userService,
                messagePassingService, messageStoringService);

            var textMessageHandler = new TextMessageHandler(userService, messagePassingService, messageStoringService);

            var messageRouter = new MessageRouter();
            messageRouter.addHandlerFor(MessageType.GetHistoryMessage, getHistoryMessageHandler);
            messageRouter.addHandlerFor(MessageType.TextMessage, textMessageHandler);

            return new EZMessenger(queueServer, clientsRegistry, messageRouter);
        }

        public static Configurator create() {
            return new Configurator();
        }

        public Configurator withClientRegistry(ClientsRegistry clientRegistry) {
            this.clientRegistry = clientRegistry;
            return this;
        }

        public Configurator withQueueServer(QueueServer queueServer) {
            this.queueServer = queueServer;
            return this;
        }

        public Configurator withMessageRouter(MessageRouter messageRouter) {
            this.messageRouter = messageRouter;
            return this;
        }

        public EZMessenger build() {
            return new EZMessenger(queueServer, clientRegistry, messageRouter);
        }
    }
}
