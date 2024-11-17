package wtf.moonlight.gui.altmanager.utils.alt.impl;

import wtf.moonlight.gui.altmanager.utils.alt.AccountEnum;
import wtf.moonlight.gui.altmanager.utils.alt.Alt;

public final class OriginalAlt extends Alt {
    private final String accessToken;
    private final String uuid;
    private final String type;

    public OriginalAlt(String userName, String accessToken, String uuid, String type) {
        super(userName, AccountEnum.ORIGINAL);
        this.accessToken = accessToken;
        this.uuid = uuid;
        this.type = type;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getUUID() {
        return uuid;
    }

    public String getType() {
        return type;
    }
}
