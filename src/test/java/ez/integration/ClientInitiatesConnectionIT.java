package ez.integration;

import java.io.IOException;
import java.util.List;

import ez.EZMessenger;
import ez.fixtures.EZMessengerClient;
import ez.messaging.data.StoredMessage;
import ez.messaging.data.transport.Message;
import ez.messaging.data.transport.MessageType;
import ez.messaging.data.transport.payload.HistoryMessagePayload;
import ez.messaging.helpers.MessagePayloadHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(BlockJUnit4ClassRunner.class)
public class ClientInitiatesConnectionIT {

    private EZMessenger messenger;

    @Before
    public void setUp() {
        messenger = EZMessenger.Configurator.configureDefault();
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

        fail(); // TODO mock messageStoringService

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
