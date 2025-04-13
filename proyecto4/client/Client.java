import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Client {

    private static final String SERVER_URL = "http://localhost:8080/search?q=";

    public static void main(String[] args) {
        System.out.println("Busqueda de palabras en PDF");
        System.out.println("PALABRA EN INGLES (o 'salir' para terminar):");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.print("\n🔤 Palabra: ");
                String palabra = reader.readLine();

                if (palabra == null || palabra.equalsIgnoreCase("salir")) {
                    System.out.println("EXIT.");
                    break;
                }

                String resultado = buscarPalabra(palabra);
                System.out.println("\n" + resultado);
            }
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }

    private static String buscarPalabra(String palabra) {
        try {
            String encoded = URLEncoder.encode(palabra, StandardCharsets.UTF_8);
            URL url = new URL(SERVER_URL + encoded);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();

            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }

            in.close();
            return response.toString();
        } catch (IOException e) {
            return "COUDNT CONNECT " + e.getMessage();
        }
    }
}
