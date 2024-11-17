package wtf.moonlight.features.friend;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Friend {
    private final String username;
    private String alias;

    public Friend(final String username) {
        this(username, username);
    }

    public Friend(final String alias, final String username) {
        this.alias = alias;
        this.username = username;
    }
}
