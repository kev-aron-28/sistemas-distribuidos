import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Translator {
    private static final String TRANSLATE_API_KEY = "";
    private static final String TRANSLATE_URL = "https://translation.googleapis.com/language/translate/v2?key=" + TRANSLATE_API_KEY;

    public static String translateText(String text, String targetLang) throws IOException, InterruptedException {
        String jsonPayload = String.format(
            "{ \"q\": \"%s\", \"target\": \"%s\", \"format\": \"text\" }",
            escapeJson(text), targetLang
        );

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(TRANSLATE_URL))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();

        Pattern pattern = Pattern.compile("\"translatedText\"\\s*:\\s*\"(.*?)\"");
        Matcher matcher = pattern.matcher(body);

        if (matcher.find()) {
            return matcher.group(1).replace("\\n", "\n").replace("\\\"", "\"");
        } else {
            return "[ERROR] No se pudo traducir";
        }
    }

    private static String escapeJson(String s) {
        return s.replace("\"", "\\\"").replace("\n", "\\n");
    }
}
