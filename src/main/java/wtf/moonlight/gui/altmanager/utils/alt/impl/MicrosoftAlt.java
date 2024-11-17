package wtf.moonlight.gui.altmanager.utils.alt.impl;

import wtf.moonlight.gui.altmanager.utils.alt.AccountEnum;
import wtf.moonlight.gui.altmanager.utils.alt.Alt;

public final class MicrosoftAlt extends Alt {
    private final String refreshToken;

    public MicrosoftAlt(String userName, String refreshToken) {
        super(userName, AccountEnum.MICROSOFT);
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
