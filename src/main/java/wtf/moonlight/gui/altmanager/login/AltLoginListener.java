package wtf.moonlight.gui.altmanager.login;

import net.minecraft.util.Session;


public interface AltLoginListener {

    void onLoginSuccess(AltType altType, Session session);

    void onLoginFailed();

}
