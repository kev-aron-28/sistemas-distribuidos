import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServer {
    private static final String TASK_ENDPOINT = "/task";
    private static final String STATUS_ENDPOINT = "/status";
    private static final String CPU_ENDPOINT = "/cpu";
    private static final String SEARCH_TOKEN_ENDPOINT = "/searchtoken";
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
        HttpContext taskContext = server.createContext(TASK_ENDPOINT);
        HttpContext searchTokenContext = server.createContext(SEARCH_TOKEN_ENDPOINT);
        HttpContext cpuContext = server.createContext(CPU_ENDPOINT);
        statusContext.setHandler(this::handleStatusCheckRequest);
        taskContext.setHandler(this::handleTaskRequest);
        searchTokenContext.setHandler(this::handleSearchTokenRequest);
        cpuContext.setHandler(this::handleCPUConsume);
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();
    }

    private void handleTaskRequest(HttpExchange exchange) throws IOException {
    if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
        exchange.close();
        return;
    }

    try {
        byte[] requestBytes = exchange.getRequestBody().readAllBytes();
        Demo receivedDemo = (Demo) SerializationUtils.deserialize(requestBytes);
        System.out.println("[SERVER] Received: " + receivedDemo);

        Demo responseDemo = new Demo(receivedDemo.a, Instant.now().toString());
        byte[] responseBytes = SerializationUtils.serialize(responseDemo);

        exchange.getResponseHeaders().set("Content-Type", "application/octet-stream");
        exchange.sendResponseHeaders(200, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    } catch (Exception e) {
        e.printStackTrace();
        String errorMsg = "Error processing request: " + e.getMessage();
        exchange.sendResponseHeaders(500, errorMsg.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(errorMsg.getBytes());
        }
    }
}

    private byte[] calculateResponse(byte[] requestBytes) {
        String bodyString = new String(requestBytes);
        String[] stringNumbers = bodyString.split(",");

        BigInteger result = BigInteger.ONE;

        for (String number : stringNumbers) {
            BigInteger bigInteger = new BigInteger(number);
            result = result.multiply(bigInteger);
        }
        return String.format("El resultado de la multiplicación es %s\n", result).getBytes();
    }

    private void handleStatusCheckRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }

        String responseMessage = "El servidor está vivo\n";
        sendResponse(responseMessage.getBytes(), exchange);
    }

    private void handleCPUConsume(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
            exchange.close();
            return;
        }

        byte[] requestBytes = exchange.getRequestBody().readAllBytes();
        String requestString = new String(requestBytes);
        String requestSplitted[] = requestString.split(",");
        if (requestSplitted.length != 3) {
            sendResponse("Formato incorrecto, uso: <porcentaje>, <tiempo-segundos>, <numero-nucleos>".getBytes(), 400,
                    exchange);
            return;
        }

        int args[] = new int[3];
        try {
            for (int i = 0; i < requestSplitted.length; i++) {
                args[i] = Integer.parseInt(requestSplitted[i]);
            }
            if (args[0] > 100 || args[0] < 0){
                sendResponse("El consumo de uso es un porcentaje...".getBytes(), 400, exchange);
                return;
            }
        } catch (NumberFormatException e) {
            sendResponse("Formato incorrecto, ingresar números.".getBytes(), 400, exchange);
            return;
        }
        long startTime = System.nanoTime();
        handleWork(args[0], args[1], args[2]);
        long finishTime = System.nanoTime();

        long duration = finishTime - startTime;
        long seconds = duration / 1_000_000_000;
        long milliseconds = (duration % 1_000_000_000) / 1_000_000;
        String durationMessage = String.format("La operación tomó %d nanosegundos = %d segundos con %d milisegundos",
                duration, seconds, milliseconds);

        sendResponse(durationMessage.getBytes(), exchange);
    }

    Random r;

    private void handleWork(int porcentaje, int timeSeconds, int no_nucleos) {
        if (r == null)
            r = new Random();

        int cores_available = Runtime.getRuntime().availableProcessors();
        if (no_nucleos > cores_available) {
            System.out.printf("Solo hay %d nucleos disponibles, por lo que será ignorado la cantidad de %d y se usara lo disponible!\n", cores_available, no_nucleos);
            no_nucleos = cores_available;
        }

        System.out.println("Se va a simular un consumo de CPU del " + porcentaje + "% durante " + timeSeconds + " segundos en " + no_nucleos + " de los " + cores_available + " disponibles.");
        ExecutorService executor = Executors.newFixedThreadPool(no_nucleos);

        for (int i = 0; i < no_nucleos; i++)
            executor.execute(() -> {
                long totalDurationMs = timeSeconds * 1000;
                long start = System.currentTimeMillis();

                long busyTime = porcentaje;
                long idleTime = 100 - porcentaje;

                while (System.currentTimeMillis() - start < totalDurationMs) {
                    long startBusy = System.nanoTime();
                    while ((System.nanoTime() - startBusy) < busyTime * 1_000_000) {
                        Math.sqrt(r.nextInt(Integer.MAX_VALUE));
                    }

                    try {
                        Thread.sleep(idleTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        executor.shutdown();

        try {
            boolean finished = executor.awaitTermination(timeSeconds + 2, java.util.concurrent.TimeUnit.SECONDS);
            if (!finished) {
                System.err.println("Advertencia: tareas no finalizaron dentro del tiempo esperado.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
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

    private void sendResponse(byte[] responseBytes, int statusCode, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(responseBytes);
        outputStream.flush();
        outputStream.close();
        exchange.close();
    }

    private void handleSearchTokenRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
            exchange.close();
            return;
        }
        Headers headers = exchange.getRequestHeaders();
        boolean isDebugMode = headers.containsKey("X-Debug") && headers.get("X-Debug").get(0).equalsIgnoreCase("true");
        long startTime = System.nanoTime();
        byte[] requestBytes = exchange.getRequestBody().readAllBytes();
        String requestBody = new String(requestBytes);
        String[] params = requestBody.split(",");
        if (params.length != 2) {
            sendResponse("Formato incorrecto. Debe ser: longitudCadena,subcadena".getBytes(), exchange);
            return;
        }
        try {
            int chainLength = Integer.parseInt(params[0]);
            String subcadena = params[1];

            if (subcadena.length() != 3) {
                sendResponse("La subcadena debe tener 3 caracteres.".getBytes(), exchange);
                return;
            }
            String cadenota = generarCadenaAleatoria(chainLength);
            int count = contarOcurrencias(cadenota, subcadena);
            String responseMessage = String.format("Se encontró la subcadena '%s' %d veces en la cadenota.\n",
                    subcadena, count);
            long finishTime = System.nanoTime();
            if (isDebugMode) {
                long duration = finishTime - startTime;
                long seconds = duration / 1_000_000_000;
                long milliseconds = (duration % 1_000_000_000) / 1_000_000;
                String debugMessage = String.format(
                        "La operación tomó %d nanosegundos = %d segundos con %d milisegundos",
                        duration, seconds, milliseconds);
                exchange.getResponseHeaders().put("X-Debug-Info", Arrays.asList(debugMessage));
            }
            sendResponse(responseMessage.getBytes(), exchange);
        } catch (NumberFormatException e) {
            sendResponse("Formato numérico incorrecto para la longitud de la cadena.".getBytes(), exchange);
        }
    }

    private String generarCadenaAleatoria(int longitud) {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(longitud);

        for (int i = 0; i < longitud; i++) {
            sb.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }

        return sb.toString();
    }

    private int contarOcurrencias(String cadenota, String subcadena) {
        int count = 0;
        int index = 0;
        while ((index = cadenota.indexOf(subcadena, index)) != -1) {
            count++;
            index += subcadena.length();
        }
        return count;
    }
}
