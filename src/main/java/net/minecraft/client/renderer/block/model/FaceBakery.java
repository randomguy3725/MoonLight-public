package net.minecraft.client.renderer.block.model;

import net.minecraftforge.client.model.ITransformation;
import net.optifine.model.BlockModelUtils;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;
import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.src.Config;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3i;

public class FaceBakery
{
    private static final float SCALE_ROTATION_22_5 = 1.0F / (float)Math.cos(0.39269909262657166D) - 1.0F;
    private static final float SCALE_ROTATION_GENERAL = 1.0F / (float)Math.cos((Math.PI / 4D)) - 1.0F;

    public BakedQuad makeBakedQuad(Vector3f posFrom, Vector3f posTo, BlockPartFace face, TextureAtlasSprite sprite, EnumFacing facing, ModelRotation modelRotationIn, BlockPartRotation partRotation, boolean uvLocked, boolean shade)
    {
        return this.makeBakedQuad(posFrom, posTo, face, sprite, facing, (ITransformation) modelRotationIn, partRotation, uvLocked, shade);
    }

    public BakedQuad makeBakedQuad(Vector3f p_makeBakedQuad_1_, Vector3f p_makeBakedQuad_2_, BlockPartFace p_makeBakedQuad_3_, TextureAtlasSprite p_makeBakedQuad_4_, EnumFacing p_makeBakedQuad_5_, ITransformation p_makeBakedQuad_6_, BlockPartRotation p_makeBakedQuad_7_, boolean p_makeBakedQuad_8_, boolean p_makeBakedQuad_9_)
    {
        int[] aint = this.makeQuadVertexData(p_makeBakedQuad_3_, p_makeBakedQuad_4_, p_makeBakedQuad_5_, this.getPositionsDiv16(p_makeBakedQuad_1_, p_makeBakedQuad_2_), p_makeBakedQuad_6_, p_makeBakedQuad_7_, p_makeBakedQuad_8_, p_makeBakedQuad_9_);
        EnumFacing enumfacing = getFacingFromVertexData(aint);

        if (p_makeBakedQuad_8_)
        {
            this.lockUv(aint, enumfacing, p_makeBakedQuad_3_.blockFaceUV, p_makeBakedQuad_4_);
        }

        if (p_makeBakedQuad_7_ == null)
        {
            this.applyFacing(aint, enumfacing);
        }

        if (Reflector.ForgeHooksClient_fillNormal.exists())
        {
            Reflector.call(Reflector.ForgeHooksClient_fillNormal, aint, enumfacing);
        }

        return new BakedQuad(aint, p_makeBakedQuad_3_.tintIndex, enumfacing);
    }

    private int[] makeQuadVertexData(BlockPartFace p_makeQuadVertexData_1_, TextureAtlasSprite p_makeQuadVertexData_2_, EnumFacing p_makeQuadVertexData_3_, float[] p_makeQuadVertexData_4_, ITransformation p_makeQuadVertexData_5_, BlockPartRotation p_makeQuadVertexData_6_, boolean p_makeQuadVertexData_7_, boolean p_makeQuadVertexData_8_)
    {
        int i = 28;

        if (Config.isShaders())
        {
            i = 56;
        }

        int[] aint = new int[i];

        for (int j = 0; j < 4; ++j)
        {
            this.fillVertexData(aint, j, p_makeQuadVertexData_3_, p_makeQuadVertexData_1_, p_makeQuadVertexData_4_, p_makeQuadVertexData_2_, p_makeQuadVertexData_5_, p_makeQuadVertexData_6_, p_makeQuadVertexData_7_, p_makeQuadVertexData_8_);
        }

        return aint;
    }

    private int getFaceShadeColor(EnumFacing facing)
    {
        float f = getFaceBrightness(facing);
        int i = MathHelper.clamp_int((int)(f * 255.0F), 0, 255);
        return -16777216 | i << 16 | i << 8 | i;
    }

