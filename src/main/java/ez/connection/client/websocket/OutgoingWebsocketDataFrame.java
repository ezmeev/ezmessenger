package ez.connection.client.websocket;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class OutgoingWebsocketDataFrame {

    public final byte[] frameData;

    public OutgoingWebsocketDataFrame(byte[] payload) {
        this.frameData = createFrameData(payload);
    }

    public OutgoingWebsocketDataFrame(String payload) {
        this(payload.getBytes(StandardCharsets.UTF_8));
    }

    private static byte[] createFrameData(byte[] payload) {

        var bytesStream = new ByteArrayOutputStream();

        bytesStream.write((byte) 0b1000_0001);

        int length = payload.length;
        if (length < 126) {
            bytesStream.write((byte) length);
        } else if (length == 126) {
            bytesStream.write((byte) 126);
            bytesStream.writeBytes(BigInteger.valueOf(payload.length).toByteArray());
        } else {
            bytesStream.write((byte) 127);
            bytesStream.writeBytes(BigInteger.valueOf(payload.length).toByteArray());
        }
        bytesStream.writeBytes(payload);
        return bytesStream.toByteArray();
    }
}
