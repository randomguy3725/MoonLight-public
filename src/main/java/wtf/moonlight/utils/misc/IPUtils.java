package wtf.moonlight.utils.misc;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class IPUtils {

    private static String getLocationByApi() throws IOException {
        String apiUrl = "https://whois.pconline.com.cn/ipJson.jsp?json=true";

        URL url = new URL(apiUrl);
        String response = RequestUtils.get(url);

        JSONObject jsonResponse = new JSONObject(response);

        // 提取省份信息
        return jsonResponse.getString("pro");

    }

    public static String getLocation() {

        String province;
        try {
            province = getLocationByApi();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return province + "人";
    }
}
