package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBat;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderBat;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityBat;
import net.optifine.reflect.Reflector;

public class ModelAdapterBat extends ModelAdapter
{
    public ModelAdapterBat()
    {
        super(EntityBat.class, "bat", 0.25F);
    }

    public ModelBase makeModel()
    {
        return new ModelBat();
    }

    public ModelRenderer getModelRenderer(ModelBase model, String modelPart)
    {
        if (!(model instanceof ModelBat modelbat))
        {
            return null;
        }
        else
        {
            return modelPart.equals("head") ? (ModelRenderer)Reflector.getFieldValue(modelbat, Reflector.ModelBat_ModelRenderers, 0) : (modelPart.equals("body") ? (ModelRenderer)Reflector.getFieldValue(modelbat, Reflector.ModelBat_ModelRenderers, 1) : (modelPart.equals("right_wing") ? (ModelRenderer)Reflector.getFieldValue(modelbat, Reflector.ModelBat_ModelRenderers, 2) : (modelPart.equals("left_wing") ? (ModelRenderer)Reflector.getFieldValue(modelbat, Reflector.ModelBat_ModelRenderers, 3) : (modelPart.equals("outer_right_wing") ? (ModelRenderer)Reflector.getFieldValue(modelbat, Reflector.ModelBat_ModelRenderers, 4) : (modelPart.equals("outer_left_wing") ? (ModelRenderer)Reflector.getFieldValue(modelbat, Reflector.ModelBat_ModelRenderers, 5) : null)))));
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"head", "body", "right_wing", "left_wing", "outer_right_wing", "outer_left_wing"};
    }

    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize)
    {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        RenderBat renderbat = new RenderBat(rendermanager);
        renderbat.mainModel = modelBase;
        renderbat.shadowSize = shadowSize;
        return renderbat;
    }
}
