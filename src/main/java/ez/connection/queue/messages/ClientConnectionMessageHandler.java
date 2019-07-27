package ez.connection.queue.messages;

import ez.connection.client.ClientConnection;
import ez.connection.client.ClientConnectionMessageReader;
import ez.connection.data.ConnectionMessage;
import ez.messaging.handlers.MessageRouter;
import ez.util.Logger;

public class ClientConnectionMessageHandler {

    private final ClientConnectionMessageReader connectionDataReader;

    private final ClientConnectionMessageQueue queue;

    private final MessageRouter handler;

    private volatile Thread handlerThread;

    private volatile boolean stopped = false;

    public ClientConnectionMessageHandler(ClientConnectionMessageQueue queue, MessageRouter handler) {

        this.queue = queue;
        this.handler = handler;
        this.connectionDataReader = new ClientConnectionMessageReader();
    }

    public void start() {
        handlerThread = new Thread(() -> {
            while (!stopped) {
                try {
                    ClientConnection connection = queue.dequeue();
                    if (connection != null) {
                        ConnectionMessage message = connectionDataReader.readMessage(connection);
                        if (message != null) {
                            try {
                                handler.route(message);
                                connection.markUnqueued();
                            } catch (Exception e) {
                                e.printStackTrace();
                                // TODO
                            }
                        }else {
                            queue.enqueue(connection);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    // TODO
                }
            }
        });
        handlerThread.start();
    }

    public void stop() throws InterruptedException {
        stopped = true;
        queue.stop();
        handlerThread.join();
        Logger.log("Client connection messages handler: stopped");
    }
}
