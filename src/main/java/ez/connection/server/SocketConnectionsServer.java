package ez.connection.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import ez.connection.client.socket.SocketConnection;
import ez.connection.queue.registration.ClientConnectionRegistrationQueue;
import ez.util.Logger;

public class SocketConnectionsServer {

    private ServerSocketChannel socketChannel;

    private Selector serverSocketSelector;

    private ClientConnectionRegistrationQueue registrationQueue;

    private volatile boolean stopped = false;

    public SocketConnectionsServer(ClientConnectionRegistrationQueue registrationQueue) {
        this.registrationQueue = registrationQueue;
    }

    public void start() {

        try {
            serverSocketSelector = Selector.open();

            socketChannel = ServerSocketChannel.open();
            socketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            socketChannel.bind(new InetSocketAddress(8083));
            socketChannel.configureBlocking(false);
            socketChannel.register(serverSocketSelector, SelectionKey.OP_ACCEPT);

            var acceptConnectionsThread = new Thread(() -> {
                while (!stopped) {
                    try {

                        serverSocketSelector.select();

                        if (!serverSocketSelector.isOpen()) {
                            return;
                        }

                        Set<SelectionKey> selectedKeys = serverSocketSelector.selectedKeys();

                        Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                        while (keyIterator.hasNext()) {

                            SelectionKey key = keyIterator.next();

                            if (key.isAcceptable()) {
                                SocketChannel clientChannel = socketChannel.accept();

                                registrationQueue.enqueue(new SocketConnection(clientChannel));
                            }

                            keyIterator.remove();
                        }
                    } catch (SocketException e) {
                        // kinda expected, seems like there no other good way to "interrupt" accept()
                    } catch (IOException e) {
                        e.printStackTrace();
                        // TODO
                    }
                }
            }, "SocketConnectionsServer");
            acceptConnectionsThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        stopped = true;

        if (socketChannel != null) {
            try {
                this.serverSocketSelector.close();
                this.socketChannel.close();
                this.socketChannel.socket().close();
            } catch (IOException e) {
                e.printStackTrace();
                // TODO
            }
        }

        Logger.log("Client connections server: stopped");
    }
}
