package ez.messaging.data.access;

import java.util.List;

import ez.messaging.data.StoredMessage;

public interface IStorageEngine {

    StoredMessage store(String userId, StoredMessage message);

    StoredMessage findMessage(String messageId);

    List<StoredMessage> findUserMessagesLaterThen(String userId, long timestamp);

    List<StoredMessage> findNUserMessagesEarlierThen(String userId, long timestamp, int n);

}
