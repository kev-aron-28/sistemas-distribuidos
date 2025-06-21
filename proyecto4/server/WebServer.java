// Proyecto 4
// Kevin Aron Tapia Cruz
// 8CM2

 import com.sun.net.httpserver.HttpContext;
 import com.sun.net.httpserver.HttpExchange;
 import com.sun.net.httpserver.HttpServer;
 import java.io.IOException;
 import java.io.OutputStream;
 import java.net.InetSocketAddress;
 import java.util.concurrent.Executors;
 
 public class WebServer {
     private static final String STATUS_ENDPOINT = "/status";

     private final int port;

     private HttpServer server;

 
     public static void main(String[] args) {
         int serverPort = 8080;
         if (args.length == 1) {
             serverPort = Integer.parseInt(args[0]);
         }
    
         WebServer webServer = new WebServer(serverPort);
         webServer.startServer();
 
         System.out.println("Servidor escuchando en el puerto " + serverPort);
     }
 
     public WebServer(int port) {
         this.port = port;
     }
 
     public void startServer() {
         try {
             this.server = HttpServer.create(new InetSocketAddress(port), 0);
         } catch (IOException e) {
             e.printStackTrace();
             return;
         }
 
         HttpContext statusContext = server.createContext(STATUS_ENDPOINT);
 
         statusContext.setHandler(this::handleStatusCheckRequest);

         server.setExecutor(Executors.newFixedThreadPool(8));
         server.start();
     }

 
     private void handleStatusCheckRequest(HttpExchange exchange) throws IOException {
         if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
             exchange.close();
             return;
         }
 
         String responseMessage = "El servidor está vivo\n";
         sendResponse(responseMessage.getBytes(), exchange);
     }
 
     private void sendResponse(byte[] responseBytes, HttpExchange exchange) throws IOException {
         exchange.sendResponseHeaders(200, responseBytes.length);
         OutputStream outputStream = exchange.getResponseBody();
         outputStream.write(responseBytes);
         outputStream.flush();
         outputStream.close();
         exchange.close();
     }
 }
 