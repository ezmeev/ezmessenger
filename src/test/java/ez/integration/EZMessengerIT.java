package ez.integration;

import java.io.IOException;

import ez.EZMessenger;
import ez.fixtures.EZMessengerClient;
import ez.messaging.data.Message;
import ez.messaging.data.MessageType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(BlockJUnit4ClassRunner.class)
public class EZMessengerIT {

    private EZMessenger messenger;

    @Before
    public void setUp() {
        messenger = new EZMessenger();
        messenger.start();
    }

    @After
    public void tearDown() {
        messenger.stop();
    }

    @Test
    public void shouldStopThenStartAndStop() {
        // verifying that sockets and other resources released without problems
        messenger.stop();
        messenger.start();
        messenger.stop();
    }

    @Test
    public void serverShouldAcknowledge_whenReceivesHelloMessage() throws IOException {
        var client1 = new EZMessengerClient("1_1", "0.0.0.0", 8083);
        Message helloMessage = Message.createHelloMessage("1_1");
        boolean ack = client1.sendMessage(helloMessage);
        assertTrue(ack);
    }

    @Test
    public void serverShouldReturnHistory_whenReceivesGetHistoryMessage() throws IOException {
        var client1 = new EZMessengerClient("1_1", "0.0.0.0", 8083);
        client1.sendMessage(Message.createHelloMessage("1_1"));
        client1.sendMessage(Message.createGetHistoryMessage("1_1"));
        Message history = client1.waitMessage();
        assertEquals(MessageType.HistoryMessage, history.getType());
    }

    @Test
    public void shouldAcknowledgeTextMessage() throws IOException {
        var client = new EZMessengerClient("1_1", "0.0.0.0", 8083);
        Message helloMessage = Message.createHelloMessage("1_1");
        boolean initMessageAcknowledged = client.sendMessage(helloMessage);
        assertTrue(initMessageAcknowledged);

        Message textMessage = Message.createTextMessage("1_1", "2_2", "Hello");
        boolean textMessageAcknowledged = client.sendMessage(textMessage);
        assertTrue(textMessageAcknowledged);

        Message textMessageAcknowledge = client.waitMessage();
        assertNotNull(textMessageAcknowledge);
    }

    @Test
    public void shouldPassMessages_WhenBothClientsPresent() throws IOException {

        messenger.stop();

        for (var i = 1; i <= 100; i++) {

            System.out.println(" ******* RUN #" + i + " ******* ");

            messenger.start();

            var bob = new EZMessengerClient("Bob", "0.0.0.0", 8083);
            Message bobInitMessage = Message.createHelloMessage("1_1");
            bob.sendMessage(bobInitMessage);

            var alice = new EZMessengerClient("Alice", "0.0.0.0", 8083);
            Message aliceInitMessage = Message.createHelloMessage("2_2");
            alice.sendMessage(aliceInitMessage);

            Message helloAlice = Message.createTextMessage("1_1", "2_2", "Hello Alice!");
            boolean messageAcknowledged = bob.sendMessage(helloAlice);
            assertTrue(messageAcknowledged);

            Message aliceHello = alice.waitMessage();
            assertNotNull(aliceHello);
            assertEquals("Hello Alice!", aliceHello.getData());

            messenger.stop();
        }

        messenger.start();
    }
}