    public static float getFaceBrightness(EnumFacing p_178412_0_)
    {
        switch (p_178412_0_)
        {
            case DOWN:
                if (Config.isShaders())
                {
                    return Shaders.blockLightLevel05;
                }

                return 0.5F;

            case UP:
                return 1.0F;

            case NORTH:
            case SOUTH:
                if (Config.isShaders())
                {
                    return Shaders.blockLightLevel08;
                }

                return 0.8F;

            case WEST:
            case EAST:
                if (Config.isShaders())
                {
                    return Shaders.blockLightLevel06;
                }

                return 0.6F;

            default:
                return 1.0F;
        }
    }

    private float[] getPositionsDiv16(Vector3f pos1, Vector3f pos2)
    {
        float[] afloat = new float[EnumFacing.values().length];
        afloat[EnumFaceDirection.Constants.WEST_INDEX] = pos1.x / 16.0F;
        afloat[EnumFaceDirection.Constants.DOWN_INDEX] = pos1.y / 16.0F;
        afloat[EnumFaceDirection.Constants.NORTH_INDEX] = pos1.z / 16.0F;
        afloat[EnumFaceDirection.Constants.EAST_INDEX] = pos2.x / 16.0F;
        afloat[EnumFaceDirection.Constants.UP_INDEX] = pos2.y / 16.0F;
        afloat[EnumFaceDirection.Constants.SOUTH_INDEX] = pos2.z / 16.0F;
        return afloat;
    }

    private void fillVertexData(int[] p_fillVertexData_1_, int p_fillVertexData_2_, EnumFacing p_fillVertexData_3_, BlockPartFace p_fillVertexData_4_, float[] p_fillVertexData_5_, TextureAtlasSprite p_fillVertexData_6_, ITransformation p_fillVertexData_7_, BlockPartRotation p_fillVertexData_8_, boolean p_fillVertexData_9_, boolean p_fillVertexData_10_)
    {
        EnumFacing enumfacing = p_fillVertexData_7_.rotate(p_fillVertexData_3_);
        int i = p_fillVertexData_10_ ? this.getFaceShadeColor(enumfacing) : -1;
        EnumFaceDirection.VertexInformation enumfacedirection$vertexinformation = EnumFaceDirection.getFacing(p_fillVertexData_3_).getVertexInformation(p_fillVertexData_2_);
        Vector3f vector3f = new Vector3f(p_fillVertexData_5_[enumfacedirection$vertexinformation.xIndex], p_fillVertexData_5_[enumfacedirection$vertexinformation.yIndex], p_fillVertexData_5_[enumfacedirection$vertexinformation.zIndex]);
        this.rotatePart(vector3f, p_fillVertexData_8_);
        int j = this.rotateVertex(vector3f, p_fillVertexData_3_, p_fillVertexData_2_, p_fillVertexData_7_, p_fillVertexData_9_);
        BlockModelUtils.snapVertexPosition(vector3f);
        this.storeVertexData(p_fillVertexData_1_, j, p_fillVertexData_2_, vector3f, i, p_fillVertexData_6_, p_fillVertexData_4_.blockFaceUV);
    }

    private void storeVertexData(int[] faceData, int storeIndex, int vertexIndex, Vector3f position, int shadeColor, TextureAtlasSprite sprite, BlockFaceUV faceUV)
    {
        int i = faceData.length / 4;
        int j = storeIndex * i;
        faceData[j] = Float.floatToRawIntBits(position.x);
        faceData[j + 1] = Float.floatToRawIntBits(position.y);
        faceData[j + 2] = Float.floatToRawIntBits(position.z);
        faceData[j + 3] = shadeColor;
        faceData[j + 4] = Float.floatToRawIntBits(sprite.getInterpolatedU((double)faceUV.func_178348_a(vertexIndex) * 0.999D + (double)faceUV.func_178348_a((vertexIndex + 2) % 4) * 0.001D));
        faceData[j + 4 + 1] = Float.floatToRawIntBits(sprite.getInterpolatedV((double)faceUV.func_178346_b(vertexIndex) * 0.999D + (double)faceUV.func_178346_b((vertexIndex + 2) % 4) * 0.001D));
    }

