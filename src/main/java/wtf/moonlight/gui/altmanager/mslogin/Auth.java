/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [RandomGuy & opZywl]
 */
package wtf.moonlight.gui.altmanager.mslogin;


import com.google.gson.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.stream.Collectors;

public class Auth {
    public static final SSLContext context;
    
    private static final Logger logger = LogManager.getLogger(Auth.class);
    private static final Gson gson = new GsonBuilder().create();
    
    static {
        SSLContext ctx = null;
        try {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            try (InputStream in = Auth.class.getResourceAsStream("/ssl.jks")) {
                ks.load(in, "floatILoveU".toCharArray());
            }
            TrustManagerFactory customTmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            customTmf.init(ks);
            TrustManagerFactory defaultTmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            defaultTmf.init((KeyStore) null);
            List<X509TrustManager> managers = new ArrayList<>();
            managers.addAll(Arrays.stream(customTmf.getTrustManagers()).filter(tm -> tm instanceof X509TrustManager)
                    .map(tm -> (X509TrustManager) tm).collect(Collectors.toList()));
            managers.addAll(Arrays.stream(defaultTmf.getTrustManagers()).filter(tm -> tm instanceof X509TrustManager)
                    .map(tm -> (X509TrustManager) tm).collect(Collectors.toList()));
            TrustManager multiManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    CertificateException wrapper = new CertificateException("Unable to validate via any trust manager.");
                    for (X509TrustManager manager : managers) {
                        try {
                            manager.checkClientTrusted(chain, authType);
                            return;
                        } catch (Throwable t) {
                            wrapper.addSuppressed(t);
                        }
                    }
                    throw wrapper;
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    CertificateException wrapper = new CertificateException("Unable to validate via any trust manager.");
                    for (X509TrustManager manager : managers) {
                        try {
                            manager.checkServerTrusted(chain, authType);
                            return;
                        } catch (Throwable t) {
                            wrapper.addSuppressed(t);
                        }
                    }
                    throw wrapper;
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    List<X509Certificate> certificates = new ArrayList<>();
                    for (X509TrustManager manager : managers) {
                        certificates.addAll(Arrays.asList(manager.getAcceptedIssuers()));
                    }
                    return certificates.toArray(new X509Certificate[0]);
                }
            };
            ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[] { multiManager }, new SecureRandom());
        } catch (Throwable t) {
            logger.error("Unable to init SSL context", t);
        }
        context = ctx;
    }

    /**
     * Get Microsoft Access Token and Microsoft Refresh Token from Microsoft Authentication Code.
     *
     * @param code Code from user auth redirect
     * @return Pair of Microsoft Access Token and Microsoft Refresh Token
     * @throws Exception If something goes wrong
     * @see <a href="https://wiki.vg/Microsoft_Authentication_Scheme#Authorization_Code_-.3E_Authorization_Token">Reference</a>
     */
    public static Map.Entry<String, String> codeToToken(String code) throws Exception {
        HttpsURLConnection conn = (HttpsURLConnection) new URL("https://login.live.com/oauth20_token.srf").openConnection();
        if (context != null) conn.setSSLSocketFactory(context.getSocketFactory());
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        conn.setDoOutput(true);
        try (OutputStream out = conn.getOutputStream()) {
            out.write(("client_id=" + URLEncoder.encode("54fd49e4-2103-4044-9603-2b028c814ec3", "UTF-8") + "&" +
                    "code=" + URLEncoder.encode(code, "UTF-8") + "&" +
                    "grant_type=authorization_code&" +
                    "redirect_uri=" + URLEncoder.encode("http://localhost:59125", "UTF-8") + "&" +
                    "scope=XboxLive.signin%20XboxLive.offline_access").getBytes(StandardCharsets.UTF_8));
            if (conn.getResponseCode() < 200 || conn.getResponseCode() > 299) {
                try (BufferedReader err = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    throw new IllegalArgumentException("CodeToToken response: " + conn.getResponseCode() + ", data: " + err.lines().collect(Collectors.joining("\n")));
                } catch (Throwable t) {
                    throw new IllegalArgumentException("CodeToToken response: " + conn.getResponseCode(), t);
                }
            }
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                JsonObject resp = gson.fromJson(in.lines().collect(Collectors.joining("\n")), JsonObject.class);
                return new AbstractMap.SimpleImmutableEntry<>(resp.get("access_token").getAsString(), resp.get("refresh_token").getAsString());
            }
        }
    }

    /**
     * Refresh Old Microsoft Refresh Token and get new Microsoft Access Token.
     *
     * @param refreshToken Microsoft Refresh Token from {@link #codeToToken(String)} or from this method
     * @return Pair of Microsoft Access Token and Microsoft Refresh Token
     * @throws Exception If something goes wrong
     * @see <a href="https://wiki.vg/Microsoft_Authentication_Scheme#Refreshing_Tokens">Reference</a>
     */
    public static Map.Entry<String, String> refreshToken(String refreshToken) throws Exception {
        HttpsURLConnection conn = (HttpsURLConnection) new URL("https://login.live.com/oauth20_token.srf").openConnection();
        if (context != null) conn.setSSLSocketFactory(context.getSocketFactory());
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        conn.setDoOutput(true);
        try (OutputStream out = conn.getOutputStream()) {
            out.write(("client_id=" + URLEncoder.encode("54fd49e4-2103-4044-9603-2b028c814ec3", "UTF-8") + "&" +
                    "refresh_token=" + URLEncoder.encode(refreshToken, "UTF-8") + "&" +
                    "grant_type=refresh_token&" +
                    "redirect_uri=" + URLEncoder.encode("http://localhost:59125", "UTF-8") + "&" +
                    "scope=XboxLive.signin%20XboxLive.offline_access").getBytes(StandardCharsets.UTF_8));
            if (conn.getResponseCode() < 200 || conn.getResponseCode() > 299) {
                try (BufferedReader err = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    throw new IllegalArgumentException("refreshToken response: " + conn.getResponseCode() + ", data: " + err.lines().collect(Collectors.joining("\n")));
                } catch (Throwable t) {
                    throw new IllegalArgumentException("refreshToken response: " + conn.getResponseCode(), t);
                }
            }
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                JsonObject resp = gson.fromJson(in.lines().collect(Collectors.joining("\n")), JsonObject.class);
                return new AbstractMap.SimpleImmutableEntry<>(resp.get("access_token").getAsString(), resp.get("refresh_token").getAsString());
            }
        }
    }

    /**
     * Get XBL Token from Microsoft Access Token.
     *
     * @param authToken Microsoft Access Token from {@link #codeToToken(String)} or {@link #refreshToken(String)}
     * @return XBL Token
     * @throws Exception If something goes wrong
     * @see <a href="https://wiki.vg/Microsoft_Authentication_Scheme#Authenticate_with_XBL">Reference</a>
     */
    public static String authXBL(String authToken) throws Exception {
        HttpsURLConnection conn = (HttpsURLConnection) new URL("https://user.auth.xboxlive.com/user/authenticate").openConnection();
        if (context != null) conn.setSSLSocketFactory(context.getSocketFactory());
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        conn.setDoOutput(true);
        try (OutputStream out = conn.getOutputStream()) {
            JsonObject req = new JsonObject();
            JsonObject reqProps = new JsonObject();
            reqProps.addProperty("AuthMethod", "RPS");
            reqProps.addProperty("SiteName", "user.auth.xboxlive.com");
            reqProps.addProperty("RpsTicket", "d=" + authToken);
            req.add("Properties", reqProps);
            req.addProperty("RelyingParty", "http://auth.xboxlive.com");
            req.addProperty("TokenType", "JWT");
            out.write(req.toString().getBytes(StandardCharsets.UTF_8));
            if (conn.getResponseCode() < 200 || conn.getResponseCode() > 299) {
                try (BufferedReader err = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    throw new IllegalArgumentException("AuthXBL response: " + conn.getResponseCode() + ", data: " + err.lines().collect(Collectors.joining("\n")));
                } catch (Throwable t) {
                    throw new IllegalArgumentException("AuthXBL response: " + conn.getResponseCode(), t);
                }
            }
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                JsonObject resp = gson.fromJson(in.lines().collect(Collectors.joining("\n")), JsonObject.class);
                return resp.get("Token").getAsString();
            }
        }
    }

    /**
     * Get XSTS Token and XUI-UHS Userhash from XBL Token.
     *
     * @param xblToken XBL Token from {@link #authXBL(String)}
     * @return Pair of XSTS Token and XUI-UHS Userhash
     * @throws Exception If something goes wrong
     * @see <a href="https://wiki.vg/Microsoft_Authentication_Scheme#Authenticate_with_XSTS">Reference</a>
     */
    public static Map.Entry<String, String> authXSTS(String xblToken) throws Exception {
        HttpsURLConnection conn = (HttpsURLConnection) new URL("https://xsts.auth.xboxlive.com/xsts/authorize").openConnection();
        if (context != null) conn.setSSLSocketFactory(context.getSocketFactory());
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        conn.setDoOutput(true);
        try (OutputStream out = conn.getOutputStream()) {
            JsonObject req = new JsonObject();
            JsonObject reqProps = new JsonObject();
            JsonArray userTokens = new JsonArray();
            userTokens.add(new JsonPrimitive(xblToken));
            reqProps.add("UserTokens", userTokens);
            reqProps.addProperty("SandboxId", "RETAIL");
            req.add("Properties", reqProps);
            req.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
            req.addProperty("TokenType", "JWT");
            out.write(req.toString().getBytes(StandardCharsets.UTF_8));
            if (conn.getResponseCode() < 200 || conn.getResponseCode() > 299) {
                try (BufferedReader err = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    throw new IllegalArgumentException("AuthXSTS response: " + conn.getResponseCode() + ", data: " + err.lines().collect(Collectors.joining("\n")));
                } catch (Throwable t) {
                    throw new IllegalArgumentException("AuthXSTS response: " + conn.getResponseCode(), t);
                }
            }
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                JsonObject resp = gson.fromJson(in.lines().collect(Collectors.joining("\n")), JsonObject.class);
                return new AbstractMap.SimpleImmutableEntry<>(resp.get("Token").getAsString(), resp.getAsJsonObject("DisplayClaims")
                        .getAsJsonArray("xui").get(0).getAsJsonObject().get("uhs").getAsString());
            }
        }

    }

    /**
     * Get Minecraft Access Token from XUI-UHS Userhash and XSTS Token.
     *
     * @param userHash  XUI-UHS Userhash from {@link #authXSTS(String)}
     * @param xstsToken XSTS Token from {@link #authXSTS(String)}
     * @return Minecraft Access Token
     * @throws Exception If something goes wrong
     * @see <a href="https://wiki.vg/Microsoft_Authentication_Scheme#Authenticate_with_Minecraft">Reference</a>
     */
    public static String authMinecraft(String userHash, String xstsToken) throws Exception {
        HttpsURLConnection conn = (HttpsURLConnection) new URL("https://api.minecraftservices.com/authentication/login_with_xbox").openConnection();
        if (context != null) conn.setSSLSocketFactory(context.getSocketFactory());
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        conn.setDoOutput(true);
        try (OutputStream out = conn.getOutputStream()) {
            JsonObject req = new JsonObject();
            req.addProperty("identityToken", "XBL3.0 x=" + userHash + ";" + xstsToken);
            out.write(req.toString().getBytes(StandardCharsets.UTF_8));
            if (conn.getResponseCode() < 200 || conn.getResponseCode() > 299) {
                try (BufferedReader err = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    throw new IllegalArgumentException("AuthMinecraft response: " + conn.getResponseCode() + ", data: " + err.lines().collect(Collectors.joining("\n")));
                } catch (Throwable t) {
                    throw new IllegalArgumentException("AuthMinecraft response: " + conn.getResponseCode(), t);
                }
            }
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                JsonObject resp = gson.fromJson(in.lines().collect(Collectors.joining("\n")), JsonObject.class);
                return resp.get("access_token").getAsString();
            }
        }
    }

    /**
     * Get Player UUID and Player Name from Minecraft Access Token.
     *
     * @param accessToken Minecraft Access Token from {@link #authMinecraft(String, String)}
     * @return Pair of Player UUID and Player Name
     * @throws Exception If something goes wrong
     * @see <a href="https://wiki.vg/Microsoft_Authentication_Scheme#Get_the_profile">Reference</a>
     */
    public static Map.Entry<UUID, String> getProfile(String accessToken) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL("https://api.minecraftservices.com/minecraft/profile").openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        if (conn.getResponseCode() < 200 || conn.getResponseCode() > 299) {
            try (BufferedReader err = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                throw new IllegalArgumentException("GetProfile response: " + conn.getResponseCode() + ", data: " + err.lines().collect(Collectors.joining("\n")));
            } catch (Throwable t) {
                throw new IllegalArgumentException("GetProfile response: " + conn.getResponseCode(), t);
            }
        }
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            JsonObject resp = gson.fromJson(in.lines().collect(Collectors.joining("\n")), JsonObject.class);
            return new AbstractMap.SimpleImmutableEntry<>(UUID.fromString(resp.get("id").getAsString().replaceFirst(
                    "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5")),
                    resp.get("name").getAsString());
        }
    }

    /**
     * Resolve UUID from name using Mojang API.
     *
     * @param name Player name
     * @return Resolved v4 UUID, v3 Offline UUID if it can't be resolved
     */
    public static UUID resolveUUID(String name) {
        try (InputStreamReader in = new InputStreamReader(new URL("https://api.mojang.com/users/profiles/minecraft/"
                + name).openStream(), StandardCharsets.UTF_8)) {
            return UUID.fromString(gson.fromJson(in, JsonObject.class).get("id").getAsString().replaceFirst(
                    "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
        } catch (Throwable ignored) {
            return UUID.randomUUID();
        }
    }
}
