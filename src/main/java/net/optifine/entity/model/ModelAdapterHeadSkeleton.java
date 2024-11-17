package net.optifine.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntitySkull;
import net.optifine.reflect.Reflector;

public class ModelAdapterHeadSkeleton extends ModelAdapter
{
    public ModelAdapterHeadSkeleton()
    {
        super(TileEntitySkull.class, "head_skeleton", 0.0F);
    }

    public ModelBase makeModel()
    {
        return new ModelSkeletonHead(0, 0, 64, 32);
    }

    public ModelRenderer getModelRenderer(ModelBase model, String modelPart)
    {
        if (!(model instanceof ModelSkeletonHead modelskeletonhead))
        {
            return null;
        }
        else
        {
            return modelPart.equals("head") ? modelskeletonhead.skeletonHead : null;
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"head"};
    }

    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize)
    {
        TileEntityRendererDispatcher tileentityrendererdispatcher = TileEntityRendererDispatcher.instance;
        TileEntitySpecialRenderer tileentityspecialrenderer = tileentityrendererdispatcher.getSpecialRendererByClass(TileEntitySkull.class);

        if (!(tileentityspecialrenderer instanceof TileEntitySkullRenderer))
        {
            return null;
        }
        else
        {
            if (tileentityspecialrenderer.getEntityClass() == null)
            {
                tileentityspecialrenderer = new TileEntitySkullRenderer();
                tileentityspecialrenderer.setRendererDispatcher(tileentityrendererdispatcher);
            }

            if (!Reflector.TileEntitySkullRenderer_humanoidHead.exists())
            {
                Config.warn("Field not found: TileEntitySkullRenderer.humanoidHead");
                return null;
            }
            else
            {
                Reflector.setFieldValue(tileentityspecialrenderer, Reflector.TileEntitySkullRenderer_humanoidHead, modelBase);
                return tileentityspecialrenderer;
            }
        }
    }
}
