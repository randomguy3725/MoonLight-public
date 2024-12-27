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

        switch (service) {
            case MOJANG:
                sslController.enableCertificateValidation();
                break;

            case THEALTENING:
                sslController.disableCertificateValidation();
                break;
        }

        this.service = serviceSwitcher.switchToService(service);
    }

    public static TheAlteningAuthentication mojang() {
        return withService(AlteningServiceType.MOJANG);
    }

    public static TheAlteningAuthentication theAltening() {
        return withService(AlteningServiceType.THEALTENING);
    }

    private static TheAlteningAuthentication withService(@NonNull AlteningServiceType service) {
        if (instance == null) {
            try {
                instance = new TheAlteningAuthentication(service);
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                SSLController.log.warn(e);
            } catch (Throwable t) {
                SSLController.log.warn("Unexpected error occurred while executing...", t);
            }
        } else if (instance.getService() != service) {
            instance.updateService(service);
        }

        return instance;
    }

    @NonNull
    public AlteningServiceType getService() {
        return this.service;
    }

}
