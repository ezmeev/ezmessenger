package ez.connection.queue.messages;

import java.util.LinkedList;

import ez.connection.client.ClientConnection;

public class ClientConnectionMessageQueue {

    private LinkedList<ClientConnection> connectionMessagesQueue;

    public ClientConnectionMessageQueue() {
        connectionMessagesQueue = new LinkedList<>();
    }

    public synchronized void enqueue(ClientConnection connection) {
        connectionMessagesQueue.addLast(connection);

        if (connectionMessagesQueue.size() == 1) {
            notifyAll();
        }
    }

    public synchronized ClientConnection dequeue() throws InterruptedException {
        if (connectionMessagesQueue.size() == 0) {
            wait();
        }
        return connectionMessagesQueue.size() == 0 ? null : connectionMessagesQueue.pop();
    }

    public synchronized void stop() {
        notifyAll();
    }
}
