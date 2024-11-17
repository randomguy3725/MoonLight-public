package wtf.moonlight.gui.altmanager.utils.alt.microsoft;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public final class MicrosoftLogin implements Closeable {
    private static final String CLIENT_ID = "67b74668-ef33-49c3-a75c-18cbb2481e0c";
    //00000000402b5328
    //67b74668-ef33-49c3-a75c-18cbb2481e0c
    private static final String REDIRECT_URI = "http://localhost:3434/sad";
    private static final String SCOPE = "XboxLive.signin%20offline_access";

    private static final String URL = "https://login.live.com/oauth20_authorize.srf?client_id=<client_id>&redirect_uri=<redirect_uri>&response_type=code&display=touch&scope=<scope>"
            .replace("<client_id>", CLIENT_ID)
            .replace("<redirect_uri>", REDIRECT_URI)
            .replace("<scope>", SCOPE);


    @Getter
    public volatile String uuid = null;
    @Getter
    public volatile String userName = null;
    @Getter
    public volatile String accessToken = null;
    @Getter

    public volatile String refreshToken = null;
    @Getter
    public volatile boolean logged = false;
    @Getter
    @Setter
    public volatile String status = EnumChatFormatting.YELLOW + "Login...";
    @Getter

    private final HttpServer httpServer;

    @SuppressWarnings("FieldCanBeLocal")
    private final MicrosoftHttpHandler handler;

    public MicrosoftLogin() throws IOException {
        handler = new MicrosoftHttpHandler();
        httpServer = HttpServer.create(new InetSocketAddress("localhost", 3434), 0);
        httpServer.createContext("/sad", handler);
        httpServer.start();

        //String browserPath = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";

        Desktop desk = Desktop.getDesktop();

        try {
            //ProcessBuilder processBuilder = new ProcessBuilder(browserPath, URL);
            //processBuilder.start();
            desk.browse(new URI(URL));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public MicrosoftLogin(String refreshToken) throws IOException {
        this.refreshToken = refreshToken;
        this.httpServer = null;
        this.handler = null;

        final String microsoftTokenAndRefreshToken = getMicrosoftTokenFromRefreshToken(refreshToken);
        final String xBoxLiveToken = getXBoxLiveToken(microsoftTokenAndRefreshToken);
        final String[] xstsTokenAndHash = getXSTSTokenAndUserHash(xBoxLiveToken);
        final String accessToken = getAccessToken(xstsTokenAndHash[0], xstsTokenAndHash[1]);
        final URL url = new URL("https://api.minecraftservices.com/minecraft/profile");
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);

        final String read = read(connection.getInputStream());
        final JSONObject jsonObject = JSON.parseObject(read);
        final String uuid = jsonObject.getString("id");
        final String userName = jsonObject.getString("name");

        MicrosoftLogin.this.uuid = uuid;
        MicrosoftLogin.this.userName = userName;
        MicrosoftLogin.this.accessToken = accessToken;
        MicrosoftLogin.this.logged = true;
    }

    @Override
    public void close() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
    }

    public void show() throws Exception {
        Desktop.getDesktop().browse(new URI(URL));
    }

    private String getAccessToken(String xstsToken, String uhs) throws IOException {
        status = EnumChatFormatting.YELLOW + "Getting access token";
        System.out.println("Getting access token");
        final URL url = new URL("https://api.minecraftservices.com/authentication/login_with_xbox");
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoInput(true);
        connection.setDoOutput(true);

        final JSONObject input = new JSONObject(true);
        input.put("identityToken", "XBL3.0 x=" + uhs + ";" + xstsToken);

        write(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream())), JSON.toJSONString(input));

        final JSONObject jsonObject = JSON.parseObject(read(connection.getInputStream()));

        return jsonObject.getString("access_token");
    }

    public String getMicrosoftTokenFromRefreshToken(String refreshToken) throws IOException {
        status = EnumChatFormatting.YELLOW + "Getting microsoft token from refresh token";
        System.out.println("Getting microsoft token from refresh token");

        final URL url = new URL("https://login.live.com/oauth20_token.srf");
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        final String param = "client_id=" + CLIENT_ID +
                //"&client_secret=" + "" +
                "&refresh_token=" + refreshToken +
                "&grant_type=refresh_token" +
                "&redirect_uri=" + REDIRECT_URI;

        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        write(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream())), param);

        final JSONObject response_obj = JSON.parseObject(read(connection.getInputStream()));
        return response_obj.getString("access_token");
    }

    public String[] getMicrosoftTokenAndRefreshToken(String code) throws IOException {
        status = EnumChatFormatting.YELLOW + "Getting microsoft token";
        System.out.println("Getting microsoft token");
        final URL url = new URL("https://login.live.com/oauth20_token.srf");
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        final String param = "client_id=" + CLIENT_ID +
                "&code=" + code +
                "&grant_type=authorization_code" +
                "&redirect_uri=" + REDIRECT_URI +
                "&scope=" + SCOPE;

        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        write(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream())), param);

        final JSONObject response_obj = JSON.parseObject(read(connection.getInputStream()));
        return new String[]{response_obj.getString("access_token"), response_obj.getString("refresh_token")};
    }

    @SuppressWarnings("HttpUrlsUsage")
    public String getXBoxLiveToken(String microsoftToken) throws IOException {
        status = EnumChatFormatting.YELLOW + "Getting xbox live token";
        System.out.println("Getting xbox live token");
        final URL connectUrl = new URL("https://user.auth.xboxlive.com/user/authenticate");
        final String param;
        final JSONObject xbl_param = new JSONObject(true);
        final JSONObject xbl_properties = new JSONObject(true);
        xbl_properties.put("AuthMethod", "RPS");
        xbl_properties.put("SiteName", "user.auth.xboxlive.com");
        xbl_properties.put("RpsTicket", "d=" + microsoftToken);
        xbl_param.put("Properties", xbl_properties);
        xbl_param.put("RelyingParty", "http://auth.xboxlive.com");
        xbl_param.put("TokenType", "JWT");
        param = JSON.toJSONString(xbl_param);
        final HttpURLConnection connection = (HttpURLConnection) connectUrl.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");

        write(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream())), param);

        final JSONObject response_obj = JSON.parseObject(read(connection.getInputStream()));
        return response_obj.getString("Token");
    }

    public String[] getXSTSTokenAndUserHash(String xboxLiveToken) throws IOException {
        status = EnumChatFormatting.YELLOW + "Getting xsts token and user hash";
        System.out.println("Getting xsts token and user hash");
        final URL ConnectUrl = new URL("https://xsts.auth.xboxlive.com/xsts/authorize");
        final String param;
        final ArrayList<String> tokens = new ArrayList<>();
        tokens.add(xboxLiveToken);
        final JSONObject xbl_param = new JSONObject(true);
        final JSONObject xbl_properties = new JSONObject(true);
        xbl_properties.put("SandboxId", "RETAIL");
        xbl_properties.put("UserTokens", JSONArray.parse(JSON.toJSONString(tokens)));
        xbl_param.put("Properties", xbl_properties);
        xbl_param.put("RelyingParty", "rp://api.minecraftservices.com/");
        xbl_param.put("TokenType", "JWT");
        param = JSON.toJSONString(xbl_param);
        final HttpURLConnection connection = (HttpURLConnection) ConnectUrl.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        write(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream())), param);
        final JSONObject response_obj = JSON.parseObject(read(connection.getInputStream()));

        final String token = response_obj.getString("Token");
        final String uhs = response_obj.getJSONObject("DisplayClaims").getJSONArray("xui").getJSONObject(0).getString("uhs");
        return new String[]{token, uhs};
    }

    private void write(BufferedWriter writer, String s) throws IOException {
        writer.write(s);
        writer.close();
    }

    private String read(InputStream stream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        final StringBuilder stringBuilder = new StringBuilder();
        String s;
        while ((s = reader.readLine()) != null) {
            stringBuilder.append(s);
        }

        stream.close();
        reader.close();

        return stringBuilder.toString();
    }


    private class MicrosoftHttpHandler implements HttpHandler {
        private boolean got = false;

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            if (!got) {
                final String query = httpExchange.getRequestURI().getQuery();
                if (query.contains("code")) {
                    got = true;
                    final String code = query.split("code=")[1];

                    final String[] microsoftTokenAndRefreshToken = getMicrosoftTokenAndRefreshToken(code);
                    final String xBoxLiveToken = getXBoxLiveToken(microsoftTokenAndRefreshToken[0]);
                    final String[] xstsTokenAndHash = getXSTSTokenAndUserHash(xBoxLiveToken);
                    final String accessToken = getAccessToken(xstsTokenAndHash[0], xstsTokenAndHash[1]);
                    final URL url = new URL("https://api.minecraftservices.com/minecraft/profile");
                    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Authorization", "Bearer " + accessToken);

                    final String read = read(connection.getInputStream());
                    final JSONObject jsonObject = JSON.parseObject(read);
                    final String uuid = jsonObject.getString("id");
                    final String userName = jsonObject.getString("name");

                    MicrosoftLogin.this.uuid = uuid;
                    MicrosoftLogin.this.userName = userName;
                    MicrosoftLogin.this.accessToken = accessToken;
                    MicrosoftLogin.this.refreshToken = microsoftTokenAndRefreshToken[1];
                    MicrosoftLogin.this.logged = true;
                }
            }
        }
    }
}
