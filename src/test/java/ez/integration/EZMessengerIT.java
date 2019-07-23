package ez.integration;

import ez.EZMessenger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class EZMessengerIT {

    private EZMessenger messenger;

    @Before
    public void setUp() {
        messenger = EZMessenger.Configurator.configureDefault();
        messenger.start();
    }

    @After
    public void tearDown() {
        messenger.stop();
    }

    @Test
    public void shouldStopThenStartAndStop() {
        // verifying that sockets and other resources released without problems
        messenger.stop();
        messenger.start();
        messenger.stop();
    }
}
