package ez;

import ez.connection.listener.ConnectionsListener;
import ez.connection.registry.ConnectionsRegistry;
import ez.connection.queue.QueueServer;
import ez.connection.queue.messages.ClientConnectionMessageHandler;
import ez.connection.queue.registration.ClientConnectionRegistrationHandler;
import ez.connection.server.SocketConnectionsServer;
import ez.connection.server.WebsocketConnectionsServer;
import ez.messaging.data.transport.MessageType;
import ez.messaging.handlers.AckByReceiverHandler;
import ez.messaging.handlers.ByeMessageHandler;
import ez.messaging.handlers.GetHistoryHandler;
import ez.messaging.handlers.HelloMessageHandler;
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

    private SocketConnectionsServer socketServer;

    private WebsocketConnectionsServer websocketServer;

    private ConnectionsListener listener;

    private ClientConnectionMessageHandler messagesHandler;

    private ClientConnectionRegistrationHandler registrationsHandler;

    private QueueServer queueServer;

    private ConnectionsRegistry connectionsRegistry;

    private MessageRouter messageRouter;

    private EZMessenger(QueueServer queueServer, ConnectionsRegistry connectionsRegister, MessageRouter messageRouter) {
        this.queueServer = queueServer;
        this.connectionsRegistry = connectionsRegister;
        this.messageRouter = messageRouter;
    }

    public void start() {
        connectionsRegistry.init();

        socketServer = new SocketConnectionsServer(queueServer.getRegistrationsQueue());
        socketServer.start();

        websocketServer = new WebsocketConnectionsServer(queueServer.getRegistrationsQueue());
        websocketServer.start();

        listener = new ConnectionsListener(queueServer.getMessagesQueue(), connectionsRegistry);
        listener.start();

        messagesHandler = new ClientConnectionMessageHandler(queueServer.getMessagesQueue(), messageRouter);
        messagesHandler.start();

        registrationsHandler = new ClientConnectionRegistrationHandler(queueServer.getRegistrationsQueue(), connectionsRegistry);
        registrationsHandler.start();

        Logger.log("Messenger started ...");
    }

    public void stop() {
        try {
            Logger.log("Stopping messenger ...");

            socketServer.stop();

            websocketServer.stop();

            listener.stop();

            messagesHandler.stop();

            registrationsHandler.stop();

        } catch (InterruptedException e) {
            e.printStackTrace();
            // TODO
        }
    }

    public static class Configurator {

        private ConnectionsRegistry clientRegistry;

        private QueueServer queueServer;

        private MessageRouter messageRouter;

        private Configurator() {
        }

        public static EZMessenger configureDefault() {

            var clientsRegistry = new ConnectionsRegistry();
            var queueServer = new QueueServer();

            var userService = new UserService();
            var messageStoringService = new MessageStoringService();
            var messagePassingService = new MessagePassingService(clientsRegistry);

            var startTypingHandler = new StartTypingHandler(userService, messagePassingService);
            var stopTypingHandler = new StopTypingHandler(userService, messagePassingService);
            var textMessageHandler = new TextMessageHandler(userService, messagePassingService, messageStoringService);
            var ackByReceiverHandler = new AckByReceiverHandler(userService, messagePassingService);
            var getHistoryHandler = new GetHistoryHandler(userService, messagePassingService, messageStoringService);
            var helloHandler = new HelloMessageHandler(clientsRegistry);
            var readHandler = new ReadHandler(userService, messagePassingService);
            var byeMessageHandler = new ByeMessageHandler(clientsRegistry);

            var messageRouter = new MessageRouter();
            messageRouter.addHandlerFor(MessageType.AckByReceiverMessage, ackByReceiverHandler);
            messageRouter.addHandlerFor(MessageType.GetHistoryMessage, getHistoryHandler);
            messageRouter.addHandlerFor(MessageType.HelloMessage, helloHandler);
            messageRouter.addHandlerFor(MessageType.ByeMessage, byeMessageHandler);
            messageRouter.addHandlerFor(MessageType.TextMessage, textMessageHandler);
            messageRouter.addHandlerFor(MessageType.StartTyping, startTypingHandler);
            messageRouter.addHandlerFor(MessageType.StopTyping, stopTypingHandler);
            messageRouter.addHandlerFor(MessageType.Read, readHandler);

            return new EZMessenger(queueServer, clientsRegistry, messageRouter);
        }

        public static Configurator create() {
            return new Configurator();
        }

        public Configurator withClientRegistry(ConnectionsRegistry clientRegistry) {
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
