package ez.messaging.handlers;

import java.io.IOException;

import ez.messaging.data.StoredMessage;
import ez.messaging.data.User;
import ez.messaging.data.transport.Message;
import ez.messaging.data.transport.payload.TextMessagePayload;
import ez.messaging.helpers.MessagePayloadHelper;
import ez.messaging.services.InMemoryMessageStoringService;
import ez.messaging.services.MessagePassingService;
import ez.messaging.services.UserService;

public class TextMessageHandler implements MessageHandler {

    private UserService userService;

    private MessagePassingService messagePassingService;

    private InMemoryMessageStoringService messageStoringService;

    public TextMessageHandler(
        UserService userService,
        MessagePassingService messagePassingService,
        InMemoryMessageStoringService messageStoringService
    ) {
        this.userService = userService;
        this.messagePassingService = messagePassingService;
        this.messageStoringService = messageStoringService;
    }

    @Override
    public void handleMessage(Message message) {

        try {
            TextMessagePayload payload = MessagePayloadHelper.readPayload(message);
            long timestamp = System.currentTimeMillis(); // FIXME timesync problem for multiple instances

            var senderMessage = new StoredMessage();
            senderMessage.setText(payload.getText());
            senderMessage.setTimestamp(timestamp);
            senderMessage.setMessgeId(message.getMessageId());

            var receiverMessage = new StoredMessage();
            receiverMessage.setText(payload.getText());
            receiverMessage.setTimestamp(timestamp);
            receiverMessage.setMessgeId(message.getMessageId());

            User sender = userService.getUser(message.getSenderId());
            messageStoringService.storeMessage(sender, senderMessage);

            User receiver = userService.getUser(message.getReceiverId());
            messageStoringService.storeMessage(receiver, receiverMessage);

            messagePassingService.tryAcknowledgeMessage(sender, message);
            messagePassingService.sendMessageTo(receiver, message);

        } catch (IOException e) {
            e.printStackTrace();
            // TODO
        }
    }
}
