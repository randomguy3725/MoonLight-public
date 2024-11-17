package net.optifine.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityChestRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntityChest;
import net.optifine.reflect.Reflector;

public class ModelAdapterChest extends ModelAdapter
{
    public ModelAdapterChest()
    {
        super(TileEntityChest.class, "chest", 0.0F);
    }

    public ModelBase makeModel()
    {
        return new ModelChest();
    }

    public ModelRenderer getModelRenderer(ModelBase model, String modelPart)
    {
        if (!(model instanceof ModelChest modelchest))
        {
            return null;
        }
        else
        {
            return modelPart.equals("lid") ? modelchest.chestLid : (modelPart.equals("base") ? modelchest.chestBelow : (modelPart.equals("knob") ? modelchest.chestKnob : null));
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"lid", "base", "knob"};
    }

    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize)
    {
        TileEntityRendererDispatcher tileentityrendererdispatcher = TileEntityRendererDispatcher.instance;
        TileEntitySpecialRenderer tileentityspecialrenderer = tileentityrendererdispatcher.getSpecialRendererByClass(TileEntityChest.class);

        if (!(tileentityspecialrenderer instanceof TileEntityChestRenderer))
        {
            return null;
        }
        else
        {
            if (tileentityspecialrenderer.getEntityClass() == null)
            {
                tileentityspecialrenderer = new TileEntityChestRenderer();
                tileentityspecialrenderer.setRendererDispatcher(tileentityrendererdispatcher);
            }

            if (!Reflector.TileEntityChestRenderer_simpleChest.exists())
            {
                Config.warn("Field not found: TileEntityChestRenderer.simpleChest");
                return null;
            }
            else
            {
                Reflector.setFieldValue(tileentityspecialrenderer, Reflector.TileEntityChestRenderer_simpleChest, modelBase);
                return tileentityspecialrenderer;
            }
        }
    }
}
