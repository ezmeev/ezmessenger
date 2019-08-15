package ez.connection.queue.messages;

import ez.connection.data.ConnectionMessage;
import ez.messaging.handlers.MessageRouter;
import ez.util.Logger;

public class ClientConnectionMessageHandler {

    private final ClientConnectionMessageQueue queue;

    private final MessageRouter handler;

    private volatile Thread handlerThread;

    private volatile boolean stopped = false;

    public ClientConnectionMessageHandler(ClientConnectionMessageQueue queue, MessageRouter handler) {

        this.queue = queue;
        this.handler = handler;
    }

    public void start() {
        handlerThread = new Thread(() -> {
            while (!stopped) {
                try {
                    ConnectionMessage message = queue.dequeue();
                    if (message != null) {
                        try {
                            handler.route(message);
                        } catch (Exception e) {
                            e.printStackTrace();
                            // TODO
                            // queue.enqueue(message); // Retry ?
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    // TODO
                }
            }
        }, "ClientConnectionMessageHandler");
        handlerThread.start();
    }

    public void stop() throws InterruptedException {
        stopped = true;
        queue.stop();
        handlerThread.join();
        Logger.log("Client connection messages handler: stopped");
    }
}
