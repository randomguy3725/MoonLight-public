package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.Date;
import java.util.UUID;

public class UserListBansEntry extends BanEntry<GameProfile>
{
    public UserListBansEntry(GameProfile profile)
    {
        this(profile, null, null, null, null);
    }

    public UserListBansEntry(GameProfile profile, Date startDate, String banner, Date endDate, String banReason)
    {
        super(profile, endDate, banner, endDate, banReason);
    }

    public UserListBansEntry(JsonObject json)
    {
        super(toGameProfile(json), json);
    }

    protected void onSerialization(JsonObject data)
    {
        if (this.getValue() != null)
        {
            data.addProperty("uuid", this.getValue().getId() == null ? "" : this.getValue().getId().toString());
            data.addProperty("name", this.getValue().getName());
            super.onSerialization(data);
        }
    }

    private static GameProfile toGameProfile(JsonObject json)
    {
        if (json.has("uuid") && json.has("name"))
        {
            String s = json.get("uuid").getAsString();
            UUID uuid;

            try
            {
                uuid = UUID.fromString(s);
            }
            catch (Throwable var4)
            {
                return null;
            }

            return new GameProfile(uuid, json.get("name").getAsString());
        }
        else
        {
            return null;
        }
    }
}
