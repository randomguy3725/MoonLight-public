package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelChicken;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderChicken;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityChicken;

public class ModelAdapterChicken extends ModelAdapter
{
    public ModelAdapterChicken()
    {
        super(EntityChicken.class, "chicken", 0.3F);
    }

    public ModelBase makeModel()
    {
        return new ModelChicken();
    }

    public ModelRenderer getModelRenderer(ModelBase model, String modelPart)
    {
        if (!(model instanceof ModelChicken modelchicken))
        {
            return null;
        }
        else
        {
            return modelPart.equals("head") ? modelchicken.head : (modelPart.equals("body") ? modelchicken.body : (modelPart.equals("right_leg") ? modelchicken.rightLeg : (modelPart.equals("left_leg") ? modelchicken.leftLeg : (modelPart.equals("right_wing") ? modelchicken.rightWing : (modelPart.equals("left_wing") ? modelchicken.leftWing : (modelPart.equals("bill") ? modelchicken.bill : (modelPart.equals("chin") ? modelchicken.chin : null)))))));
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"head", "body", "right_leg", "left_leg", "right_wing", "left_wing", "bill", "chin"};
    }

    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize)
    {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        RenderChicken renderchicken = new RenderChicken(rendermanager, modelBase, shadowSize);
        return renderchicken;
    }
}
