import java.net.*;
import java.util.*;
import java.util.regex.*;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class SearchWorker {

    private String bucket;
    private String token;

    private Map<String, Map<String, Integer>> fileOccurrences = new HashMap<>();


    public SearchWorker() {

    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String search(String keyword) throws IOException {
        List<String> files = listFiles(bucket);
        StringBuilder result = new StringBuilder();

        for (String file : files) {
            Map<String, Integer> wordOccurrences = fileOccurrences.computeIfAbsent(file, k -> new HashMap<>());

            int ocurrencia = wordOccurrences.getOrDefault(keyword.toLowerCase(), 0) + 1;

            byte[] pdfBytes = downloadPdf(bucket, file);
            
            String matchedSentence = findNthSentenceWithWord(pdfBytes, keyword, ocurrencia);

            String translated = "";

            if (matchedSentence != null) {
                try {
                    translated = Translator.translateText(matchedSentence, "ES");
                } catch (Exception e) {
                    translated = "Error al traducir: " + e.getMessage();
                }

                wordOccurrences.put(keyword.toLowerCase(), ocurrencia);

                result.append("Libro: ").append(file).append("\n")
                      .append("Ocurrencia #").append(ocurrencia).append(" de '").append(keyword).append("':\n")
                      .append("\n")
                      .append(highlightWord(matchedSentence, keyword)).append("\n")
                      .append("\n")
                      .append(translated).append("\n")
                      .append("─────────────────────────────────────────────\n");
            }
        }

        if (result.length() == 0) {
            result.append("No se encontro la palabra '").append(keyword).append("' en ningun texto.\n");
        }

        return result.toString();
    }

    private List<String> listFiles(String bucketName) throws IOException {
        String urlStr = "https://storage.googleapis.com/storage/v1/b/" + bucketName + "/o";

        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        
        conn.setRequestProperty("Authorization", "Bearer " + token);
        
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        
        StringBuilder response = new StringBuilder();
        
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();

        List<String> pdfs = new ArrayList<>();

        
        Matcher matcher = Pattern.compile("\"name\":\\s*\"(.*?)\"").matcher(response.toString());
        
        while (matcher.find()) {
            String name = matcher.group(1);
            
            if (name.endsWith(".pdf")) {
                pdfs.add(name);
            }
        }

        return pdfs;
    }

    private byte[] downloadPdf(String bucketName, String objectName) throws IOException {
        String encoded = URLEncoder.encode(objectName, StandardCharsets.UTF_8.toString());
        String urlStr = "https://storage.googleapis.com/storage/v1/b/" + bucketName + "/o/" + encoded + "?alt=media";


        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + token);
        conn.setRequestMethod("GET");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = conn.getInputStream();
        byte[] buffer = new byte[4096];
        int n;

        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }

        return out.toByteArray();
    }

    public void clearHashMap() {
        fileOccurrences.clear();
    }

    private String findNthSentenceWithWord(byte[] pdfBytes, String keyword, int n) throws IOException {
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
            PDFTextStripper stripper = new PDFTextStripper();

            int found = 0;

            for (int i = 1; i <= document.getNumberOfPages(); i++) {
                stripper.setStartPage(i);
                stripper.setEndPage(i);
                String pageText = stripper.getText(document);

                String[] sentences = pageText.split("(?<=[.!?])\\s+");

                for (String sentence : sentences) {
                    if (sentence.toLowerCase().contains(keyword.toLowerCase())) {
                        found++;
                        if (found == n) {
                            return sentence.trim();
                        }
                    }
                }
            }
        }
        return null;
    }

    private String highlightWord(String text, String keyword) {
        return text.replaceAll("(?i)(" + Pattern.quote(keyword) + ")", "\u001B[31m$1\u001B[0m");
    }
}
