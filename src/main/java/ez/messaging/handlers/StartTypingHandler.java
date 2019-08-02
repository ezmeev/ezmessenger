package ez.messaging.handlers;

import ez.connection.data.ConnectionMessage;
import ez.messaging.data.User;
import ez.messaging.data.transport.Message;
import ez.messaging.services.MessagePassingService;
import ez.messaging.services.UserService;

public class StartTypingHandler implements MessageHandler {

    private UserService userService;

    private MessagePassingService messagePassingService;

    public StartTypingHandler(
        UserService userService,
        MessagePassingService messagePassingService
    ) {
        this.userService = userService;
        this.messagePassingService = messagePassingService;
    }

    @Override
    public void handleMessage(Message message, ConnectionMessage connectionMessage) {

        try {
            User receiver = userService.getUser(message.getReceiverId());
            messagePassingService.sendMessageTo(receiver, message);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO
        }
    }
}
