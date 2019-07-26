package ez.connection.queue;

import ez.connection.queue.messages.ClientConnectionMessageQueue;
import ez.connection.queue.registration.ClientConnectionRegistrationQueue;

public class QueueServer {

    private ClientConnectionMessageQueue messagesQueue;

    private ClientConnectionRegistrationQueue registrationsQueue;

    public QueueServer() {
        // TODO http://tutorials.jenkov.com/java-concurrency/blocking-queues.html

        this.messagesQueue = new ClientConnectionMessageQueue();
        this.registrationsQueue = new ClientConnectionRegistrationQueue();
    }

    public ClientConnectionMessageQueue getMessagesQueue() {
        return messagesQueue;
    }

    public ClientConnectionRegistrationQueue getRegistrationsQueue() {
        return registrationsQueue;
    }
}
