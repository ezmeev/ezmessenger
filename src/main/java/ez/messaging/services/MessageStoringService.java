package ez.messaging.services;

import java.util.ArrayList;
import java.util.List;

import ez.messaging.data.StoredMessage;
import ez.messaging.data.User;
import ez.messaging.data.transport.Message;

public class MessageStoringService {
    public void storeMessage(User sender, Message message) {

    }

    public List<StoredMessage> getMessagesAfter(User sender, String messageId) {
        return new ArrayList<>();
    }

    public List<StoredMessage> getMessagesBefore(User sender, String lastMessageId) {
        return new ArrayList<>();
    }
}
