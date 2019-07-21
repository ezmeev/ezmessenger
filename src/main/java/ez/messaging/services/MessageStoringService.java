package ez.messaging.services;

import java.util.ArrayList;
import java.util.List;

import ez.messaging.data.Message;
import ez.messaging.data.User;

public class MessageStoringService {
    public void storeMessage(User sender, Message message) {

    }

    public List<String> getUserMessagesSince(User sender, long timestamp) {
        return new ArrayList<>();
    }
}
