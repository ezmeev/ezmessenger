package ez.connection.client.websocket;

import java.math.BigInteger;

public class IncomingWebsocketDataFrame {

    public final FrameOpCode opCode;

    public final byte[] payload;

    public IncomingWebsocketDataFrame(byte[] data) {

        opCode = getOpCode(data);

        long payloadLength = getPayloadLength(data);

        if (isMasked(data)) {
            byte[] mask = getMask(data, payloadLength);
            payload = getPayload(mask, payloadLength, data);
        } else {
            payload = getPayload(payloadLength, data);
        }
    }

    private static byte[] getPayload(byte[] mask, long payloadLength, byte[] data) {
        byte[] payload = getPayload(payloadLength, data);

        for (int i = 0; i < payloadLength; i++) {
            byte b = payload[i];
            byte m = mask[i % 4];
            payload[i] = (byte) (b ^ m);
        }

        return payload;
    }

    private static byte[] getPayload(long payloadLengthLong, byte[] data) {
        if (payloadLengthLong > Integer.MAX_VALUE) {
            throw new RuntimeException("Too long messages (>Integer.MAX_VALUE) not implemented yet");
        }
        int payloadLength = (int) payloadLengthLong;
        var startFromByte = 15;
        if (payloadLength <= Byte.MAX_VALUE) {
            startFromByte = 7;
        } else if (payloadLength <= Short.MAX_VALUE) {
            startFromByte = 9;
        }

        var payload = new byte[payloadLength];
        System.arraycopy(data, startFromByte - 1, payload, 0, payloadLength);
        return payload;
    }

    private static byte[] getMask(byte[] data, long payloadLength) {
        // masking key (4 bytes):
        // when payload length 8 bytes: mask is from 11th to 14th byte
        // when payload length 2 bytes: mask is from 5th to 8th byte
        // when payload length 1 byte: mask is from 3rd to 6th byte

        var mask = new byte[4];

        var startFromByte = 11;

        if (payloadLength <= Byte.MAX_VALUE) {
            startFromByte = 3;
        } else if (payloadLength <= Short.MAX_VALUE) {
            startFromByte = 5;
        }
        System.arraycopy(data, startFromByte - 1, mask, 0, 4);
        return mask;
    }

    private static long toLong(byte[] data) {
        return new BigInteger(data).longValueExact();
    }

    private static long getPayloadLength(byte[] data) {
        // payload len: last 7 bits of second byte
        byte b1 = data[1];

        byte length = (byte) (b1 & 0b01111111);

        if (length == 127) {
            // extended payload length: from 3rd to 10th (8 bytes) byte
            var dest = new byte[8];
            System.arraycopy(data, 3, dest, 0, 8);
            return toLong(dest);
        }

        if (length == 126) {
            // extended payload length: 3rd and 4th bytes
            var dest = new byte[2];
            System.arraycopy(data, 3, dest, 0, 2);
            return toLong(dest);
        }

        return length;
    }

    private static boolean isMasked(byte[] data) {
        byte b1 = data[1];
        // mask: first bit of second byte
        return b1 < 0;
    }

    private static FrameOpCode getOpCode(byte[] data) {
        byte b1 = data[0];

        // op code: last 4 bits of 1st byte
        // x0 0000 - continuation
        // x1 0001 - text frame
        // x2 0011 - binary frame
        // x8 1000 - connection close
        // x9 1001 - ping
        // xA 1010 - pong

        // 15 = 00001111
        var mask = 0b00001111;
        byte opCode = (byte) (b1 & mask);

        switch (opCode) {
            case 0:
                return FrameOpCode.CONTINUE;
            case 1:
                return FrameOpCode.TEXT;
            case 2:
                return FrameOpCode.BINARY;
            case 8:
                return FrameOpCode.CLOSE;
            case 9:
                return FrameOpCode.PING;
            case 10:
                return FrameOpCode.PONG;
            default:
                throw new RuntimeException("Unknown opcode:" + b1);
        }
    }

    public enum FrameOpCode {
        CONTINUE,
        TEXT,
        BINARY,
        CLOSE,
        PING,
        PONG
    }
}
