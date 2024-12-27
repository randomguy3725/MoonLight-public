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
package wtf.moonlight.gui.altmanager.login;

import net.minecraft.util.Session;


public interface AltLoginListener {

    void onLoginSuccess(AltType altType, Session session);

    void onLoginFailed();

}
