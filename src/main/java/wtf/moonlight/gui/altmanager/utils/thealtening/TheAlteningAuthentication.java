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
package wtf.moonlight.gui.altmanager.utils.thealtening;

import lombok.NonNull;
import wtf.moonlight.gui.altmanager.utils.thealtening.service.AlteningServiceType;
import wtf.moonlight.gui.altmanager.utils.thealtening.service.ServiceSwitcher;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public final class TheAlteningAuthentication {

    private static TheAlteningAuthentication instance;

    private final ServiceSwitcher serviceSwitcher = new ServiceSwitcher();
    private final SSLController sslController = new SSLController();
    private AlteningServiceType service;

    private TheAlteningAuthentication(@NonNull AlteningServiceType service) throws Throwable {
        updateService(service);
    }

    public void updateService(@NonNull AlteningServiceType service) {
        if (this.service == service) return;

        sslController.enableCertificateValidation();

        this.service = serviceSwitcher.switchToService(service);
    }

    public static TheAlteningAuthentication mojang() {
        return withService();
    }

    private static TheAlteningAuthentication withService() {
        if (instance == null) {
            try {
                instance = new TheAlteningAuthentication(AlteningServiceType.MOJANG);
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                SSLController.log.warn(e);
            } catch (Throwable t) {
                SSLController.log.warn("Unexpected error occurred while executing...", t);
            }
        }

        return instance;
    }

}
