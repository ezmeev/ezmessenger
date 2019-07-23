package ez.messaging.handlers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import ez.messaging.data.User;
import ez.messaging.data.transport.Message;
import ez.messaging.data.transport.payload.GetHistoryMessagePayload;
import ez.messaging.data.transport.payload.HistoryMessagePayload;
import ez.messaging.helpers.MessagePayloadHelper;
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

        try {
            User sender = userService.getUser(message.getSenderId());
            GetHistoryMessagePayload getHistoryPayload = MessagePayloadHelper.readPayload(message);
            var messagesHistory = messageStoringService.getMessagesBefore(sender, getHistoryPayload.getLastMessageId());
            var newMessages = messageStoringService.getMessagesAfter(sender, getHistoryPayload.getLastMessageId());

            var historyPayload = new HistoryMessagePayload();
            historyPayload.setHistory(messagesHistory);
            historyPayload.setNewMessages(newMessages);

            String history = JsonConvert.serialize(historyPayload);
            Message historyMessage = Message.createHistoryMessage(sender.getIdentity(), history);
            messagePassingService.sendMessageTo(sender, historyMessage);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            // TODO
        } catch (IOException e) {
            e.printStackTrace();
            // TODO
        }
    }
}
