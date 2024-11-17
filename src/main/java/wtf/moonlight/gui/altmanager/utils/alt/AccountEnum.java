package wtf.moonlight.gui.altmanager.utils.alt;

public enum AccountEnum {
    OFFLINE("OFFLINE"),
    MOJANG("MOJANG"),
    MICROSOFT("MICROSOFT"),
    ORIGINAL("ORIGINAL");

    private final String writeName;

    AccountEnum(String name) {
        this.writeName = name;
    }

    public String getWriteName() {
        return writeName;
    }

    public static AccountEnum parse(String str) {
        for (AccountEnum value : values()) {
            if (value.writeName.equals(str)) {
                return value;
            }
        }

        return null;
    }
}
