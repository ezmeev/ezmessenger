package ez.messaging.handlers;

import ez.messaging.data.Message;
import ez.messaging.data.User;
import ez.messaging.services.MessagePassingService;
import ez.messaging.services.MessageStoringService;
import ez.messaging.services.UserService;

public class TextMessageHandler implements MessageHandler {

    private UserService userService;

    private MessagePassingService messagePassingService;

    private MessageStoringService messageStoringService;

    public TextMessageHandler(
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
        messageStoringService.storeMessage(sender, message);

        User receiver = userService.getUser(message.getReceiverId());
        messageStoringService.storeMessage(receiver, message);

        messagePassingService.tryAcknowledgeMessage(sender, message);
        messagePassingService.tryDeliverMessage(receiver, message);
    }
}
