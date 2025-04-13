// Proyecto 4
// Kevin Aron Tapia Cruz
// 8CM2


 import com.sun.net.httpserver.HttpContext;
 import com.sun.net.httpserver.HttpExchange;
 import com.sun.net.httpserver.HttpServer;
 import java.io.IOException;
 import java.io.OutputStream;
 import java.net.InetSocketAddress;
 import java.net.URI;
 import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
 
 public class WebServer {
     private static final String TASK_ENDPOINT = "/task";
     private static final String STATUS_ENDPOINT = "/status";
     private static final String SEARCH_ENDPOINT = "/search";

     private final int port;

     private HttpServer server;

     private static  SearchWorker worker = new SearchWorker();
 
     public static void main(String[] args) {
         int serverPort = 8080;
         if (args.length == 1) {
             serverPort = Integer.parseInt(args[0]);
         }
    
        
        worker.setBucket(bucket);

        worker.setToken(token);

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

         HttpContext searchContext = server.createContext(SEARCH_ENDPOINT);

         searchContext.setHandler(this::handleSearchRequest);
 
         server.setExecutor(Executors.newFixedThreadPool(8));
         server.start();
     }

     private void handleSearchRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }

        URI requestURI = exchange.getRequestURI();
        Map<String, String> query = queryToMap(requestURI.getRawQuery());

        String keyword = query.getOrDefault("q", "default");

        if (keyword.equalsIgnoreCase("reset")) {
            this.worker.clearHashMap();
            String text = "Reiniciando contador";
            sendResponse(text.getBytes(), exchange);
        } else {
            String result = this.worker.search(keyword);

            sendResponse(result.getBytes(), exchange);
        }
    }
 

    private Map<String, String> queryToMap(String query) {
        Map<String, String> map = new HashMap<>();
            if (query == null) {
                return map;
            }

            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length == 2) {
                    map.put(URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                            URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
                }
            }

            return map;
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
 