package ez.connection.queue.registration;

import java.util.LinkedList;

import ez.connection.client.ClientConnection;

public class ClientConnectionRegistrationQueue {

    private LinkedList<ClientConnection> connectionRegistrationQueue;

    public ClientConnectionRegistrationQueue() {
        connectionRegistrationQueue = new LinkedList<>();
    }

    public synchronized void enqueue(ClientConnection connection) {
        connectionRegistrationQueue.addLast(connection);
        if (connectionRegistrationQueue.size() == 1) {
            notifyAll();
        }
    }

    public synchronized ClientConnection dequeue() throws InterruptedException {
        if (connectionRegistrationQueue.size() == 0) {
            wait();
        }
        return connectionRegistrationQueue.size() == 0 ? null : connectionRegistrationQueue.pop();
    }

    public synchronized void stop() {
        notifyAll();
    }
}
