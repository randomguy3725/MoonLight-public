package wtf.moonlight.features.modules.impl.visual;

import net.minecraft.block.BlockAir;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.render.Render3DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ColorValue;
import wtf.moonlight.utils.player.PlayerUtils;
import wtf.moonlight.utils.render.RenderUtils;

import java.awt.*;

@ModuleInfo(name = "BlockOverlay",category = ModuleCategory.Visual)
public class BlockOverlay extends Module {

    public final BoolValue outline = new BoolValue("Outline", true, this);
    public final BoolValue filled = new BoolValue("Filled", false, this);
    public final BoolValue syncColor = new BoolValue("Sync Color", false, this);
    public final ColorValue color = new ColorValue("Color",new Color(255,255,255),this ,() -> !syncColor.get());

    @EventTarget
    public void onRender3D(Render3DEvent event) {

        if(PlayerUtils.getBlock(mc.objectMouseOver.getBlockPos()) instanceof BlockAir)
            return;

        if (syncColor.get()) {
            RenderUtils.renderBlock(mc.objectMouseOver.getBlockPos(), getModule(Interface.class).color(0), outline.get(), filled.get());
        } else {
            RenderUtils.renderBlock(mc.objectMouseOver.getBlockPos(), color.get().getRGB(), outline.get(), filled.get());
        }

    }
}
