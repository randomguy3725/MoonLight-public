package net.minecraft.util;

public class ChatComponentText extends ChatComponentStyle
{
    private final String text;

    public ChatComponentText(String msg)
    {
        this.text = msg;
    }

    public String getChatComponentText_TextValue()
    {
        return this.text;
    }

    public String getUnformattedTextForChat()
    {
        return this.text;
    }

    public ChatComponentText createCopy()
    {
        ChatComponentText chatcomponenttext = new ChatComponentText(this.text);
        chatcomponenttext.setChatStyle(this.getChatStyle().createShallowCopy());

        for (IChatComponent ichatcomponent : this.getSiblings())
        {
            chatcomponenttext.appendSibling(ichatcomponent.createCopy());
        }

        return chatcomponenttext;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof ChatComponentText chatcomponenttext))
        {
            return false;
        }
        else
        {
            return this.text.equals(chatcomponenttext.getChatComponentText_TextValue()) && super.equals(p_equals_1_);
        }
    }

    public String toString()
    {
        return "TextComponent{text='" + this.text + '\'' + ", siblings=" + this.siblings + ", style=" + this.getChatStyle() + '}';
    }
}
