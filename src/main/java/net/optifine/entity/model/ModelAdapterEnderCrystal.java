package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelEnderCrystal;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.RenderEnderCrystal;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.src.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterEnderCrystal extends ModelAdapter
{
    public ModelAdapterEnderCrystal()
    {
        this("end_crystal");
    }

    protected ModelAdapterEnderCrystal(String name)
    {
        super(EntityEnderCrystal.class, name, 0.5F);
    }

    public ModelBase makeModel()
    {
        return new ModelEnderCrystal(0.0F, true);
    }

    public ModelRenderer getModelRenderer(ModelBase model, String modelPart)
    {
        if (!(model instanceof ModelEnderCrystal modelendercrystal))
        {
            return null;
        }
        else
        {
            return modelPart.equals("cube") ? (ModelRenderer)Reflector.getFieldValue(modelendercrystal, Reflector.ModelEnderCrystal_ModelRenderers, 0) : (modelPart.equals("glass") ? (ModelRenderer)Reflector.getFieldValue(modelendercrystal, Reflector.ModelEnderCrystal_ModelRenderers, 1) : (modelPart.equals("base") ? (ModelRenderer)Reflector.getFieldValue(modelendercrystal, Reflector.ModelEnderCrystal_ModelRenderers, 2) : null));
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"cube", "glass", "base"};
    }

    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize)
    {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        Render render = rendermanager.getEntityRenderMap().get(EntityEnderCrystal.class);

        if (!(render instanceof RenderEnderCrystal renderendercrystal))
        {
            Config.warn("Not an instance of RenderEnderCrystal: " + render);
            return null;
        }
        else
        {

            if (!Reflector.RenderEnderCrystal_modelEnderCrystal.exists())
            {
                Config.warn("Field not found: RenderEnderCrystal.modelEnderCrystal");
                return null;
            }
            else
            {
                Reflector.setFieldValue(renderendercrystal, Reflector.RenderEnderCrystal_modelEnderCrystal, modelBase);
                renderendercrystal.shadowSize = shadowSize;
                return renderendercrystal;
            }
        }
    }
}
