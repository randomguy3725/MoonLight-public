package wtf.moonlight.gui.altmanager.login;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public abstract class SessionUpdatingAltLoginListener implements AltLoginListener {

    @Override
    public void onLoginSuccess(AltType type, Session session) {
        updateMinecraftSession(session);
    }

    private void updateMinecraftSession(Session newSession) {
        Minecraft.getMinecraft().session = newSession;
    }

}
