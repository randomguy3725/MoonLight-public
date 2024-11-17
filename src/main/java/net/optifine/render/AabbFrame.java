package net.optifine.render;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.util.AxisAlignedBB;

public class AabbFrame extends AxisAlignedBB
{
    private int frameCount = -1;
    private boolean inFrustumFully = false;

    public AabbFrame(double x1, double y1, double z1, double x2, double y2, double z2)
    {
        super(x1, y1, z1, x2, y2, z2);
    }

    public boolean isBoundingBoxInFrustumFully(ICamera camera, int frameCount)
    {
        if (this.frameCount != frameCount)
        {
            this.inFrustumFully = camera instanceof Frustum && ((Frustum) camera).isBoxInFrustumFully(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
            this.frameCount = frameCount;
        }

        return this.inFrustumFully;
    }
}
