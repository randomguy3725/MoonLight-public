package net.minecraft.command;

public class EntityNotFoundException extends CommandException
{
    public EntityNotFoundException()
    {
        this("commands.generic.entity.notFound");
    }

    public EntityNotFoundException(String p_i46035_1_, Object... p_i46035_2_)
    {
        super(p_i46035_1_, p_i46035_2_);
    }
}
