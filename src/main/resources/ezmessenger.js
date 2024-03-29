class EZMessenger {

    connect() {
        EZMessenger.logStatus('Connecting ...');

        const socket = new WebSocket('ws://localhost:8084/connect');

        socket.onopen = function (event) {
            EZMessenger.logStatus('Connected, sending HelloMessage ...');

            console.log('[on open]', event);

            socket.send(JSON.stringify({
                type: 'HelloMessage',
                senderId: '1_1'
            }));

            EZMessenger.logStatus('HelloMessage sent, waiting for ack ...');
        };

        socket.onmessage = function (event) {
            EZMessenger.logStatus('Acknowledged!');

            console.log('[on message]', event);
        };

        socket.onerror = function (event) {
            EZMessenger.logStatus('Error: ' + event);
            console.log('[on error]', event);
        };
    }

    static logStatus(status) {
        const newStatus = document.createElement('div');
        newStatus.innerText = status;
        document.getElementById('statusLog').appendChild(newStatus);
    }
}

const messenger = new EZMessenger();

messenger.connect();

