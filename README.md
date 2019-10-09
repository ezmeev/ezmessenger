<h1 align='center'> EZ Messenger</h1>
EaZy messenger which is build because of ... just for fun actually. Everyone should try to implement a chat, right? Here is my attempt :)

### Idea
My main idea was to implement non-blocking server with minimal number of treads required to handle clients and also try to avoid "busy" waits as they burn CPU time.

Communication protocol loosely defined in `src/main/resources/protocol-definition.md`

#### Disclaimer
I'm certainly aware of various frameworks which can be used for in order to implement what I want (for example I can use Vertx or just Netty to have non-blocking server or Spring[Boot] for Websocket & dependency injection), but my intent was to do all these things myself for learning and practicing purposes.  
