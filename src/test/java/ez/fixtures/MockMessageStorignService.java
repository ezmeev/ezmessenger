package ez.fixtures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ez.messaging.data.StoredMessage;
import ez.messaging.data.User;
import ez.messaging.services.IMessageStoringService;

public class MockMessageStorignService implements IMessageStoringService {

    private Map<String, List<StoredMessage>> storedMessages = new HashMap<>();

    @Override
    public void storeMessage(User user, StoredMessage message) {
        if (!storedMessages.containsKey(user.getIdentity())) {
            storedMessages.put(user.getIdentity(), new ArrayList<>());
        }
        storedMessages.get(user.getIdentity()).add(message);
    }

    @Override
    public List<StoredMessage> getAllMessagesAfter(User sender, String messageId) {
        return null;
    }

    @Override
    public List<StoredMessage> getNMessagesBefore(User sender, int historyDepth, String lastMessageId) {
        return null;
    }
}
