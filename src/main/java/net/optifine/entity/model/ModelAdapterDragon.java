package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelDragon;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderDragon;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.boss.EntityDragon;
import net.optifine.reflect.Reflector;

public class ModelAdapterDragon extends ModelAdapter
{
    public ModelAdapterDragon()
    {
        super(EntityDragon.class, "dragon", 0.5F);
    }

    public ModelBase makeModel()
    {
        return new ModelDragon(0.0F);
    }

    public ModelRenderer getModelRenderer(ModelBase model, String modelPart)
    {
        if (!(model instanceof ModelDragon modeldragon))
        {
            return null;
        }
        else
        {
            return modelPart.equals("head") ? (ModelRenderer)Reflector.getFieldValue(modeldragon, Reflector.ModelDragon_ModelRenderers, 0) : (modelPart.equals("spine") ? (ModelRenderer)Reflector.getFieldValue(modeldragon, Reflector.ModelDragon_ModelRenderers, 1) : (modelPart.equals("jaw") ? (ModelRenderer)Reflector.getFieldValue(modeldragon, Reflector.ModelDragon_ModelRenderers, 2) : (modelPart.equals("body") ? (ModelRenderer)Reflector.getFieldValue(modeldragon, Reflector.ModelDragon_ModelRenderers, 3) : (modelPart.equals("rear_leg") ? (ModelRenderer)Reflector.getFieldValue(modeldragon, Reflector.ModelDragon_ModelRenderers, 4) : (modelPart.equals("front_leg") ? (ModelRenderer)Reflector.getFieldValue(modeldragon, Reflector.ModelDragon_ModelRenderers, 5) : (modelPart.equals("rear_leg_tip") ? (ModelRenderer)Reflector.getFieldValue(modeldragon, Reflector.ModelDragon_ModelRenderers, 6) : (modelPart.equals("front_leg_tip") ? (ModelRenderer)Reflector.getFieldValue(modeldragon, Reflector.ModelDragon_ModelRenderers, 7) : (modelPart.equals("rear_foot") ? (ModelRenderer)Reflector.getFieldValue(modeldragon, Reflector.ModelDragon_ModelRenderers, 8) : (modelPart.equals("front_foot") ? (ModelRenderer)Reflector.getFieldValue(modeldragon, Reflector.ModelDragon_ModelRenderers, 9) : (modelPart.equals("wing") ? (ModelRenderer)Reflector.getFieldValue(modeldragon, Reflector.ModelDragon_ModelRenderers, 10) : (modelPart.equals("wing_tip") ? (ModelRenderer)Reflector.getFieldValue(modeldragon, Reflector.ModelDragon_ModelRenderers, 11) : null)))))))))));
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"head", "spine", "jaw", "body", "rear_leg", "front_leg", "rear_leg_tip", "front_leg_tip", "rear_foot", "front_foot", "wing", "wing_tip"};
    }

    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize)
    {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        RenderDragon renderdragon = new RenderDragon(rendermanager);
        renderdragon.mainModel = modelBase;
        renderdragon.shadowSize = shadowSize;
        return renderdragon;
    }
}
