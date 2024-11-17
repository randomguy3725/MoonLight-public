package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelWitch;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderWitch;
import net.minecraft.entity.monster.EntityWitch;
import net.optifine.reflect.Reflector;

public class ModelAdapterWitch extends ModelAdapter
{
    public ModelAdapterWitch()
    {
        super(EntityWitch.class, "witch", 0.5F);
    }

    public ModelBase makeModel()
    {
        return new ModelWitch(0.0F);
    }

    public ModelRenderer getModelRenderer(ModelBase model, String modelPart)
    {
        if (!(model instanceof ModelWitch modelwitch))
        {
            return null;
        }
        else
        {
            return modelPart.equals("mole") ? (ModelRenderer)Reflector.getFieldValue(modelwitch, Reflector.ModelWitch_mole) : (modelPart.equals("hat") ? (ModelRenderer)Reflector.getFieldValue(modelwitch, Reflector.ModelWitch_hat) : (modelPart.equals("head") ? modelwitch.villagerHead : (modelPart.equals("body") ? modelwitch.villagerBody : (modelPart.equals("arms") ? modelwitch.villagerArms : (modelPart.equals("left_leg") ? modelwitch.leftVillagerLeg : (modelPart.equals("right_leg") ? modelwitch.rightVillagerLeg : (modelPart.equals("nose") ? modelwitch.villagerNose : null)))))));
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"mole", "head", "body", "arms", "right_leg", "left_leg", "nose"};
    }

    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize)
    {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        RenderWitch renderwitch = new RenderWitch(rendermanager);
        renderwitch.mainModel = modelBase;
        renderwitch.shadowSize = shadowSize;
        return renderwitch;
    }
}
