package net.minecraft.event;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.IChatComponent;

public class HoverEvent
{
    private final HoverEvent.Action action;
    private final IChatComponent value;

    public HoverEvent(HoverEvent.Action actionIn, IChatComponent valueIn)
    {
        this.action = actionIn;
        this.value = valueIn;
    }

    public HoverEvent.Action getAction()
    {
        return this.action;
    }

    public IChatComponent getValue()
    {
        return this.value;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass())
        {
            HoverEvent hoverevent = (HoverEvent)p_equals_1_;

            if (this.action != hoverevent.action)
            {
                return false;
            }
            else
            {
                if (this.value != null)
                {
                    return this.value.equals(hoverevent.value);
                }
                else return hoverevent.value == null;
            }
        }
        else
        {
            return false;
        }
    }

    public String toString()
    {
        return "HoverEvent{action=" + this.action + ", value='" + this.value + '\'' + '}';
    }

    public int hashCode()
    {
        int i = this.action.hashCode();
        i = 31 * i + (this.value != null ? this.value.hashCode() : 0);
        return i;
    }

    public enum Action
    {
        SHOW_TEXT("show_text", true),
        SHOW_ACHIEVEMENT("show_achievement", true),
        SHOW_ITEM("show_item", true),
        SHOW_ENTITY("show_entity", true);

        private static final Map<String, HoverEvent.Action> nameMapping = Maps.newHashMap();
        private final boolean allowedInChat;
        private final String canonicalName;

        Action(String canonicalNameIn, boolean allowedInChatIn)
        {
            this.canonicalName = canonicalNameIn;
            this.allowedInChat = allowedInChatIn;
        }

        public boolean shouldAllowInChat()
        {
            return this.allowedInChat;
        }

        public String getCanonicalName()
        {
            return this.canonicalName;
        }

        public static HoverEvent.Action getValueByCanonicalName(String canonicalNameIn)
        {
            return nameMapping.get(canonicalNameIn);
        }

        static {
            for (HoverEvent.Action hoverevent$action : values())
            {
                nameMapping.put(hoverevent$action.getCanonicalName(), hoverevent$action);
            }
        }
    }
}
