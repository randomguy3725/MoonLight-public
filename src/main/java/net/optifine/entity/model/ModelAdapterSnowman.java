package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSnowMan;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowMan;
import net.minecraft.entity.monster.EntitySnowman;

public class ModelAdapterSnowman extends ModelAdapter
{
    public ModelAdapterSnowman()
    {
        super(EntitySnowman.class, "snow_golem", 0.5F);
    }

    public ModelBase makeModel()
    {
        return new ModelSnowMan();
    }

    public ModelRenderer getModelRenderer(ModelBase model, String modelPart)
    {
        if (!(model instanceof ModelSnowMan modelsnowman))
        {
            return null;
        }
        else
        {
            return modelPart.equals("body") ? modelsnowman.body : (modelPart.equals("body_bottom") ? modelsnowman.bottomBody : (modelPart.equals("head") ? modelsnowman.head : (modelPart.equals("left_hand") ? modelsnowman.leftHand : (modelPart.equals("right_hand") ? modelsnowman.rightHand : null))));
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"body", "body_bottom", "head", "right_hand", "left_hand"};
    }

    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize)
    {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        RenderSnowMan rendersnowman = new RenderSnowMan(rendermanager);
        rendersnowman.mainModel = modelBase;
        rendersnowman.shadowSize = shadowSize;
        return rendersnowman;
    }
}
