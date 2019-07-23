package ez.connection.queue.registration;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import ez.connection.client.ClientConnection;
import ez.connection.client.ClientConnectionMessageReader;
import ez.connection.client.ClientsRegistry;
import ez.connection.data.ConnectionMessage;
import ez.messaging.data.transport.Message;
import ez.util.Logger;

public class ClientConnectionRegistrationHandler {

    private final ClientConnectionMessageReader connectionDataReader;

    private final ClientConnectionRegistrationQueue queue;

    private ClientsRegistry connectionsRegister;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private volatile Thread handlerThread;

    private volatile boolean stopped = false;

    public ClientConnectionRegistrationHandler(ClientConnectionRegistrationQueue queue, ClientsRegistry connectionsRegister) {
        this.queue = queue;
        this.connectionsRegister = connectionsRegister;
        this.connectionDataReader = new ClientConnectionMessageReader();
    }

    public void start() {
        handlerThread = new Thread(() -> {
            while (!stopped) {
                ClientConnection connection = queue.peek();
                if (connection != null) {
                    ConnectionMessage message = connectionDataReader.readMessage(connection);
                    if (message != null) {
                        String messageData = new String(message.getData());

                        try {
                            Message helloMessage = objectMapper.readValue(messageData, Message.class);

                            // TODO validate helloMessage

                            connectionsRegister.registerConnection(helloMessage.getSenderId(), connection);

                        } catch (IOException e) {
                            e.printStackTrace();
                            // TODO
                        }
                        queue.pop();
                    }
                }
            }
        });
        handlerThread.start();
    }

    public void stop() throws InterruptedException {
        stopped = true;
        handlerThread.join();

        Logger.log("Client connection registrations handler: stopped");
    }
}
