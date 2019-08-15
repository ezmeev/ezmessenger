package ez.unit;

import ez.connection.client.websocket.IncomingWebsocketDataFrame;
import ez.connection.client.websocket.IncomingWebsocketDataFrame.FrameOpCode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(BlockJUnit4ClassRunner.class)
public class IncomingWebsocketDataFrameTest {

    @Test
    public void websocketDataFrameTest() {

        var data = new byte[12];
        data[0] = (byte) -127;
        data[1] = (byte) -122;
        data[2] = (byte) -127;
        data[3] = (byte) -118;
        data[4] = (byte) 108;
        data[5] = (byte) -106;
        data[6] = (byte) -55;
        data[7] = (byte) -17;
        data[8] = (byte) 0;
        data[9] = (byte) -6;
        data[10] = (byte) -18;
        data[11] = (byte) -85;

        var dataFrame = new IncomingWebsocketDataFrame(data);
        assertEquals(FrameOpCode.TEXT, dataFrame.opCode);
        assertEquals("Hello!", new String(dataFrame.payload));
    }
}
