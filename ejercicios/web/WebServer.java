import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class WebServer {
    private static final String CPU_ENDPOINT = "/cpu";
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

        HttpContext cpuContext = server.createContext(CPU_ENDPOINT);
        cpuContext.setHandler(this::handleCpuRequest);

        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();
    }

    private void handleCpuRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        InputStream input = exchange.getRequestBody();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int read;
        byte[] data = new byte[1024];

        while ((read = input.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, read);
        }

        String bodyString = new String(buffer.toByteArray()).trim();

        try {
            String[] params = bodyString.split(",");
            int porcentaje = Integer.parseInt(params[0].trim());
            int segundos = Integer.parseInt(params[1].trim());
            int hilos = Integer.parseInt(params[2].trim());

            SimulaCargaCpu.ejecutarSimulacion(porcentaje, segundos, hilos);

            String response = String.format("Simulación iniciada: %d%% por %d segundos en %d hilos\n",
                                            porcentaje, segundos, hilos);
            sendResponse(response.getBytes(), exchange);

        } catch (Exception e) {
            String errorMsg = "Error: formato esperado 'porcentaje,segundos,hilos'\n";
            sendResponse(errorMsg.getBytes(), exchange);
        }
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
