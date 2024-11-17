package net.optifine.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

public abstract class ModelAdapterBiped extends ModelAdapter
{
    public ModelAdapterBiped(Class entityClass, String name, float shadowSize)
    {
        super(entityClass, name, shadowSize);
    }

    public ModelRenderer getModelRenderer(ModelBase model, String modelPart)
    {
        if (!(model instanceof ModelBiped modelbiped))
        {
            return null;
        }
        else
        {
            return modelPart.equals("head") ? modelbiped.bipedHead : (modelPart.equals("headwear") ? modelbiped.bipedHeadwear : (modelPart.equals("body") ? modelbiped.bipedBody : (modelPart.equals("left_arm") ? modelbiped.bipedLeftArm : (modelPart.equals("right_arm") ? modelbiped.bipedRightArm : (modelPart.equals("left_leg") ? modelbiped.bipedLeftLeg : (modelPart.equals("right_leg") ? modelbiped.bipedRightLeg : null))))));
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"head", "headwear", "body", "left_arm", "right_arm", "left_leg", "right_leg"};
    }
}
