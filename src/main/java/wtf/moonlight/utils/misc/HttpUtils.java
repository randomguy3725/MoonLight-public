package wtf.moonlight.utils.misc;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import wtf.moonlight.Moonlight;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Map;

public class HttpUtils {

    private static final String DEFAULT_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0";
    private static final String BOUNDARY_PREFIX = "--";
    private static final String LINE_END = "\r\n";

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


    public static JSONObject obtainJson(String url){
        try{
            URL u = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("User-Agent","Mozilla/4.76 (Sk1er-UHCStars V1.0)");
            InputStream is = connection.getInputStream();
            return new JSONObject(IOUtils.toString(is, Charset.defaultCharset()));
        }catch (Exception e){
            e.printStackTrace();
        }
        return new JSONObject().put("succcess",false).put("cause","API_DOWN");
    }

    public static HttpResponse postFormData(String urlStr, Map<String, File> filePathMap, Map<String, Object> keyValues, Map<String, Object> headers) throws IOException {
        HttpResponse response;
        HttpURLConnection conn = getHttpURLConnection(urlStr, headers);
        String boundary = "------------9sflsjbdgvsuhgbjvskjlvj13h5g1jh34v513hb" + System.currentTimeMillis() + "515ijk9a85e18429711ee9e70f040429711eebe560242ac120002be5602------------";
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        //发送参数数据
        try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
            //发送普通参数
            if (keyValues != null && !keyValues.isEmpty()) {
                for (Map.Entry<String, Object> entry : keyValues.entrySet()) {
                    writeSimpleFormField(boundary, out, entry);
                }
            }
            //发送文件类型参数
            if (filePathMap != null && !filePathMap.isEmpty()) {
                for (Map.Entry<String, File> filePath : filePathMap.entrySet()) {
                    writeFile(filePath.getKey(), filePath.getValue(), boundary, out);
                }
            }

            //写结尾的分隔符--${boundary}--,然后回车换行
            String endStr = BOUNDARY_PREFIX + boundary + BOUNDARY_PREFIX + LINE_END;
            out.write(endStr.getBytes());
        } catch (Exception e) {
            Moonlight.LOGGER.error("HttpUtils.postFormData 请求异常！", e);
            response = new HttpResponse(500, e.getMessage());
            return response;
        }

        return getHttpResponse(conn);
    }

    private static HttpURLConnection getHttpURLConnection(String urlStr, Map<String, Object> headers) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(50000);
        conn.setReadTimeout(50000);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Charset", "UTF-8");
        conn.setRequestProperty("connection", "keep-alive");

        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, Object> header : headers.entrySet()) {
                conn.setRequestProperty(header.getKey(), header.getValue().toString());
            }
        }

        return conn;
    }

    private static HttpResponse getHttpResponse(HttpURLConnection conn) {
        HttpResponse response;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            int responseCode = conn.getResponseCode();
            StringBuilder responseContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseContent.append(line);
            }
            response = new HttpResponse(responseCode, responseContent.toString());
        } catch (Exception e) {
            Moonlight.LOGGER.error("获取 HTTP 响应异常！", e);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                StringBuilder responseContent = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                response = new HttpResponse(500, responseContent.toString());
            } catch (IOException ex) {
                response = new HttpResponse(500, e.getMessage());
            }
        }
        return response;
    }

    private static void writeFile(String paramName, File filePath, String boundary,
                                  DataOutputStream out) {
        try (InputStream fileReader = Files.newInputStream(filePath.toPath())) {
            String boundaryStr = BOUNDARY_PREFIX + boundary + LINE_END;
            out.write(boundaryStr.getBytes());
            String fileName = filePath.getName();
            String contentDispositionStr = String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"", paramName, fileName) + LINE_END;
            out.write(contentDispositionStr.getBytes());
            String contentType = "Content-Type: application/octet-stream" + LINE_END + LINE_END;
            out.write(contentType.getBytes());

            int bt = fileReader.read();
            while (bt != -1){
                out.write(bt);
                bt = fileReader.read();
            }

            out.write(LINE_END.getBytes());
        } catch (Exception e) {
            Moonlight.LOGGER.error("写文件类型的表单参数异常", e);
        }
    }

    private static void writeSimpleFormField(String boundary, DataOutputStream out, Map.Entry<String, Object> entry) throws IOException {
        String boundaryStr = BOUNDARY_PREFIX + boundary + LINE_END;
        out.write(boundaryStr.getBytes());
        String contentDispositionStr = String.format("Content-Disposition: form-data; name=\"%s\"", entry.getKey()) + LINE_END + LINE_END;
        out.write(contentDispositionStr.getBytes());
        String valueStr = entry.getValue().toString() + LINE_END;
        out.write(valueStr.getBytes());
    }
}
