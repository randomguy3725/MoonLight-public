package net.minecraft.client.model;

import net.minecraft.entity.Entity;

public class ModelSkeletonHead extends ModelBase
{
    public ModelRenderer skeletonHead;

    public ModelSkeletonHead()
    {
        this(0, 35, 64, 64);
    }

    public ModelSkeletonHead(int p_i1155_1_, int p_i1155_2_, int p_i1155_3_, int p_i1155_4_)
    {
        this.textureWidth = p_i1155_3_;
        this.textureHeight = p_i1155_4_;
        this.skeletonHead = new ModelRenderer(this, p_i1155_1_, p_i1155_2_);
        this.skeletonHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
        this.skeletonHead.setRotationPoint(0.0F, 0.0F, 0.0F);
    }

    public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale)
    {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entityIn);
        this.skeletonHead.render(scale);
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        this.skeletonHead.rotateAngleY = netHeadYaw / (180F / (float)Math.PI);
        this.skeletonHead.rotateAngleX = headPitch / (180F / (float)Math.PI);
    }
}
