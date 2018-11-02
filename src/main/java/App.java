// Spark test code
//YOU CAN DELETE ALL OF THIS
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;


/**
 * This is the main entry point for the application, and is responsible for starting the Jetty server
 * and attaching the WebSocket handler that manages communication with the web clients.
 */
public class App {
    public static void main(String[] args){
        Server server = new Server(8090);
        WebSocketHandler wsHandler = new WebSocketHandler() {
            @Override
            public void configure(WebSocketServletFactory factory) {
                factory.register(MyWebSocketHandler.class);
            }
        };
        server.setHandler(wsHandler);

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            //TODO: better error handling when Jetty startup fails
            System.out.println(e.getMessage());
        }
    }
}

