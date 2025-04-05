import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

public class BreakingBadTranslator {
    private static final String TRANSLATE_API_KEY = "";
    private static final String TRANSLATE_URL = "https://translation.googleapis.com/language/translate/v2?key=" + TRANSLATE_API_KEY;

    public static void main(String[] args) {
        try {
            JSONArray quotes = fetchBreakingBadQuotes(3);

            for (int i = 0; i < quotes.length(); i++) {
                JSONObject quoteObj = quotes.getJSONObject(i);
                String author = quoteObj.getString("author");
                String quote = quoteObj.getString("quote");

                String translatedQuote = translateText(quote, "es");

                System.out.println("Autor: " + author);
                System.out.println("Cita: " + quote);
                System.out.println("Traducción: " + translatedQuote);
                System.out.println();
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static JSONArray fetchBreakingBadQuotes(int count) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.breakingbadquotes.xyz/v1/quotes/" + count))
            .GET()
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return new JSONArray(response.body());
    }

    public static String translateText(String text, String targetLang) throws IOException, InterruptedException {
        String jsonPayload = "{ \"q\": \"" + text + "\", \"target\": \"" + targetLang + "\", \"format\": \"text\" }";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(TRANSLATE_URL))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject jsonResponse = new JSONObject(response.body());
        return jsonResponse.getJSONObject("data")
                           .getJSONArray("translations")
                           .getJSONObject(0)
                           .getString("translatedText");
    }
}
