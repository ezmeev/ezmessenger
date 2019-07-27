package ez.connection.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import ez.connection.client.ClientConnection;
import ez.connection.queue.registration.ClientConnectionRegistrationQueue;
import ez.util.Logger;

public class ClientConnectionsServer {

    private ServerSocket socket;

    private ClientConnectionRegistrationQueue registrationQueue;

    private Thread acceptConnectionsThread;

//    private Thread cleanupConnectionsThread;

    private volatile boolean stopped = false;

    public ClientConnectionsServer(ClientConnectionRegistrationQueue registrationQueue) throws IOException {
        this.socket = new ServerSocket(8083);
        this.registrationQueue = registrationQueue;
    }

    public void start() {

        acceptConnectionsThread = new Thread(() -> {
            while (!stopped) {
                try {
                    Socket incomingConnection = socket.accept();
                    ClientConnection clientConnection = new ClientConnection(incomingConnection);

                    registrationQueue.enqueue(clientConnection);

                } catch (SocketException e) {
                    // kinda expected, seems like there no other good way to "interrupt" accept()
                } catch (IOException e) {
                    e.printStackTrace();
                    // TODO
                }
            }
        });
        acceptConnectionsThread.start();

        // TODO move to separate class
//        cleanupConnectionsThread = new Thread(() -> {
//            while (!stopped) {
//                try {
//                    Logger.log("Initiating cleanup ...");
//
//                    var connectionsToCleanup = connectionsRegister.getSnapshot().stream()
//                        .filter(Predicate.not(ClientConnection::isAlive))
//                        .collect(Collectors.toList());
//
//                    for (var clientConnection : connectionsToCleanup) {
//                        connectionsRegister.unregisterConnection(clientConnection);
//                    }
//
//                    Logger.log("Cleanup complete");
//
//                    Thread.sleep(10000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                    // TODO
//                }
//            }
//        });
//        cleanupConnectionsThread.start();
    }

    public void stop() {
        stopped = true;

        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO
        }

        Logger.log("Client connections server: stopped");
    }
}
