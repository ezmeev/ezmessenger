package ez.connection.queue.messages;

import java.util.LinkedList;

import ez.connection.data.ConnectionMessage;

public class ClientConnectionMessageQueue {

    private LinkedList<ConnectionMessage> connectionMessagesQueue;

    public ClientConnectionMessageQueue() {
        connectionMessagesQueue = new LinkedList<>();
    }

    public synchronized void enqueue(ConnectionMessage connection) {
        connectionMessagesQueue.addLast(connection);

        if (connectionMessagesQueue.size() == 1) {
            notifyAll();
        }
    }

    public synchronized ConnectionMessage dequeue() throws InterruptedException {
        if (connectionMessagesQueue.size() == 0) {
            wait();
        }
        return connectionMessagesQueue.size() == 0 ? null : connectionMessagesQueue.pop();
    }

    public synchronized void stop() {
        notifyAll();
    }
}