    private void rotatePart(Vector3f p_178407_1_, BlockPartRotation partRotation)
    {
        if (partRotation != null)
        {
            Matrix4f matrix4f = this.getMatrixIdentity();
            Vector3f vector3f = new Vector3f(0.0F, 0.0F, 0.0F);

            switch (partRotation.axis)
            {
                case X:
                    Matrix4f.rotate(partRotation.angle * 0.017453292F, new Vector3f(1.0F, 0.0F, 0.0F), matrix4f, matrix4f);
                    vector3f.set(0.0F, 1.0F, 1.0F);
                    break;

                case Y:
                    Matrix4f.rotate(partRotation.angle * 0.017453292F, new Vector3f(0.0F, 1.0F, 0.0F), matrix4f, matrix4f);
                    vector3f.set(1.0F, 0.0F, 1.0F);
                    break;

                case Z:
                    Matrix4f.rotate(partRotation.angle * 0.017453292F, new Vector3f(0.0F, 0.0F, 1.0F), matrix4f, matrix4f);
                    vector3f.set(1.0F, 1.0F, 0.0F);
            }

            if (partRotation.rescale)
            {
                if (Math.abs(partRotation.angle) == 22.5F)
                {
                    vector3f.scale(SCALE_ROTATION_22_5);
                }
                else
                {
                    vector3f.scale(SCALE_ROTATION_GENERAL);
                }

                Vector3f.add(vector3f, new Vector3f(1.0F, 1.0F, 1.0F), vector3f);
            }
            else
            {
                vector3f.set(1.0F, 1.0F, 1.0F);
            }

            this.rotateScale(p_178407_1_, new Vector3f(partRotation.origin), matrix4f, vector3f);
        }
    }

    public int rotateVertex(Vector3f position, EnumFacing facing, int vertexIndex, ModelRotation modelRotationIn, boolean uvLocked)
    {
        return this.rotateVertex(position, facing, vertexIndex, modelRotationIn, uvLocked);
    }

    public int rotateVertex(Vector3f p_rotateVertex_1_, EnumFacing p_rotateVertex_2_, int p_rotateVertex_3_, ITransformation p_rotateVertex_4_, boolean p_rotateVertex_5_)
    {
        if (p_rotateVertex_4_ == ModelRotation.X0_Y0)
        {
            return p_rotateVertex_3_;
        }
        else
        {
            if (Reflector.ForgeHooksClient_transform.exists())
            {
                Reflector.call(Reflector.ForgeHooksClient_transform, p_rotateVertex_1_, p_rotateVertex_4_.getMatrix());
            }
            else
            {
                this.rotateScale(p_rotateVertex_1_, new Vector3f(0.5F, 0.5F, 0.5F), ((ModelRotation)p_rotateVertex_4_).getMatrix4d(), new Vector3f(1.0F, 1.0F, 1.0F));
            }

            return p_rotateVertex_4_.rotate(p_rotateVertex_2_, p_rotateVertex_3_);
        }
    }

    private void rotateScale(Vector3f position, Vector3f rotationOrigin, Matrix4f rotationMatrix, Vector3f scale)
    {
        Vector4f vector4f = new Vector4f(position.x - rotationOrigin.x, position.y - rotationOrigin.y, position.z - rotationOrigin.z, 1.0F);
        Matrix4f.transform(rotationMatrix, vector4f, vector4f);
        vector4f.x *= scale.x;
        vector4f.y *= scale.y;
        vector4f.z *= scale.z;
        position.set(vector4f.x + rotationOrigin.x, vector4f.y + rotationOrigin.y, vector4f.z + rotationOrigin.z);
    }

    private Matrix4f getMatrixIdentity()
    {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.setIdentity();
        return matrix4f;
    }

