package net.minecraft.server.management;

import com.google.gson.JsonObject;
import java.util.Date;

public class IPBanEntry extends BanEntry<String>
{
    public IPBanEntry(String valueIn)
    {
        this(valueIn, null, null, null, null);
    }

    public IPBanEntry(String valueIn, Date startDate, String banner, Date endDate, String banReason)
    {
        super(valueIn, startDate, banner, endDate, banReason);
    }

    public IPBanEntry(JsonObject json)
    {
        super(getIPFromJson(json), json);
    }

    private static String getIPFromJson(JsonObject json)
    {
        return json.has("ip") ? json.get("ip").getAsString() : null;
    }

    protected void onSerialization(JsonObject data)
    {
        if (this.getValue() != null)
        {
            data.addProperty("ip", this.getValue());
            super.onSerialization(data);
        }
    }
}
