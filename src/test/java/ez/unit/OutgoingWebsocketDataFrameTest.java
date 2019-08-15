package ez.unit;

import ez.connection.client.websocket.OutgoingWebsocketDataFrame;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class OutgoingWebsocketDataFrameTest {

    @Test
    public void websocketDataFrameTest() {

        var data = new byte[8];
        data[0] = (byte) -127;
        data[1] = (byte) 6;
        data[2] = (byte) 72;
        data[3] = (byte) 101;
        data[4] = (byte) 108;
        data[5] = (byte) 108;
        data[6] = (byte) 111;
        data[7] = (byte) 33;

        var frame = new OutgoingWebsocketDataFrame("Hello!");

        Assert.assertArrayEquals(data, frame.frameData);
    }
}
