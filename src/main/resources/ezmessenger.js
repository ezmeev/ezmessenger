console.log('Connecting ...');

class EZMessenger {

    connect() {
        const socket = new WebSocket('ws://localhost:8084/connect');

        socket.onopen = function (event) {
            console.log('[on open]', event);

            socket.send(JSON.stringify({
                type: 'HelloMessage',
                senderId: '1_1'
            }));
        };

        socket.onmessage = function (event) {
            console.log('[on message]', event);
        };

        socket.onerror = function (event) {
            console.log('[on error]', event);
        };
    }
}

const messenger = new EZMessenger();

messenger.connect();

