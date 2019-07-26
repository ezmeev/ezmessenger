package ez.messaging.services;

import java.util.List;

import ez.messaging.data.StoredMessage;
import ez.messaging.data.User;
import ez.messaging.data.access.IStorageEngine;
import ez.messaging.data.access.InMemoryStorageEngine;

public class MessageStoringService implements IMessageStoringService {

    private IStorageEngine storageEngine;

    public MessageStoringService() {
        this.storageEngine = new InMemoryStorageEngine();
    }

    public MessageStoringService(IStorageEngine storageEngine) {
        this.storageEngine = storageEngine;
    }

    public void storeMessage(User user, StoredMessage message) {
        storageEngine.store(user.getIdentity(), message);
    }

    public List<StoredMessage> getAllMessagesAfter(User user, String messageId) {
        if (messageId.equals("0")) {
            return storageEngine.findUserMessagesLaterThen(user.getIdentity(), 0);
        }

        StoredMessage message = storageEngine.findMessage(messageId);
        return storageEngine.findUserMessagesLaterThen(user.getIdentity(), message.getTimestamp());
    }

    public List<StoredMessage> getNMessagesBefore(User user, int historyDepth, String lastMessageId) {
        if (lastMessageId.equals("0")) {
            return storageEngine.findNUserMessagesEarlierThen(user.getIdentity(), Long.MAX_VALUE, historyDepth);
        }

        StoredMessage message = storageEngine.findMessage(lastMessageId);
        return storageEngine.findNUserMessagesEarlierThen(user.getIdentity(), message.getTimestamp(), historyDepth);
    }
}
