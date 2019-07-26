package ez;

import java.io.IOException;

import ez.connection.client.ClientConnectionsListener;
import ez.connection.client.ClientsRegistry;
import ez.connection.queue.QueueServer;
import ez.connection.queue.messages.ClientConnectionMessageHandler;
import ez.connection.queue.registration.ClientConnectionRegistrationHandler;
import ez.connection.server.ClientConnectionsServer;
import ez.messaging.data.transport.MessageType;
import ez.messaging.handlers.AckByReceiverHandler;
import ez.messaging.handlers.GetHistoryHandler;
import ez.messaging.handlers.MessageRouter;
import ez.messaging.handlers.ReadHandler;
import ez.messaging.handlers.StartTypingHandler;
import ez.messaging.handlers.StopTypingHandler;
import ez.messaging.handlers.TextMessageHandler;
import ez.messaging.services.MessagePassingService;
import ez.messaging.services.MessageStoringService;
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
            var messageStoringService = new MessageStoringService();
            var messagePassingService = new MessagePassingService(clientsRegistry);

            var startTypingHandler = new StartTypingHandler(userService, messagePassingService);
            var stopTypingHandler = new StopTypingHandler(userService, messagePassingService);
            var textMessageHandler = new TextMessageHandler(userService, messagePassingService, messageStoringService);
            var ackByReceiverHandler = new AckByReceiverHandler(userService, messagePassingService);
            var getHistoryHandler = new GetHistoryHandler(userService, messagePassingService, messageStoringService);
            var readHandler = new ReadHandler(userService, messagePassingService);

            var messageRouter = new MessageRouter();
            messageRouter.addHandlerFor(MessageType.AckByReceiverMessage, ackByReceiverHandler);
            messageRouter.addHandlerFor(MessageType.GetHistoryMessage, getHistoryHandler);
            messageRouter.addHandlerFor(MessageType.TextMessage, textMessageHandler);
            messageRouter.addHandlerFor(MessageType.StartTyping, startTypingHandler);
            messageRouter.addHandlerFor(MessageType.StopTyping, stopTypingHandler);
            messageRouter.addHandlerFor(MessageType.Read, readHandler);

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
