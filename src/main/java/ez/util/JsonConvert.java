package ez.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonConvert {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> String serialize(T object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    public static <T> T deserialize(String json, Class<T> klass) throws IOException {
        return objectMapper.readValue(json, klass);
    }
}
