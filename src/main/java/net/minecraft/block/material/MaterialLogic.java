package net.minecraft.block.material;

public class MaterialLogic extends Material
{
    public MaterialLogic(MapColor color)
    {
        super(color);
        this.setAdventureModeExempt();
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
