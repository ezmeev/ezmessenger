package ez.messaging.data.access;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ez.messaging.data.StoredMessage;

public class InMemoryStorageEngine implements IStorageEngine {

    private Map<String, List<StoredMessage>> storedMessages;

    public InMemoryStorageEngine() {
        this.storedMessages = new HashMap<>();
    }

    @Override
    public StoredMessage store(String userId, StoredMessage message) {
        if (!storedMessages.containsKey(userId)) {
            storedMessages.put(userId, new ArrayList<>());
        }

        message.setMessgeId(UUID.randomUUID().toString());

        storedMessages.get(userId).add(message);

        return message;
    }

    @Override
    public StoredMessage findMessage(String messageId) {
        return storedMessages.values().stream()
            .flatMap(Collection::stream)
            .filter(m -> m.getMessgeId().equals(messageId))
            .findFirst()
            .orElse(null);
    }

    @Override
    public List<StoredMessage> findUserMessagesLaterThen(String userId, long timestamp) {
        if (!storedMessages.containsKey(userId)) {
            return new ArrayList<>();
        }

        List<StoredMessage> userMessages = this.storedMessages.get(userId);

        var result = new ArrayList<StoredMessage>();

        for (StoredMessage message : userMessages) {
            if (message.getTimestamp() > timestamp) {
                result.add(message);
            }
        }

        return result;
    }

    @Override
    public List<StoredMessage> findNUserMessagesEarlierThen(String userId, long timestamp, int n) {
        if (!storedMessages.containsKey(userId)) {
            return new ArrayList<>();
        }

        List<StoredMessage> userMessages = this.storedMessages.get(userId);

        var result = new ArrayList<StoredMessage>();

        for (int i = userMessages.size() - 1; i >= 0; i--) {
            StoredMessage m = userMessages.get(i);
            if (m.getTimestamp() < timestamp) {
                result.add(m);
            }

            if (result.size() == n) {
                break;
            }
        }

        return result;
    }

    public Map<String, List<StoredMessage>> getStoredMessages() {
        return storedMessages;
    }
}
