package net.minecraft.client.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public abstract class ModelBase
{
    public float swingProgress;
    public boolean isRiding;
    public boolean isChild = true;
    public List<ModelRenderer> boxList = Lists.newArrayList();
    private final Map<String, TextureOffset> modelTextureMap = Maps.newHashMap();
    public int textureWidth = 64;
    public int textureHeight = 32;

    public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale)
    {
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
    }

    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float p_78086_2_, float p_78086_3_, float partialTickTime)
    {
    }

    public ModelRenderer getRandomModelBox(Random rand)
    {
        return this.boxList.get(rand.nextInt(this.boxList.size()));
    }

    protected void setTextureOffset(String partName, int x, int y)
    {
        this.modelTextureMap.put(partName, new TextureOffset(x, y));
    }

    public TextureOffset getTextureOffset(String partName)
    {
        return this.modelTextureMap.get(partName);
    }

    public static void copyModelAngles(ModelRenderer source, ModelRenderer dest)
    {
        dest.rotateAngleX = source.rotateAngleX;
        dest.rotateAngleY = source.rotateAngleY;
        dest.rotateAngleZ = source.rotateAngleZ;
        dest.rotationPointX = source.rotationPointX;
        dest.rotationPointY = source.rotationPointY;
        dest.rotationPointZ = source.rotationPointZ;
    }

    public void setModelAttributes(ModelBase model)
    {
        this.swingProgress = model.swingProgress;
        this.isRiding = model.isRiding;
        this.isChild = model.isChild;
    }
}
