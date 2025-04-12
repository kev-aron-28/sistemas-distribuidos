import java.net.*;
import java.util.*;
import java.util.regex.*;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class SearchWorker {

    private final String bucket;
    private final String token;

    public SearchWorker(String bucket, String token) {
        this.bucket = bucket;
        this.token = token;
    }

    public String search(String keyword) throws IOException {
        List<String> files = listFiles(bucket);
        StringBuilder result = new StringBuilder();

        for (String file : files) {
            System.out.println(file);
            byte[] pdfBytes = downloadPdf(bucket, file);
            String text = extractTextFromPdf(pdfBytes);

            // if (text.toLowerCase().contains(keyword.toLowerCase())) {
            //     result.append("✅ ").append(file).append(" contiene la palabra '").append(keyword).append("'\n\n");
            // }
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

    private String extractTextFromPdf(byte[] pdfBytes) throws IOException {
        try (PDDocument doc = PDDocument.load(pdfBytes)) {
            return new PDFTextStripper().getText(doc);
        }
    }
}
