package ez.messaging.services;

import java.util.List;

import ez.messaging.data.StoredMessage;
import ez.messaging.data.User;

public interface IMessageStoringService {

    void storeMessage(User sender, StoredMessage message);

    List<StoredMessage> getAllMessagesAfter(User sender, String messageId);

    List<StoredMessage> getNMessagesBefore(User sender, int historyDepth, String lastMessageId);
}
