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

public enum AlteningServiceType {

    MOJANG("https://authserver.mojang.com/", "https://sessionserver.mojang.com/"),
    THEALTENING("http://authserver.thealtening.com/", "http://sessionserver.thealtening.com/");

    private final String authServer;
    private final String sessionServer;

    AlteningServiceType(String authServer, String sessionServer) {
        this.authServer = authServer;
        this.sessionServer = sessionServer;
    }

    //region Lombok
    public String getAuthServer() {
        return this.authServer;
    }

    public String getSessionServer() {
        return this.sessionServer;
    }


}
