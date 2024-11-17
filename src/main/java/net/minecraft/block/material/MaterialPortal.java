package net.minecraft.block.material;

public class MaterialPortal extends Material
{
    public MaterialPortal(MapColor color)
    {
        super(color);
    }

    public boolean isSolid()
    {
        return false;
    }

    public boolean blocksLight()
    {
        return false;
    }

    public boolean blocksMovement()
    {
        return false;
    }
}
