package net.minecraft.client.renderer.culling;

import net.minecraft.util.AxisAlignedBB;

public class Frustum implements ICamera
{
    private final ClippingHelper clippingHelper;
    private double xPosition;
    private double yPosition;
    private double zPosition;

    public Frustum()
    {
        this(ClippingHelperImpl.getInstance());
    }

    public Frustum(ClippingHelper p_i46196_1_)
    {
        this.clippingHelper = p_i46196_1_;
    }
    public Frustum(double p_78547_1_, double p_78547_3_, double p_78547_5_) {
        this(ClippingHelperImpl.getInstance());
        this.xPosition = p_78547_1_;
        this.yPosition = p_78547_3_;
        this.zPosition = p_78547_5_;
    }

    public void setPosition(double p_78547_1_, double p_78547_3_, double p_78547_5_)
    {
        this.xPosition = p_78547_1_;
        this.yPosition = p_78547_3_;
        this.zPosition = p_78547_5_;
    }

    public boolean isBoxInFrustum(double p_78548_1_, double p_78548_3_, double p_78548_5_, double p_78548_7_, double p_78548_9_, double p_78548_11_)
    {
        return this.clippingHelper.isBoxInFrustum(p_78548_1_ - this.xPosition, p_78548_3_ - this.yPosition, p_78548_5_ - this.zPosition, p_78548_7_ - this.xPosition, p_78548_9_ - this.yPosition, p_78548_11_ - this.zPosition);
    }

    public boolean isBoundingBoxInFrustum(AxisAlignedBB p_78546_1_)
    {
        return this.isBoxInFrustum(p_78546_1_.minX, p_78546_1_.minY, p_78546_1_.minZ, p_78546_1_.maxX, p_78546_1_.maxY, p_78546_1_.maxZ);
    }

    public boolean isBoxInFrustumFully(double p_isBoxInFrustumFully_1_, double p_isBoxInFrustumFully_3_, double p_isBoxInFrustumFully_5_, double p_isBoxInFrustumFully_7_, double p_isBoxInFrustumFully_9_, double p_isBoxInFrustumFully_11_)
    {
        return this.clippingHelper.isBoxInFrustumFully(p_isBoxInFrustumFully_1_ - this.xPosition, p_isBoxInFrustumFully_3_ - this.yPosition, p_isBoxInFrustumFully_5_ - this.zPosition, p_isBoxInFrustumFully_7_ - this.xPosition, p_isBoxInFrustumFully_9_ - this.yPosition, p_isBoxInFrustumFully_11_ - this.zPosition);
    }
}
