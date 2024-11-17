package net.minecraft.client.resources;

import java.util.Map;

public class I18n
{
    private static Locale i18nLocale;

    static void setLocale(Locale i18nLocaleIn)
    {
        i18nLocale = i18nLocaleIn;
    }

    public static String format(String translateKey, Object... parameters)
    {
        return i18nLocale.formatMessage(translateKey, parameters);
    }

    public static Map getLocaleProperties()
    {
        return i18nLocale.properties;
    }
}
