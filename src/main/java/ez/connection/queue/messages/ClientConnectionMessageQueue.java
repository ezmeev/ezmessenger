package ez.connection.queue.messages;

import java.util.concurrent.ConcurrentLinkedDeque;

import ez.connection.client.ClientConnection;

public class ClientConnectionMessageQueue {

    private ConcurrentLinkedDeque<ClientConnection> connectionMessagesQueue;

    public ClientConnectionMessageQueue() {
        connectionMessagesQueue = new ConcurrentLinkedDeque<>();
    }

    public void add(ClientConnection connection) {
        connectionMessagesQueue.addLast(connection);
    }

    public ClientConnection peek() {
        return connectionMessagesQueue.isEmpty() ? null : connectionMessagesQueue.peekFirst();
    }

    public ClientConnection pop() {
        return connectionMessagesQueue.pop();
    }

    public int size() {
        return connectionMessagesQueue.size();
    }
}
