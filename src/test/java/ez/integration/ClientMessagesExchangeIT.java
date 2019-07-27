package ez.integration;

import java.io.IOException;

import ez.EZMessenger;
import ez.fixtures.EZMessengerClient;
import ez.messaging.data.transport.Message;
import ez.messaging.data.transport.MessageType;
import ez.messaging.data.transport.payload.TextMessagePayload;
import ez.messaging.helpers.MessagePayloadHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(BlockJUnit4ClassRunner.class)
public class ClientMessagesExchangeIT {

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
    public void shouldPassMessagesWhenBothClientsOnline_x1000() throws IOException {

        messenger.stop();

        for (var i = 1; i <= 1000; i++) {

            System.out.println(" ******* RUN #" + i + " ******* ");

            messenger.start();

            var bob = new EZMessengerClient("Bob", "0.0.0.0", 8083);
            Message bobInitMessage = Message.createHelloMessage("1_1");
            bob.sendMessage(bobInitMessage);

            var alice = new EZMessengerClient("Alice", "0.0.0.0", 8083);
            Message aliceInitMessage = Message.createHelloMessage("2_2");
            alice.sendMessage(aliceInitMessage);

            Message helloAlice = Message.createTextMessage("1_1", "2_2", "Hello Alice!");
            bob.sendMessage(helloAlice);

            Message aliceHello = alice.waitMessage();
            assertNotNull(aliceHello);
            assertEquals(MessageType.TextMessage, aliceHello.getType());

            TextMessagePayload p = MessagePayloadHelper.readPayload(aliceHello);
            assertEquals("Hello Alice!", p.getText());

            messenger.stop();
        }

        messenger.start();
    }

    @Test
    public void serverShouldAcknowledgeTextMessage() throws IOException {
        var client = new EZMessengerClient("1_1", "0.0.0.0", 8083);
        Message helloMessage = Message.createHelloMessage("1_1");
        client.sendMessage(helloMessage);

        Message textMessage = Message.createTextMessage("1_1", "2_2", "Hello");
        boolean textMessageAcknowledged = client.sendMessage(textMessage);
        assertTrue(textMessageAcknowledged);
    }

    @Test
    public void senderShouldReceiveAckByServer() throws IOException {
        var client = new EZMessengerClient("1_1", "0.0.0.0", 8083);
        client.sendMessage(Message.createHelloMessage("1_1"));

        Message textMessage = Message.createTextMessage("1_1", "2_2", "Hello");
        client.sendMessage(textMessage);
        Message ackByServer = client.waitMessage();

        assertNotNull(ackByServer);
        assertEquals(MessageType.AckByServerMessage, ackByServer.getType());
    }

    @Test
    public void receiverShouldReceiveTextMessage() throws IOException {
        var client1 = new EZMessengerClient("1_1", "0.0.0.0", 8083);
        client1.sendMessage(Message.createHelloMessage("1_1"));

        var client2 = new EZMessengerClient("2_2", "0.0.0.0", 8083);
        client2.sendMessage(Message.createHelloMessage("2_2"));

        Message textMessage = Message.createTextMessage("1_1", "2_2", "Hello");
        client1.sendMessage(textMessage);
        client1.waitMessage(); // AckByServerMessage

        Message receiverMessage = client2.waitMessage();
        assertEquals(MessageType.TextMessage, receiverMessage.getType());
        assertEquals("1_1", receiverMessage.getSenderId());
        assertEquals("2_2", receiverMessage.getReceiverId());

        TextMessagePayload textMessagePayload = MessagePayloadHelper.readPayload(receiverMessage);
        assertEquals("Hello", textMessagePayload.getText());
    }

    @Test
    public void senderShouldReceiveAckByReceiverMessage() throws IOException {
        var client1 = new EZMessengerClient("1_1", "0.0.0.0", 8083);
        client1.sendMessage(Message.createHelloMessage("1_1"));

        var client2 = new EZMessengerClient("2_2", "0.0.0.0", 8083);
        client2.sendMessage(Message.createHelloMessage("2_2"));

        client1.sendMessage(Message.createTextMessage("1_1", "2_2", "Hello"));
        client1.waitMessage(); // AckByServerMessage

        Message receiverMsg = client2.waitMessage();
        client2.ackMessage(receiverMsg);

        Message ackByReceiver = client1.waitMessage();
        assertEquals(MessageType.AckByReceiverMessage, ackByReceiver.getType());
    }

    @Test
    public void senderShouldReceiveReadMessage() throws IOException {
        var client1 = new EZMessengerClient("1_1", "0.0.0.0", 8083);
        client1.sendMessage(Message.createHelloMessage("1_1"));

        var client2 = new EZMessengerClient("2_2", "0.0.0.0", 8083);
        client2.sendMessage(Message.createHelloMessage("2_2"));

        client1.sendMessage(Message.createTextMessage("1_1", "2_2", "Hello"));
        client1.waitMessage(); // AckByServerMessage

        Message receiverMsg = client2.waitMessage();
        client2.ackMessage(receiverMsg);

        client1.waitMessage(); // AckByReceiverMessage

        client2.readMessage(receiverMsg);

        Message readByClient2 = client1.waitMessage();
        assertEquals(MessageType.Read, readByClient2.getType());
    }

    @Test
    public void receiverShouldReceiveStartTypingMessage() throws IOException {
        var client1 = new EZMessengerClient("1_1", "0.0.0.0", 8083);
        client1.sendMessage(Message.createHelloMessage("1_1"));

        var client2 = new EZMessengerClient("2_2", "0.0.0.0", 8083);
        client2.sendMessage(Message.createHelloMessage("2_2"));

        client1.sendMessage(Message.createStartTypingMessage("1_1", "2_2"));

        Message receiverMsg = client2.waitMessage();
        assertEquals(MessageType.StartTyping, receiverMsg.getType());
    }

    @Test
    public void receiverShouldReceiveStopTypingMessage() throws IOException {
        var client1 = new EZMessengerClient("1_1", "0.0.0.0", 8083);
        client1.sendMessage(Message.createHelloMessage("1_1"));

        var client2 = new EZMessengerClient("2_2", "0.0.0.0", 8083);
        client2.sendMessage(Message.createHelloMessage("2_2"));

        client1.sendMessage(Message.createStopTypingMessage("1_1", "2_2"));

        Message receiverMsg = client2.waitMessage();
        assertEquals(MessageType.StopTyping, receiverMsg.getType());
    }
}
