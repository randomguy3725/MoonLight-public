package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelMagmaCube;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderMagmaCube;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.src.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterMagmaCube extends ModelAdapter
{
    public ModelAdapterMagmaCube()
    {
        super(EntityMagmaCube.class, "magma_cube", 0.5F);
    }

    public ModelBase makeModel()
    {
        return new ModelMagmaCube();
    }

    public ModelRenderer getModelRenderer(ModelBase model, String modelPart)
    {
        if (!(model instanceof ModelMagmaCube modelmagmacube))
        {
            return null;
        }
        else
        {

            if (modelPart.equals("core"))
            {
                return (ModelRenderer)Reflector.getFieldValue(modelmagmacube, Reflector.ModelMagmaCube_core);
            }
            else
            {
                String s = "segment";

                if (modelPart.startsWith(s))
                {
                    ModelRenderer[] amodelrenderer = (ModelRenderer[]) Reflector.getFieldValue(modelmagmacube, Reflector.ModelMagmaCube_segments);

                    if (amodelrenderer == null)
                    {
                        return null;
                    }
                    else
                    {
                        String s1 = modelPart.substring(s.length());
                        int i = Config.parseInt(s1, -1);
                        --i;
                        return i >= 0 && i < amodelrenderer.length ? amodelrenderer[i] : null;
                    }
                }
                else
                {
                    return null;
                }
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"core", "segment1", "segment2", "segment3", "segment4", "segment5", "segment6", "segment7", "segment8"};
    }

    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize)
    {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        RenderMagmaCube rendermagmacube = new RenderMagmaCube(rendermanager);
        rendermagmacube.mainModel = modelBase;
        rendermagmacube.shadowSize = shadowSize;
        return rendermagmacube;
    }
}
