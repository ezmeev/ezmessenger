package ez.integration;

import java.io.IOException;
import java.util.List;

import ez.EZMessenger;
import ez.connection.registry.ConnectionsRegistry;
import ez.connection.queue.QueueServer;
import ez.fixtures.EZMessengerClient;
import ez.messaging.data.StoredMessage;
import ez.messaging.data.User;
import ez.messaging.data.transport.Message;
import ez.messaging.data.transport.MessageType;
import ez.messaging.data.transport.payload.HistoryMessagePayload;
import ez.messaging.handlers.GetHistoryHandler;
import ez.messaging.handlers.HelloMessageHandler;
import ez.messaging.handlers.MessageRouter;
import ez.messaging.handlers.TextMessageHandler;
import ez.messaging.helpers.MessagePayloadHelper;
import ez.messaging.services.MessageStoringService;
import ez.messaging.services.MessagePassingService;
import ez.messaging.services.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(BlockJUnit4ClassRunner.class)
public class ClientInitiatesConnectionIT {

    private EZMessenger messenger;

    private MessageStoringService messageStoringService;

    @Before
    public void setUp() {

        ConnectionsRegistry connectionsRegistry = new ConnectionsRegistry();
        MessagePassingService messagePassingService = new MessagePassingService(connectionsRegistry);
        messageStoringService = new MessageStoringService();

        var userService = new UserService();

        var getHistoryMessageHandler = new GetHistoryHandler(userService,
            messagePassingService, messageStoringService);

        var textMessageHandler = new TextMessageHandler(userService,
            messagePassingService, messageStoringService);

        var helloHandler = new HelloMessageHandler(connectionsRegistry);

        var messageRouter = new MessageRouter();
        messageRouter.addHandlerFor(MessageType.TextMessage, textMessageHandler);
        messageRouter.addHandlerFor(MessageType.HelloMessage, helloHandler);
        messageRouter.addHandlerFor(MessageType.GetHistoryMessage, getHistoryMessageHandler);

        messenger = EZMessenger.Configurator.create()
            .withClientRegistry(connectionsRegistry)
            .withMessageRouter(messageRouter)
            .withQueueServer(new QueueServer())
            .build();
        messenger.start();
    }

    @After
    public void tearDown() {
        messenger.stop();
    }

    @Test
    public void serverShouldAcknowledge_whenReceivesHelloMessage() throws IOException {
        var client = new EZMessengerClient("1_1", "0.0.0.0", 8083);
        Message helloMessage = Message.createHelloMessage("1_1");
        boolean ack = client.sendMessage(helloMessage);
        assertTrue(ack);
    }

    @Test
    public void serverShouldAcknowledge_whenReceivesHelloMessage_andClientReconnects() throws IOException {
        var client = new EZMessengerClient("1_1", "0.0.0.0", 8083);
        Message helloMessage = Message.createHelloMessage("1_1");
        boolean ack1 = client.sendMessage(helloMessage);
        assertTrue(ack1);

        boolean ack2 = client.sendMessage(helloMessage);
        assertTrue(ack2);
    }

    @Test
    public void serverShouldReturnHistory_whenReceivesGetHistoryMessage_andBlankState() throws IOException {
        var client = new EZMessengerClient("1_1", "0.0.0.0", 8083);
        client.sendMessage(Message.createHelloMessage("1_1"));
        client.sendMessage(Message.createGetHistoryMessage("1_1", "0"));
        Message historyMessage = client.waitMessage();
        assertEquals(MessageType.HistoryMessage, historyMessage.getType());

        HistoryMessagePayload payload = MessagePayloadHelper.readPayload(historyMessage);

        List<StoredMessage> history = payload.getHistory();
        assertNotNull(history);
        assertTrue(history.isEmpty());

        List<StoredMessage> newMessages = payload.getNewMessages();
        assertNotNull(newMessages);
        assertTrue(newMessages.isEmpty());
    }

    @Test
    public void serverShouldReturnHistory_whenReceivesGetHistoryMessage_andHistoryExists() throws IOException {

        StoredMessage message1 = new StoredMessage();
        message1.setTimestamp(1);
        message1.setText("Hi");
        message1.setMessgeId("1");
        messageStoringService.storeMessage(new User("1_1"), message1);

        StoredMessage message2 = new StoredMessage();
        message2.setTimestamp(2);
        message2.setText("How are you?");
        message2.setMessgeId("2");
        messageStoringService.storeMessage(new User("1_1"), message2);

        var client = new EZMessengerClient("1_1", "0.0.0.0", 8083);
        client.sendMessage(Message.createHelloMessage("1_1"));
        client.sendMessage(Message.createGetHistoryMessage("1_1", "0"));
        Message historyMessage = client.waitMessage();
        assertEquals(MessageType.HistoryMessage, historyMessage.getType());

        HistoryMessagePayload payload = MessagePayloadHelper.readPayload(historyMessage);

        List<StoredMessage> history = payload.getHistory();
        assertNotNull(history);
        assertEquals(2, history.size());
    }
}
