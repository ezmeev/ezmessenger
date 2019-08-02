package ez.messaging.handlers;

import java.io.IOException;

import ez.connection.data.ConnectionMessage;
import ez.messaging.data.StoredMessage;
import ez.messaging.data.transport.Message;
import ez.messaging.data.transport.payload.TextMessagePayload;
import ez.messaging.helpers.MessagePayloadHelper;
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
    public void handleMessage(Message message, ConnectionMessage connectionMessage) {

        try {
            TextMessagePayload payload = MessagePayloadHelper.readPayload(message);
            long timestamp = System.currentTimeMillis(); // FIXME timesync problem for multiple instances

            var storedMessage = new StoredMessage();
            storedMessage.setText(payload.getText());
            storedMessage.setTimestamp(timestamp);
            storedMessage.setMessgeId(message.getMessageId());

            var sender = userService.getUser(message.getSenderId());
            messageStoringService.storeMessage(sender, storedMessage);

            var receiver = userService.getUser(message.getReceiverId());
            messageStoringService.storeMessage(receiver, storedMessage);

            var ackByServer = Message.createAckByServerMessage(sender.getIdentity(), message.getMessageId());
            messagePassingService.sendMessageTo(sender, ackByServer);

            messagePassingService.sendMessageTo(receiver, message);

        } catch (IOException e) {
            e.printStackTrace();
            // TODO
        }
    }
}
