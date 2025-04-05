private void handleTaskRequest(HttpExchange exchange) throws IOException {
    if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
        exchange.close();
        return;
    }

    Headers headers = exchange.getRequestHeaders();


    System.out.println("Número de headers recibidos: " + headers.size());

    
    for (String key : headers.keySet()) {
        System.out.println("Header: " + key + " -> " + headers.get(key));
    }

    if (headers.containsKey("X-Test") && headers.get("X-Test").get(0).equalsIgnoreCase("true")) {
        String dummyResponse = "123\n";
        sendResponse(dummyResponse.getBytes(), exchange);
        return;
    }

    boolean isDebugMode = false;
    if (headers.containsKey("X-Debug") && headers.get("X-Debug").get(0).equalsIgnoreCase("true")) {
        isDebugMode = true;
    }

    long startTime = System.nanoTime();

    byte[] requestBytes = exchange.getRequestBody().readAllBytes();
    byte[] responseBytes = calculateResponse(requestBytes);

    long finishTime = System.nanoTime();

    System.out.println("Número de bytes en el cuerpo del mensaje: " + requestBytes.length);
    System.out.println("Cuerpo del mensaje: <" + new String(requestBytes) + ">");
    
    int time = finishTime - startTime;
    if (isDebugMode) {
	
        String debugMessage = String.format("La operación tomó %d", TimeUnit.SECONDS.convert(time, TimeUnit.NANOSECONDS));
        exchange.getResponseHeaders().put("X-Debug-Info", Arrays.asList(debugMessage));
    }

    sendResponse(responseBytes, exchange);
}
