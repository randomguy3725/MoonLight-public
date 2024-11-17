package net.minecraft.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.util.IllegalFormatException;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class StringTranslate
{
    private static final Pattern numericVariablePattern = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    private static final Splitter equalSignSplitter = Splitter.on('=').limit(2);
    private static final StringTranslate instance = new StringTranslate();
    private final Map<String, String> languageList = Maps.newHashMap();
    private long lastUpdateTimeInMilliseconds;

    public StringTranslate()
    {
        try
        {
            InputStream inputstream = StringTranslate.class.getResourceAsStream("/assets/minecraft/lang/en_US.lang");

            for (String s : IOUtils.readLines(inputstream, Charsets.UTF_8))
            {
                if (!s.isEmpty() && s.charAt(0) != 35)
                {
                    String[] astring = Iterables.toArray(equalSignSplitter.split(s), String.class);

                    if (astring != null && astring.length == 2)
                    {
                        String s1 = astring[0];
                        String s2 = numericVariablePattern.matcher(astring[1]).replaceAll("%$1s");
                        this.languageList.put(s1, s2);
                    }
                }
            }

            this.lastUpdateTimeInMilliseconds = System.currentTimeMillis();
        }
        catch (IOException var7)
        {
        }
    }

    static StringTranslate getInstance()
    {
        return instance;
    }

    public static synchronized void replaceWith(Map<String, String> p_135063_0_)
    {
        instance.languageList.clear();
        instance.languageList.putAll(p_135063_0_);
        instance.lastUpdateTimeInMilliseconds = System.currentTimeMillis();
    }

    public synchronized String translateKey(String key)
    {
        return this.tryTranslateKey(key);
    }

    public synchronized String translateKeyFormat(String key, Object... format)
    {
        String s = this.tryTranslateKey(key);

        try
        {
            return String.format(s, format);
        }
        catch (IllegalFormatException var5)
        {
            return "Format error: " + s;
        }
    }

    private String tryTranslateKey(String key)
    {
        String s = this.languageList.get(key);
        return s == null ? key : s;
    }

    public synchronized boolean isKeyTranslated(String key)
    {
        return this.languageList.containsKey(key);
    }

    public long getLastUpdateTimeInMilliseconds()
    {
        return this.lastUpdateTimeInMilliseconds;
    }
}