package wtf.moonlight.gui.altmanager.repository.credential;

import java.util.Objects;
import java.util.UUID;

public class MicrosoftAltCredential extends AltCredential {
    private final String name;
    private final String refreshToken;
    private final UUID uuid;

    public MicrosoftAltCredential(String name, String refreshToken, UUID uuid) {
        super("", "");
        this.name = name;
        this.refreshToken = refreshToken;
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MicrosoftAltCredential)) return false;
        if (!super.equals(o)) return false;
        final MicrosoftAltCredential that = (MicrosoftAltCredential) o;
        return name.equals(that.name) && refreshToken.equals(that.refreshToken) && uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, refreshToken, uuid);
    }

    public String getName() {
        return name;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public UUID getUUID() {
        return uuid;
    }
}
