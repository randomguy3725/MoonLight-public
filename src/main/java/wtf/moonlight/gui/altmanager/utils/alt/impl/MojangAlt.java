package wtf.moonlight.gui.altmanager.utils.alt.impl;

import wtf.moonlight.gui.altmanager.utils.alt.AccountEnum;
import wtf.moonlight.gui.altmanager.utils.alt.Alt;

public final class MojangAlt extends Alt {
    private final String account;
    private final String password;

    public MojangAlt(String account, String password, String userName) {
        super(userName, AccountEnum.MOJANG);
        this.account = account;
        this.password = password;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }
}
