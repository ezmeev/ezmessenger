package ez.messaging.handlers;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import ez.messaging.data.Message;
import ez.messaging.data.User;
import ez.messaging.services.MessagePassingService;
import ez.messaging.services.MessageStoringService;
import ez.messaging.services.UserService;
import ez.util.JsonConvert;

public class GetHistoryMessageHandler implements MessageHandler {

    private UserService userService;

    private MessagePassingService messagePassingService;

    private MessageStoringService messageStoringService;

    public GetHistoryMessageHandler(
        UserService userService,
        MessagePassingService messagePassingService,
        MessageStoringService messageStoringService
    ) {
        this.userService = userService;
        this.messagePassingService = messagePassingService;
        this.messageStoringService = messageStoringService;
    }

    @Override
    public void handleMessage(Message message) {

        User sender = userService.getUser(message.getSenderId());

        var timestamp = 1L;

        List<String> userMessagesSince = messageStoringService.getUserMessagesSince(sender, timestamp);

        try {
            String history = JsonConvert.serialize(userMessagesSince);
            Message historyMessage = Message.createHistoryMessage(sender.getIdentity(), history);
            messagePassingService.tryDeliverMessage(sender, historyMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            // TODO
        }
    }
}
