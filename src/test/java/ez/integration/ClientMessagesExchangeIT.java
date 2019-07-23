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
    public void shouldPassMessages_WhenBothClientsOnline() throws IOException {

        messenger.stop();

        for (var i = 1; i <= 500; i++) {

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
}
