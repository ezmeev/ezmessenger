class EZMessenger {

    // ?. senderId & recepientId are same level ids? If I'm a malicious client,
    //    then I can use recipientId as my sednerId unless we pass some kind of
    //    token/password with senderId. What if I will use JWT(senderId+some secret issued by server) instead of senderId?

    // TODO
    // 1. login - as result this client will receive senderId
    // 2. get list of known users - as result this client will get users with their ids
    // 3.

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

