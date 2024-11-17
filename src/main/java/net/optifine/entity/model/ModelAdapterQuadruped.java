package net.optifine.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.model.ModelRenderer;

public abstract class ModelAdapterQuadruped extends ModelAdapter
{
    public ModelAdapterQuadruped(Class entityClass, String name, float shadowSize)
    {
        super(entityClass, name, shadowSize);
    }

    public ModelRenderer getModelRenderer(ModelBase model, String modelPart)
    {
        if (!(model instanceof ModelQuadruped modelquadruped))
        {
            return null;
        }
        else
        {
            return modelPart.equals("head") ? modelquadruped.head : (modelPart.equals("body") ? modelquadruped.body : (modelPart.equals("leg1") ? modelquadruped.leg1 : (modelPart.equals("leg2") ? modelquadruped.leg2 : (modelPart.equals("leg3") ? modelquadruped.leg3 : (modelPart.equals("leg4") ? modelquadruped.leg4 : null)))));
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"head", "body", "leg1", "leg2", "leg3", "leg4"};
    }
}
