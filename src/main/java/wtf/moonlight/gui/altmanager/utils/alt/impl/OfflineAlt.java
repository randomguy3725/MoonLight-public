package wtf.moonlight.gui.altmanager.utils.alt.impl;

import wtf.moonlight.gui.altmanager.utils.alt.AccountEnum;
import wtf.moonlight.gui.altmanager.utils.alt.Alt;

public final class OfflineAlt extends Alt {
    public OfflineAlt(String userName) {
        super(userName, AccountEnum.OFFLINE);
    }
}
