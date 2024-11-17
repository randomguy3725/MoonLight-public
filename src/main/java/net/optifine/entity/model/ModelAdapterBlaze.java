package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBlaze;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderBlaze;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.src.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterBlaze extends ModelAdapter
{
    public ModelAdapterBlaze()
    {
        super(EntityBlaze.class, "blaze", 0.5F);
    }

    public ModelBase makeModel()
    {
        return new ModelBlaze();
    }

    public ModelRenderer getModelRenderer(ModelBase model, String modelPart)
    {
        if (!(model instanceof ModelBlaze modelblaze))
        {
            return null;
        }
        else
        {

            if (modelPart.equals("head"))
            {
                return (ModelRenderer)Reflector.getFieldValue(modelblaze, Reflector.ModelBlaze_blazeHead);
            }
            else
            {
                String s = "stick";

                if (modelPart.startsWith(s))
                {
                    ModelRenderer[] amodelrenderer = (ModelRenderer[]) Reflector.getFieldValue(modelblaze, Reflector.ModelBlaze_blazeSticks);

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
        return new String[] {"head", "stick1", "stick2", "stick3", "stick4", "stick5", "stick6", "stick7", "stick8", "stick9", "stick10", "stick11", "stick12"};
    }

    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize)
    {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        RenderBlaze renderblaze = new RenderBlaze(rendermanager);
        renderblaze.mainModel = modelBase;
        renderblaze.shadowSize = shadowSize;
        return renderblaze;
    }
}
