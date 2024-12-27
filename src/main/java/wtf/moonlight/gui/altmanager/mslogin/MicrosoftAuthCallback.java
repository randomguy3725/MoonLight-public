package wtf.moonlight.gui.altmanager.mslogin;

import wtf.moonlight.gui.altmanager.repository.credential.MicrosoftAltCredential;
import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.Closeable;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class MicrosoftAuthCallback implements Closeable {
    public static final String url = "https://login.live.com/oauth20_authorize.srf?client_id=54fd49e4-2103-4044-9603-2b028c814ec3&response_type=code&scope=XboxLive.signin%20XboxLive.offline_access&redirect_uri=http://localhost:59125&prompt=select_account";
    private static final Logger logger = LogManager.getLogger(MicrosoftAuthCallback.class);
    
    private static HttpServer server;

    public CompletableFuture<MicrosoftAltCredential> start(BiConsumer<String, Object[]> progressHandler) {
        CompletableFuture<MicrosoftAltCredential> cf = new CompletableFuture<>();
        try {
            if (server != null) {
                server.stop(0);
                server = null;
            }

            server = HttpServer.create(new InetSocketAddress("localhost", 59125), 0);
            server.createContext("/", ex -> {
            	logger.info("Microsoft authentication callback request: " + ex.getRemoteAddress());
                try {
                    final byte[] messageToHTML = "You can now close this page.".getBytes(StandardCharsets.UTF_8);

                    progressHandler.accept("Authentication... (%s)", new Object[] {"preparing"});
                    ex.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
                    ex.sendResponseHeaders(307, messageToHTML.length);
                    try (OutputStream os = ex.getResponseBody()) {
                        os.write(messageToHTML);
                    }
                    close();

                    final Thread thread = new Thread(() -> {
                        try {
                            cf.complete(auth(progressHandler, ex.getRequestURI().getQuery()));
                        } catch (Throwable t) {
                            logger.error("Unable to authenticate via Microsoft.", t);
                            cf.completeExceptionally(t);
                        }
                    }, "MicrosoftAuthThread");

                    thread.setDaemon(true);
                    thread.start();
                } catch (Throwable t) {
                    logger.error("Unable to process request on Microsoft authentication callback server.", t);
                    close();
                    cf.completeExceptionally(t);
                }
            });
            server.start();
            logger.info("Started Microsoft authentication callback server.");
        } catch (Throwable t) {
            logger.error("Unable to run the Microsoft authentication callback server.", t);
            close();
            cf.completeExceptionally(t);
        }
        return cf;
    }

    private MicrosoftAltCredential auth(BiConsumer<String, Object[]> progressHandler, String query) throws Exception {
        logger.info("Authenticating...");
        if (query == null) throw new NullPointerException("query=null");
        if (query.equals("error=access_denied&error_description=The user has denied access to the scope requested by the client application.")) return null;
        if (!query.startsWith("code=")) throw new IllegalStateException("query=" + query);
        logger.info("Step: CodeToToken.");
        progressHandler.accept("Authentication... (%s)", new Object[] {"CodeToToken"});
        Map.Entry<String, String> authRefreshTokens = Auth.codeToToken(query.replace("code=", ""));
        String refreshToken = authRefreshTokens.getValue();
        logger.info("Step: AuthXBL.");
        progressHandler.accept("Authentication... (%s)", new Object[] {"AuthXBL"});
        String xblToken = Auth.authXBL(authRefreshTokens.getKey());
        logger.info("Step: AuthXSTS.");
        progressHandler.accept("Authentication... (%s)", new Object[] {"AuthXSTS"});
        Map.Entry<String, String> xstsTokenUserhash = Auth.authXSTS(xblToken);
        logger.info("Step: AuthMinecraft.");
        progressHandler.accept("Authentication... (%s)", new Object[] {"AuthMinecraft"});
        String accessToken = Auth.authMinecraft(xstsTokenUserhash.getValue(), xstsTokenUserhash.getKey());
        logger.info("Step: GetProfile.");
        progressHandler.accept("Authentication... (%s)", new Object[] {"GetProfile"});
        Map.Entry<UUID, String> profile = Auth.getProfile(accessToken);
        logger.info("Authenticated.");
        return new MicrosoftAltCredential(profile.getValue(), refreshToken, profile.getKey());
    }

    @Override
    public void close() {
        try {
            if (server != null) {
                server.stop(0);
                logger.info("Stopped Microsoft authentication callback server.");
            }
        } catch (Throwable t) {
            logger.error("Unable to stop the Microsoft authentication callback server.", t);
        }
    }
}
