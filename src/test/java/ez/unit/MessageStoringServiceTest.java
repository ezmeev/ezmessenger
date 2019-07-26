package ez.unit;

import java.util.List;
import java.util.Map;

import ez.messaging.data.StoredMessage;
import ez.messaging.data.User;
import ez.messaging.data.access.InMemoryStorageEngine;
import ez.messaging.services.MessageStoringService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

@RunWith(BlockJUnit4ClassRunner.class)
public class MessageStoringServiceTest {

    private MessageStoringService messageStoringService;

    private InMemoryStorageEngine inMemoryStorageEngine;


    @Before
    public void setup() {
        inMemoryStorageEngine = new InMemoryStorageEngine();
        messageStoringService = new MessageStoringService(inMemoryStorageEngine);
    }

    @Test
    public void storeMessage_shouldStoreMessage() {

        User user = new User("1");

        StoredMessage message = new StoredMessage();
        message.setText("Hi, how is it going?");
        message.setTimestamp(System.currentTimeMillis());

        messageStoringService.storeMessage(user, message);

        Map<String, List<StoredMessage>> storedMessages = inMemoryStorageEngine.getStoredMessages();
        assertEquals(1, storedMessages.size());
        assertTrue(storedMessages.containsKey("1"));
        assertEquals(1, storedMessages.get("1").size());
        StoredMessage storedMessage = storedMessages.get("1").get(0);
        assertEquals("Hi, how is it going?", storedMessage.getText());
    }

    @Test
    public void storeMessage_shouldSetMessageId() {

        User user = new User("1");

        StoredMessage message = new StoredMessage();
        message.setText("Hi, how is it going?");
        message.setTimestamp(System.currentTimeMillis());

        messageStoringService.storeMessage(user, message);

        Map<String, List<StoredMessage>> storedMessages = inMemoryStorageEngine.getStoredMessages();
        StoredMessage storedMessage = storedMessages.get("1").get(0);
        assertNotNull(storedMessage.getMessgeId());
    }

    @Test
    public void storeMessage_shouldSetTimestamp() {

        User user = new User("1");

        StoredMessage message = new StoredMessage();
        message.setText("Hi, how is it going?");
        message.setTimestamp(System.currentTimeMillis());

        messageStoringService.storeMessage(user, message);

        Map<String, List<StoredMessage>> storedMessages = inMemoryStorageEngine.getStoredMessages();
        StoredMessage storedMessage = storedMessages.get("1").get(0);
        assertTrue(storedMessage.getTimestamp() > 0);
    }
}