    public static EnumFacing getFacingFromVertexData(int[] faceData)
    {
        int i = faceData.length / 4;
        int j = i * 2;
        int k = i * 3;
        Vector3f vector3f = new Vector3f(Float.intBitsToFloat(faceData[0]), Float.intBitsToFloat(faceData[1]), Float.intBitsToFloat(faceData[2]));
        Vector3f vector3f1 = new Vector3f(Float.intBitsToFloat(faceData[i]), Float.intBitsToFloat(faceData[i + 1]), Float.intBitsToFloat(faceData[i + 2]));
        Vector3f vector3f2 = new Vector3f(Float.intBitsToFloat(faceData[j]), Float.intBitsToFloat(faceData[j + 1]), Float.intBitsToFloat(faceData[j + 2]));
        Vector3f vector3f3 = new Vector3f();
        Vector3f vector3f4 = new Vector3f();
        Vector3f vector3f5 = new Vector3f();
        Vector3f.sub(vector3f, vector3f1, vector3f3);
        Vector3f.sub(vector3f2, vector3f1, vector3f4);
        Vector3f.cross(vector3f4, vector3f3, vector3f5);
        float f = (float)Math.sqrt(vector3f5.x * vector3f5.x + vector3f5.y * vector3f5.y + vector3f5.z * vector3f5.z);
        vector3f5.x /= f;
        vector3f5.y /= f;
        vector3f5.z /= f;
        EnumFacing enumfacing = null;
        float f1 = 0.0F;

        for (EnumFacing enumfacing1 : EnumFacing.values())
        {
            Vec3i vec3i = enumfacing1.getDirectionVec();
            Vector3f vector3f6 = new Vector3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
            float f2 = Vector3f.dot(vector3f5, vector3f6);

            if (f2 >= 0.0F && f2 > f1)
            {
                f1 = f2;
                enumfacing = enumfacing1;
            }
        }

        if (enumfacing == null)
        {
            return EnumFacing.UP;
        }
        else
        {
            return enumfacing;
        }
    }

    public void lockUv(int[] p_178409_1_, EnumFacing facing, BlockFaceUV p_178409_3_, TextureAtlasSprite p_178409_4_)
    {
        for (int i = 0; i < 4; ++i)
        {
            this.lockVertexUv(i, p_178409_1_, facing, p_178409_3_, p_178409_4_);
        }
    }

