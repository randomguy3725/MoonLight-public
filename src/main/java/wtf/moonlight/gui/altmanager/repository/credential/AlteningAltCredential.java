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
package wtf.moonlight.gui.altmanager.repository.credential;

import com.thealtening.api.response.AccountDetails;
import lombok.NonNull;
import wtf.moonlight.gui.altmanager.utils.Checks;

import java.util.Objects;

public class AlteningAltCredential extends AltCredential {

    @NonNull
    private final String name;
    @NonNull
    private final AccountDetails details;

    public AlteningAltCredential(@NonNull String login, @NonNull String name, @NonNull AccountDetails details) {
        super(login, null);
        this.name = Checks.notBlank(name, "name");
        this.details = details;
    }

    @NonNull
    public String getName() {
        return this.name;
    }

    @NonNull
    public AccountDetails getDetails() {
        return this.details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlteningAltCredential)) return false;
        if (!super.equals(o)) return false;
        final AlteningAltCredential that = (AlteningAltCredential) o;
        return this.name.equals(that.name) && this.details.equals(that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.name, this.details);
    }


}
