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
