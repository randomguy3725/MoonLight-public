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

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class AltCredential {
	private final String login;
	private final String password;

	public AltCredential(@NotNull String login, @Nullable String password) {
		this.login = login.trim();
		this.password = StringUtils.isNotBlank(password) ? password : null;
	}

	@NotNull
	public String getLogin() {
		return login;
	}

	@Nullable
	public String getPassword() {
		return password;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		AltCredential that = (AltCredential) o;
		return login.equals(that.login) && Objects.equals(password, that.password);
	}

	@Override
	public int hashCode() {
		return Objects.hash(login, password);
	}

	@Override
	public String toString() {
		return login + (password != null ? ":" + password : "");
	}

}
