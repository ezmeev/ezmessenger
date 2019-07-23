package ez.messaging.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ez.messaging.data.StoredMessage;
import ez.messaging.data.User;

public class InMemoryMessageStoringService implements IMessageStoringService {

    private Map<String, List<StoredMessage>> storedMessages;

    public InMemoryMessageStoringService() {
        this.storedMessages = new HashMap<>();
    }

    public void storeMessage(User user, StoredMessage message) {

        if (!storedMessages.containsKey(user.getIdentity())) {
            storedMessages.put(user.getIdentity(), new ArrayList<>());
        }

        storedMessages.get(user.getIdentity()).add(message);
    }

    public List<StoredMessage> getAllMessagesAfter(User sender, String messageId) {
        if (!storedMessages.containsKey(sender.getIdentity())) {
            return new ArrayList<>();
        }

        List<StoredMessage> storedMessages = this.storedMessages.get(sender.getIdentity());

        for (int i = 0; i < storedMessages.size(); i++) {
            StoredMessage storedMessage = storedMessages.get(i);
            if (storedMessage.getMessgeId().equals(messageId)) {
                return storedMessages.subList(i, storedMessages.size());
            }
        }

        return new ArrayList<>();
    }

    public List<StoredMessage> getNMessagesBefore(User sender, int historyDepth, String lastMessageId) {
        if (!storedMessages.containsKey(sender.getIdentity())) {
            return new ArrayList<>();
        }

        List<StoredMessage> storedMessages = this.storedMessages.get(sender.getIdentity());

        for (int i = 0; i < storedMessages.size(); i++) {
            StoredMessage storedMessage = storedMessages.get(i);
            if (storedMessage.getMessgeId().equals(lastMessageId)) {
                return storedMessages.subList(Math.max(0, i - historyDepth), i);
            }
        }

        var adjustedDepth = Math.min(historyDepth, storedMessages.size());

        var result = new ArrayList<StoredMessage>(adjustedDepth);
        for (int i = 1; i <= adjustedDepth; i++) {
            result.add(storedMessages.get(storedMessages.size() - i));
        }

        return result;
    }
}
