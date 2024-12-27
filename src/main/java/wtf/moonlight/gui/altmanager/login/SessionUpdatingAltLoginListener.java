/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & opZywl & lucas]
 */
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