    private void applyFacing(int[] p_178408_1_, EnumFacing p_178408_2_)
    {
        int[] aint = new int[p_178408_1_.length];
        System.arraycopy(p_178408_1_, 0, aint, 0, p_178408_1_.length);
        float[] afloat = new float[EnumFacing.values().length];
        afloat[EnumFaceDirection.Constants.WEST_INDEX] = 999.0F;
        afloat[EnumFaceDirection.Constants.DOWN_INDEX] = 999.0F;
        afloat[EnumFaceDirection.Constants.NORTH_INDEX] = 999.0F;
        afloat[EnumFaceDirection.Constants.EAST_INDEX] = -999.0F;
        afloat[EnumFaceDirection.Constants.UP_INDEX] = -999.0F;
        afloat[EnumFaceDirection.Constants.SOUTH_INDEX] = -999.0F;
        int i = p_178408_1_.length / 4;

        for (int j = 0; j < 4; ++j)
        {
            int k = i * j;
            float f = Float.intBitsToFloat(aint[k]);
            float f1 = Float.intBitsToFloat(aint[k + 1]);
            float f2 = Float.intBitsToFloat(aint[k + 2]);

            if (f < afloat[EnumFaceDirection.Constants.WEST_INDEX])
            {
                afloat[EnumFaceDirection.Constants.WEST_INDEX] = f;
            }

            if (f1 < afloat[EnumFaceDirection.Constants.DOWN_INDEX])
            {
                afloat[EnumFaceDirection.Constants.DOWN_INDEX] = f1;
            }

            if (f2 < afloat[EnumFaceDirection.Constants.NORTH_INDEX])
            {
                afloat[EnumFaceDirection.Constants.NORTH_INDEX] = f2;
            }

            if (f > afloat[EnumFaceDirection.Constants.EAST_INDEX])
            {
                afloat[EnumFaceDirection.Constants.EAST_INDEX] = f;
            }

            if (f1 > afloat[EnumFaceDirection.Constants.UP_INDEX])
            {
                afloat[EnumFaceDirection.Constants.UP_INDEX] = f1;
            }

            if (f2 > afloat[EnumFaceDirection.Constants.SOUTH_INDEX])
            {
                afloat[EnumFaceDirection.Constants.SOUTH_INDEX] = f2;
            }
        }

        EnumFaceDirection enumfacedirection = EnumFaceDirection.getFacing(p_178408_2_);

        for (int j1 = 0; j1 < 4; ++j1)
        {
            int k1 = i * j1;
            EnumFaceDirection.VertexInformation enumfacedirection$vertexinformation = enumfacedirection.getVertexInformation(j1);
            float f8 = afloat[enumfacedirection$vertexinformation.xIndex];
            float f3 = afloat[enumfacedirection$vertexinformation.yIndex];
            float f4 = afloat[enumfacedirection$vertexinformation.zIndex];
            p_178408_1_[k1] = Float.floatToRawIntBits(f8);
            p_178408_1_[k1 + 1] = Float.floatToRawIntBits(f3);
            p_178408_1_[k1 + 2] = Float.floatToRawIntBits(f4);

            for (int l = 0; l < 4; ++l)
            {
                int i1 = i * l;
                float f5 = Float.intBitsToFloat(aint[i1]);
                float f6 = Float.intBitsToFloat(aint[i1 + 1]);
                float f7 = Float.intBitsToFloat(aint[i1 + 2]);

                if (MathHelper.epsilonEquals(f8, f5) && MathHelper.epsilonEquals(f3, f6) && MathHelper.epsilonEquals(f4, f7))
                {
                    p_178408_1_[k1 + 4] = aint[i1 + 4];
                    p_178408_1_[k1 + 4 + 1] = aint[i1 + 4 + 1];
                }
            }
        }
    }

    private void lockVertexUv(int p_178401_1_, int[] p_178401_2_, EnumFacing facing, BlockFaceUV p_178401_4_, TextureAtlasSprite p_178401_5_)
    {
        int i = p_178401_2_.length / 4;
        int j = i * p_178401_1_;
        float f = Float.intBitsToFloat(p_178401_2_[j]);
        float f1 = Float.intBitsToFloat(p_178401_2_[j + 1]);
        float f2 = Float.intBitsToFloat(p_178401_2_[j + 2]);

        if (f < -0.1F || f >= 1.1F)
        {
            f -= (float)MathHelper.floor_float(f);
        }

        if (f1 < -0.1F || f1 >= 1.1F)
        {
            f1 -= (float)MathHelper.floor_float(f1);
        }

        if (f2 < -0.1F || f2 >= 1.1F)
        {
            f2 -= (float)MathHelper.floor_float(f2);
        }

        float f3 = 0.0F;
        float f4 = 0.0F;

        switch (facing)
        {
            case DOWN:
                f3 = f * 16.0F;
                f4 = (1.0F - f2) * 16.0F;
                break;

            case UP:
                f3 = f * 16.0F;
                f4 = f2 * 16.0F;
                break;

            case NORTH:
                f3 = (1.0F - f) * 16.0F;
                f4 = (1.0F - f1) * 16.0F;
                break;

            case SOUTH:
                f3 = f * 16.0F;
                f4 = (1.0F - f1) * 16.0F;
                break;

            case WEST:
                f3 = f2 * 16.0F;
                f4 = (1.0F - f1) * 16.0F;
                break;

            case EAST:
                f3 = (1.0F - f2) * 16.0F;
                f4 = (1.0F - f1) * 16.0F;
        }

        int k = p_178401_4_.func_178345_c(p_178401_1_) * i;
        p_178401_2_[k + 4] = Float.floatToRawIntBits(p_178401_5_.getInterpolatedU(f3));
        p_178401_2_[k + 4 + 1] = Float.floatToRawIntBits(p_178401_5_.getInterpolatedV(f4));
    }
}
