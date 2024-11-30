package wtf.moonlight.utils.misc;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestUtils {

    private static final String DEFAULT_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0";

    static {
        HttpURLConnection.setFollowRedirects(true);
    }

    private static HttpURLConnection make(String url, String method, String agent) throws IOException {
        HttpURLConnection httpConnection = (HttpURLConnection) new URL(url).openConnection();

        httpConnection.setRequestMethod(method);
        httpConnection.setConnectTimeout(1000);
        httpConnection.setReadTimeout(1000);

        httpConnection.setRequestProperty("User-Agent", agent);

        httpConnection.setInstanceFollowRedirects(true);
        httpConnection.setDoOutput(true);

        return httpConnection;
    }

    private static HttpURLConnection make(String url, String method) throws IOException {
        return make(url,method,DEFAULT_AGENT);
    }

    public static String request(String url, String method, String agent) throws IOException {
        HttpURLConnection connection = make(url, method, agent);

        try (InputStream inputStream = connection.getInputStream()) {
            return new String(inputStream.readAllBytes());
        }
    }

    public static InputStream requestStream(String url, String method, String agent) throws IOException {
        HttpURLConnection connection = make(url, method, agent);

        return connection.getInputStream();
    }

    public static String get(String url) throws IOException {
        return request(url, "GET", DEFAULT_AGENT);
    }

    public static void download(String url, File file) throws IOException {
        FileUtils.copyInputStreamToFile(make(url, "GET").getInputStream(), file);
    }
}
