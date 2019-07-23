package ez.messaging.helpers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import ez.messaging.data.transport.Message;
import ez.messaging.data.transport.payload.AckByServerMessagePayload;
import ez.messaging.data.transport.payload.GetHistoryMessagePayload;
import ez.messaging.data.transport.payload.HelloMessagePayload;
import ez.messaging.data.transport.payload.HistoryMessagePayload;
import ez.messaging.data.transport.payload.TextMessagePayload;
import ez.util.JsonConvert;

public class MessagePayloadHelper {

    public static <T> T readPayload(Message message) throws IOException {
        var type = message.getType();

        switch (type) {
            case HelloMessage:
                return (T) JsonConvert.deserialize(toJsonString(message.getData()), HelloMessagePayload.class);
            case GetHistoryMessage:
                return (T) JsonConvert.deserialize(toJsonString(message.getData()), GetHistoryMessagePayload.class);
            case HistoryMessage:
                return (T) JsonConvert.deserialize(toJsonString(message.getData()), HistoryMessagePayload.class);
            case TextMessage:
                return (T) JsonConvert.deserialize(toJsonString(message.getData()), TextMessagePayload.class);
            case AckByServerMessage:
                return (T) JsonConvert.deserialize(toJsonString(message.getData()), AckByServerMessagePayload.class);
            case StartTyping:
            case StopTyping:
            case Received:
            case AckByReceiverMessage:
            case Read:
            default:
                throw new RuntimeException("Message type [" + message.getType() + "]: payload not supported");
        }
    }

    public static <T> byte[] toBytes(T data) {
        try {
            String json = JsonConvert.serialize(data);
            return json.getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            // TODO
            return null;
        }
    }

    private static String toJsonString(byte[] data) {
        return new String(data, StandardCharsets.UTF_8);
    }
}
