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
package wtf.moonlight.gui.altmanager.utils.thealtening.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;

public final class ServiceSwitcher {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String MINECRAFT_SESSION_SERVICE_CLASS = "com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService";
    private static final String MINECRAFT_AUTHENTICATION_SERVICE_CLASS = "com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication";
    private final FieldAdapter minecraftSessionServer = new FieldAdapter(MINECRAFT_SESSION_SERVICE_CLASS);
    private final FieldAdapter userAuthentication = new FieldAdapter(MINECRAFT_AUTHENTICATION_SERVICE_CLASS);

    public ServiceSwitcher() {
        try {
            final String[] whitelistedDomains = new String[]{".minecraft.net", ".mojang.com", ".thealtening.com"};
            minecraftSessionServer.updateFieldIfPresent("WHITELISTED_DOMAINS", whitelistedDomains);
        } catch (Throwable t) {
            LOGGER.warn(t);
        }
    }

    public AlteningServiceType switchToService(AlteningServiceType service) {
        try {
            final String authServer = service.getAuthServer();
            final FieldAdapter userAuth = this.userAuthentication;

            userAuth.updateFieldIfPresent("BASE_URL", authServer);
            userAuth.updateFieldIfPresent("ROUTE_AUTHENTICATE", new URL(authServer + "authenticate"));
            userAuth.updateFieldIfPresent("ROUTE_INVALIDATE", new URL(authServer + "invalidate"));
            userAuth.updateFieldIfPresent("ROUTE_REFRESH", new URL(authServer + "refresh"));
            userAuth.updateFieldIfPresent("ROUTE_VALIDATE", new URL(authServer + "validate"));
            userAuth.updateFieldIfPresent("ROUTE_SIGNOUT", new URL(authServer + "signout"));

            final String sessionServer = service.getSessionServer();
            final FieldAdapter userSession = minecraftSessionServer;

            userSession.updateFieldIfPresent("BASE_URL", sessionServer + "session/minecraft/");
            userSession.updateFieldIfPresent("JOIN_URL", new URL(sessionServer + "session/minecraft/join"));
            userSession.updateFieldIfPresent("CHECK_URL", new URL(sessionServer + "session/minecraft/hasJoined"));
        } catch (Exception e) {
            LOGGER.warn(e);
            return AlteningServiceType.MOJANG;
        }

        return service;
    }

}
