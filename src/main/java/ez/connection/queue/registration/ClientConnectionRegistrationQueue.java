package ez.connection.queue.registration;

import java.util.concurrent.ConcurrentLinkedDeque;

import ez.connection.client.ClientConnection;

public class ClientConnectionRegistrationQueue {

    private ConcurrentLinkedDeque<ClientConnection> connectionRegistrationQueue;

    public ClientConnectionRegistrationQueue() {
        connectionRegistrationQueue = new ConcurrentLinkedDeque<>();
    }

    public void add(ClientConnection connection) {
        connectionRegistrationQueue.addLast(connection);
    }

    public ClientConnection peek() {
        return connectionRegistrationQueue.isEmpty() ? null : connectionRegistrationQueue.peekFirst();
    }

    public ClientConnection pop() {
        return connectionRegistrationQueue.pop();
    }
}
