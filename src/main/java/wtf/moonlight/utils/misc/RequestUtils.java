package wtf.moonlight.utils.misc;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestUtils {

    private static final String DEFAULT_AGENT = "Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.7113.93 Safari/537.36 Java/1.8.0_191";

    public static String get(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setConnectTimeout(2000);
        connection.setReadTimeout(10000);

        connection.setRequestMethod("GET");

        connection.setInstanceFollowRedirects(true);
        connection.setDoOutput(true);

        connection.setRequestProperty("User-Agent", DEFAULT_AGENT);

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "GBK"));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();

        return response.toString();
    }

    public static String post(URL url, String data) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setConnectTimeout(2000);
        connection.setReadTimeout(10000);

        connection.setRequestMethod("POST");

        connection.setInstanceFollowRedirects(true);
        connection.setDoOutput(true);

        connection.setRequestProperty("User-Agent", DEFAULT_AGENT);

        DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
        dataOutputStream.writeBytes(data);
        dataOutputStream.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "GBK"));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();

        return response.toString();
    }
}
