package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.src.Config;
import net.optifine.SmartAnimations;
import net.optifine.render.GlAlphaState;
import net.optifine.render.GlBlendState;
import net.optifine.shaders.Shaders;
import net.optifine.util.LockCounter;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;

public class GlStateManager
{
    private static final GlStateManager.AlphaState alphaState = new GlStateManager.AlphaState();
    private static final GlStateManager.BooleanState lightingState = new GlStateManager.BooleanState(2896);
    private static final GlStateManager.BooleanState[] lightState = new GlStateManager.BooleanState[8];
    private static final GlStateManager.ColorMaterialState colorMaterialState = new GlStateManager.ColorMaterialState();
    private static final GlStateManager.BlendState blendState = new GlStateManager.BlendState();
    private static final GlStateManager.DepthState depthState = new GlStateManager.DepthState();
    private static final GlStateManager.FogState fogState = new GlStateManager.FogState();
    private static final GlStateManager.CullState cullState = new GlStateManager.CullState();
    private static final GlStateManager.PolygonOffsetState polygonOffsetState = new GlStateManager.PolygonOffsetState();
    private static final GlStateManager.ColorLogicState colorLogicState = new GlStateManager.ColorLogicState();
    private static final GlStateManager.TexGenState texGenState = new GlStateManager.TexGenState();
    private static final GlStateManager.ClearState clearState = new GlStateManager.ClearState();
    private static final GlStateManager.StencilState stencilState = new GlStateManager.StencilState();
    private static final GlStateManager.BooleanState normalizeState = new GlStateManager.BooleanState(2977);
    private static int activeTextureUnit = 0;
    private static final GlStateManager.TextureState[] textureState = new GlStateManager.TextureState[32];
    private static int activeShadeModel = 7425;
    private static final GlStateManager.BooleanState rescaleNormalState = new GlStateManager.BooleanState(32826);
    private static final GlStateManager.ColorMask colorMaskState = new GlStateManager.ColorMask();
    private static final GlStateManager.Color colorState = new GlStateManager.Color();
    public static boolean clearEnabled = true;
    private static final LockCounter alphaLock = new LockCounter();
    private static final GlAlphaState alphaLockState = new GlAlphaState();
    private static final LockCounter blendLock = new LockCounter();
    private static final GlBlendState blendLockState = new GlBlendState();
    private static boolean creatingDisplayList = false;

    public static void pushAttrib()
    {
        GL11.glPushAttrib(8256);
    }

    public static void popAttrib()
    {
        GL11.glPopAttrib();
    }

    public static void disableAlpha()
    {
        if (alphaLock.isLocked())
        {
            alphaLockState.setDisabled();
        }
        else
        {
            alphaState.alphaTest.setDisabled();
        }
    }

    public static void enableAlpha()
    {
        if (alphaLock.isLocked())
        {
            alphaLockState.setEnabled();
        }
        else
        {
            alphaState.alphaTest.setEnabled();
        }
    }

    public static void alphaFunc(int func, float ref)
    {
        if (alphaLock.isLocked())
        {
            alphaLockState.setFuncRef(func, ref);
        }
        else
        {
            if (func != alphaState.func || ref != alphaState.ref)
            {
                alphaState.func = func;
                alphaState.ref = ref;
                GL11.glAlphaFunc(func, ref);
            }
        }
    }

    public static void enableLighting()
    {
        lightingState.setEnabled();
    }

    public static void disableLighting()
    {
        lightingState.setDisabled();
    }

    public static void enableLight(int light)
    {
        lightState[light].setEnabled();
    }

    public static void disableLight(int light)
    {
        lightState[light].setDisabled();
    }

    public static void enableColorMaterial()
    {
        colorMaterialState.colorMaterial.setEnabled();
    }

    public static void disableColorMaterial()
    {
        colorMaterialState.colorMaterial.setDisabled();
    }

    public static void colorMaterial(int face, int mode)
    {
        if (face != colorMaterialState.face || mode != colorMaterialState.mode)
        {
            colorMaterialState.face = face;
            colorMaterialState.mode = mode;
            GL11.glColorMaterial(face, mode);
        }
    }

    public static void disableDepth()
    {
        depthState.depthTest.setDisabled();
    }

    public static void enableDepth()
    {
        depthState.depthTest.setEnabled();
    }

    public static void depthFunc(int depthFunc)
    {
        if (depthFunc != depthState.depthFunc)
        {
            depthState.depthFunc = depthFunc;
            GL11.glDepthFunc(depthFunc);
        }
    }

    public static void depthMask(boolean flagIn)
    {
        if (flagIn != depthState.maskEnabled)
        {
            depthState.maskEnabled = flagIn;
            GL11.glDepthMask(flagIn);
        }
    }

    public static void disableBlend()
    {
        if (blendLock.isLocked())
        {
            blendLockState.setDisabled();
        }
        else
        {
            blendState.blend.setDisabled();
        }
    }

    public static void enableBlend()
    {
        if (blendLock.isLocked())
        {
            blendLockState.setEnabled();
        }
        else
        {
            blendState.blend.setEnabled();
        }
    }

    public static void blendFunc(int srcFactor, int dstFactor)
    {
        if (blendLock.isLocked())
        {
            blendLockState.setFactors(srcFactor, dstFactor);
        }
        else
        {
            if (srcFactor != blendState.srcFactor || dstFactor != blendState.dstFactor || srcFactor != blendState.srcFactorAlpha || dstFactor != blendState.dstFactorAlpha)
            {
                blendState.srcFactor = srcFactor;
                blendState.dstFactor = dstFactor;
                blendState.srcFactorAlpha = srcFactor;
                blendState.dstFactorAlpha = dstFactor;

                if (Config.isShaders())
                {
                    Shaders.uniform_blendFunc.setValue(srcFactor, dstFactor, srcFactor, dstFactor);
                }

                GL11.glBlendFunc(srcFactor, dstFactor);
            }
        }
    }

    public static void tryBlendFuncSeparate(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha)
    {
        if (blendLock.isLocked())
        {
            blendLockState.setFactors(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
        }
        else
        {
            if (srcFactor != blendState.srcFactor || dstFactor != blendState.dstFactor || srcFactorAlpha != blendState.srcFactorAlpha || dstFactorAlpha != blendState.dstFactorAlpha)
            {
                blendState.srcFactor = srcFactor;
                blendState.dstFactor = dstFactor;
                blendState.srcFactorAlpha = srcFactorAlpha;
                blendState.dstFactorAlpha = dstFactorAlpha;

                if (Config.isShaders())
                {
                    Shaders.uniform_blendFunc.setValue(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
                }

                OpenGlHelper.glBlendFunc(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
            }
        }
    }

    public static void enableFog()
    {
        fogState.fog.setEnabled();
    }

    public static void disableFog()
    {
        fogState.fog.setDisabled();
    }

    public static void setFog(int param)
    {
        if (param != fogState.mode)
        {
            fogState.mode = param;
            GL11.glFogi(GL11.GL_FOG_MODE, param);

            if (Config.isShaders())
            {
                Shaders.setFogMode(param);
            }
        }
    }

    public static void setFogDensity(float param)
    {
        if (param < 0.0F)
        {
            param = 0.0F;
        }

        if (param != fogState.density)
        {
            fogState.density = param;
            GL11.glFogf(GL11.GL_FOG_DENSITY, param);

            if (Config.isShaders())
            {
                Shaders.setFogDensity(param);
            }
        }
    }

    public static void setFogStart(float param)
    {
        if (param != fogState.start)
        {
            fogState.start = param;
            GL11.glFogf(GL11.GL_FOG_START, param);
        }
    }

    public static void setFogEnd(float param)
    {
        if (param != fogState.end)
        {
            fogState.end = param;
            GL11.glFogf(GL11.GL_FOG_END, param);
        }
    }

    public static void glFog(int p_glFog_0_, FloatBuffer p_glFog_1_)
    {
        GL11.glFogfv(p_glFog_0_, p_glFog_1_);
    }

    public static void glFogi(int p_glFogi_0_, int p_glFogi_1_)
    {
        GL11.glFogi(p_glFogi_0_, p_glFogi_1_);
    }

    public static void enableCull()
    {
        cullState.cullFace.setEnabled();
    }

    public static void disableCull()
    {
        cullState.cullFace.setDisabled();
    }

    public static void cullFace(int mode)
    {
        if (mode != cullState.mode)
        {
            cullState.mode = mode;
            GL11.glCullFace(mode);
        }
    }

    public static void enablePolygonOffset()
    {
        polygonOffsetState.polygonOffsetFill.setEnabled();
    }

    public static void disablePolygonOffset()
    {
        polygonOffsetState.polygonOffsetFill.setDisabled();
    }

    public static void doPolygonOffset(float factor, float units)
    {
        if (factor != polygonOffsetState.factor || units != polygonOffsetState.units)
        {
            polygonOffsetState.factor = factor;
            polygonOffsetState.units = units;
            GL11.glPolygonOffset(factor, units);
        }
    }

    public static void enableColorLogic()
    {
        colorLogicState.colorLogicOp.setEnabled();
    }

    public static void disableColorLogic()
    {
        colorLogicState.colorLogicOp.setDisabled();
    }

    public static void colorLogicOp(int opcode)
    {
        if (opcode != colorLogicState.opcode)
        {
            colorLogicState.opcode = opcode;
            GL11.glLogicOp(opcode);
        }
    }

    public static void enableTexGenCoord(GlStateManager.TexGen p_179087_0_)
    {
        texGenCoord(p_179087_0_).textureGen.setEnabled();
    }

    public static void disableTexGenCoord(GlStateManager.TexGen p_179100_0_)
    {
        texGenCoord(p_179100_0_).textureGen.setDisabled();
    }

    public static void texGen(GlStateManager.TexGen texGen, int param)
    {
        GlStateManager.TexGenCoord glstatemanager$texgencoord = texGenCoord(texGen);

        if (param != glstatemanager$texgencoord.param)
        {
            glstatemanager$texgencoord.param = param;
            GL11.glTexGeni(glstatemanager$texgencoord.coord, GL11.GL_TEXTURE_GEN_MODE, param);
        }
    }

    public static void texGen(GlStateManager.TexGen p_179105_0_, int pname, FloatBuffer params)
    {
        GL11.glGetTexGenfv(texGenCoord(p_179105_0_).coord, pname, params);
    }
    private static GlStateManager.TexGenCoord texGenCoord(GlStateManager.TexGen p_179125_0_)
    {
        switch (p_179125_0_)
        {
            case S:
                return texGenState.s;

            case T:
                return texGenState.t;

            case R:
                return texGenState.r;

            case Q:
                return texGenState.q;

            default:
                return texGenState.s;
        }
    }

    public static void setActiveTexture(int texture)
    {
        if (activeTextureUnit != texture - OpenGlHelper.defaultTexUnit)
        {
            activeTextureUnit = texture - OpenGlHelper.defaultTexUnit;
            OpenGlHelper.setActiveTexture(texture);
        }
    }

    public static void enableTexture2D()
    {
        textureState[activeTextureUnit].texture2DState.setEnabled();
    }

    public static void disableTexture2D()
    {
        textureState[activeTextureUnit].texture2DState.setDisabled();
    }

    public static int generateTexture()
    {
        return GL11.glGenTextures();
    }

    public static void deleteTexture(int texture)
    {
        if (texture != 0)
        {
            GL11.glDeleteTextures(texture);

            for (GlStateManager.TextureState glstatemanager$texturestate : textureState)
            {
                if (glstatemanager$texturestate.textureName == texture)
                {
                    glstatemanager$texturestate.textureName = 0;
                }
            }
        }
    }

    public static void bindTexture(int texture)
    {
        if (texture != textureState[activeTextureUnit].textureName)
        {
            textureState[activeTextureUnit].textureName = texture;
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);

            if (SmartAnimations.isActive())
            {
                SmartAnimations.textureRendered(texture);
            }
        }
    }

    public static void enableNormalize()
    {
        normalizeState.setEnabled();
    }

    public static void disableNormalize()
    {
        normalizeState.setDisabled();
    }

    public static void shadeModel(int mode)
    {
        if (mode != activeShadeModel)
        {
            activeShadeModel = mode;
            GL11.glShadeModel(mode);
        }
    }

    public static void enableRescaleNormal()
    {
        rescaleNormalState.setEnabled();
    }

    public static void disableRescaleNormal()
    {
        rescaleNormalState.setDisabled();
    }

    public static void viewport(int x, int y, int width, int height)
    {
        GL11.glViewport(x, y, width, height);
    }

    public static void colorMask(boolean red, boolean green, boolean blue, boolean alpha)
    {
        if (red != colorMaskState.red || green != colorMaskState.green || blue != colorMaskState.blue || alpha != colorMaskState.alpha)
        {
            colorMaskState.red = red;
            colorMaskState.green = green;
            colorMaskState.blue = blue;
            colorMaskState.alpha = alpha;
            GL11.glColorMask(red, green, blue, alpha);
        }
    }

    public static void clearDepth(double depth)
    {
        if (depth != clearState.depth)
        {
            clearState.depth = depth;
            GL11.glClearDepth(depth);
        }
    }

    public static void clearColor(float red, float green, float blue, float alpha)
    {
        if (red != clearState.color.red || green != clearState.color.green || blue != clearState.color.blue || alpha != clearState.color.alpha)
        {
            clearState.color.red = red;
            clearState.color.green = green;
            clearState.color.blue = blue;
            clearState.color.alpha = alpha;
            GL11.glClearColor(red, green, blue, alpha);
        }
    }

    public static void clear(int mask)
    {
        if (clearEnabled)
        {
            GL11.glClear(mask);
        }
    }

    public static void matrixMode(int mode)
    {
        GL11.glMatrixMode(mode);
    }

    public static void loadIdentity()
    {
        GL11.glLoadIdentity();
    }

    public static void pushMatrix()
    {
        GL11.glPushMatrix();
    }

    public static void popMatrix()
    {
        GL11.glPopMatrix();
    }

    public static void getFloat(int pname, FloatBuffer params)
    {
        GL11.glGetFloatv(pname, params);
    }
    public static void ortho(double left, double right, double bottom, double top, double zNear, double zFar)
    {
        GL11.glOrtho(left, right, bottom, top, zNear, zFar);
    }

    public static void rotate(float angle, float x, float y, float z)
    {
        GL11.glRotatef(angle, x, y, z);
    }

    public static void scale(float x, float y, float z)
    {
        GL11.glScalef(x, y, z);
    }

    public static void scale(double x, double y, double z)
    {
        GL11.glScaled(x, y, z);
    }

    public static void translate(float x, float y, float z)
    {
        GL11.glTranslatef(x, y, z);
    }

    public static void translate(double x, double y, double z)
    {
        GL11.glTranslated(x, y, z);
    }

    public static void multMatrix(FloatBuffer matrix)
    {
        GL11.glMultMatrixf(matrix);
    }

    public static void color(float colorRed, float colorGreen, float colorBlue, float colorAlpha)
    {
        if (colorRed != colorState.red || colorGreen != colorState.green || colorBlue != colorState.blue || colorAlpha != colorState.alpha)
        {
            colorState.red = colorRed;
            colorState.green = colorGreen;
            colorState.blue = colorBlue;
            colorState.alpha = colorAlpha;
            GL11.glColor4f(colorRed, colorGreen, colorBlue, colorAlpha);
        }
    }

    public static void color(float colorRed, float colorGreen, float colorBlue)
    {
        color(colorRed, colorGreen, colorBlue, 1.0F);
    }

    public static void resetColor()
    {
        colorState.red = colorState.green = colorState.blue = colorState.alpha = -1.0F;
    }

    public static void glNormalPointer(int p_glNormalPointer_0_, int p_glNormalPointer_1_, ByteBuffer p_glNormalPointer_2_)
    {
        GL11.glNormalPointer(p_glNormalPointer_0_, p_glNormalPointer_1_, p_glNormalPointer_2_);
    }

    public static void glTexCoordPointer(int p_glTexCoordPointer_0_, int p_glTexCoordPointer_1_, int p_glTexCoordPointer_2_, int p_glTexCoordPointer_3_)
    {
        GL11.glTexCoordPointer(p_glTexCoordPointer_0_, p_glTexCoordPointer_1_, p_glTexCoordPointer_2_, p_glTexCoordPointer_3_);
    }

    public static void glTexCoordPointer(int p_glTexCoordPointer_0_, int p_glTexCoordPointer_1_, int p_glTexCoordPointer_2_, ByteBuffer p_glTexCoordPointer_3_)
    {
        GL11.glTexCoordPointer(p_glTexCoordPointer_0_, p_glTexCoordPointer_1_, p_glTexCoordPointer_2_, p_glTexCoordPointer_3_);
    }

    public static void glVertexPointer(int p_glVertexPointer_0_, int p_glVertexPointer_1_, int p_glVertexPointer_2_, int p_glVertexPointer_3_)
    {
        GL11.glVertexPointer(p_glVertexPointer_0_, p_glVertexPointer_1_, p_glVertexPointer_2_, p_glVertexPointer_3_);
    }

    public static void glVertexPointer(int p_glVertexPointer_0_, int p_glVertexPointer_1_, int p_glVertexPointer_2_, ByteBuffer p_glVertexPointer_3_)
    {
        GL11.glVertexPointer(p_glVertexPointer_0_, p_glVertexPointer_1_, p_glVertexPointer_2_, p_glVertexPointer_3_);
    }

    public static void glColorPointer(int p_glColorPointer_0_, int p_glColorPointer_1_, int p_glColorPointer_2_, int p_glColorPointer_3_)
    {
        GL11.glColorPointer(p_glColorPointer_0_, p_glColorPointer_1_, p_glColorPointer_2_, p_glColorPointer_3_);
    }

    public static void glColorPointer(int p_glColorPointer_0_, int p_glColorPointer_1_, int p_glColorPointer_2_, ByteBuffer p_glColorPointer_3_)
    {
        GL11.glColorPointer(p_glColorPointer_0_, p_glColorPointer_1_, p_glColorPointer_2_, p_glColorPointer_3_);
    }

    public static void glDisableClientState(int p_glDisableClientState_0_)
    {
        GL11.glDisableClientState(p_glDisableClientState_0_);
    }

    public static void glEnableClientState(int p_glEnableClientState_0_)
    {
        GL11.glEnableClientState(p_glEnableClientState_0_);
    }

    public static void glBegin(int p_glBegin_0_)
    {
        GL11.glBegin(p_glBegin_0_);
    }

    public static void glEnd()
    {
        GL11.glEnd();
    }

    public static void glDrawArrays(int p_glDrawArrays_0_, int p_glDrawArrays_1_, int p_glDrawArrays_2_)
    {
        GL11.glDrawArrays(p_glDrawArrays_0_, p_glDrawArrays_1_, p_glDrawArrays_2_);

        if (Config.isShaders() && !creatingDisplayList)
        {
            int i = Shaders.activeProgram.getCountInstances();

            if (i > 1)
            {
                for (int j = 1; j < i; ++j)
                {
                    Shaders.uniform_instanceId.setValue(j);
                    GL11.glDrawArrays(p_glDrawArrays_0_, p_glDrawArrays_1_, p_glDrawArrays_2_);
                }

                Shaders.uniform_instanceId.setValue(0);
            }
        }
    }

    public static void callList(int list)
    {
        GL11.glCallList(list);

        if (Config.isShaders() && !creatingDisplayList)
        {
            int i = Shaders.activeProgram.getCountInstances();

            if (i > 1)
            {
                for (int j = 1; j < i; ++j)
                {
                    Shaders.uniform_instanceId.setValue(j);
                    GL11.glCallList(list);
                }

                Shaders.uniform_instanceId.setValue(0);
            }
        }
    }

    public static void callLists(IntBuffer p_callLists_0_)
    {
        GL11.glCallLists(p_callLists_0_);

        if (Config.isShaders() && !creatingDisplayList)
        {
            int i = Shaders.activeProgram.getCountInstances();

            if (i > 1)
            {
                for (int j = 1; j < i; ++j)
                {
                    Shaders.uniform_instanceId.setValue(j);
                    GL11.glCallLists(p_callLists_0_);
                }

                Shaders.uniform_instanceId.setValue(0);
            }
        }
    }

    public static void glDeleteLists(int p_glDeleteLists_0_, int p_glDeleteLists_1_)
    {
        GL11.glDeleteLists(p_glDeleteLists_0_, p_glDeleteLists_1_);
    }

    public static void glNewList(int p_glNewList_0_, int p_glNewList_1_)
    {
        GL11.glNewList(p_glNewList_0_, p_glNewList_1_);
        creatingDisplayList = true;
    }

    public static void glEndList()
    {
        GL11.glEndList();
        creatingDisplayList = false;
    }

    public static int glGetError()
    {
        return GL11.glGetError();
    }

    public static void glTexImage2D(int p_glTexImage2D_0_, int p_glTexImage2D_1_, int p_glTexImage2D_2_, int p_glTexImage2D_3_, int p_glTexImage2D_4_, int p_glTexImage2D_5_, int p_glTexImage2D_6_, int p_glTexImage2D_7_, IntBuffer p_glTexImage2D_8_)
    {
        GL11.glTexImage2D(p_glTexImage2D_0_, p_glTexImage2D_1_, p_glTexImage2D_2_, p_glTexImage2D_3_, p_glTexImage2D_4_, p_glTexImage2D_5_, p_glTexImage2D_6_, p_glTexImage2D_7_, p_glTexImage2D_8_);
    }

    public static void glTexSubImage2D(int p_glTexSubImage2D_0_, int p_glTexSubImage2D_1_, int p_glTexSubImage2D_2_, int p_glTexSubImage2D_3_, int p_glTexSubImage2D_4_, int p_glTexSubImage2D_5_, int p_glTexSubImage2D_6_, int p_glTexSubImage2D_7_, IntBuffer p_glTexSubImage2D_8_)
    {
        GL11.glTexSubImage2D(p_glTexSubImage2D_0_, p_glTexSubImage2D_1_, p_glTexSubImage2D_2_, p_glTexSubImage2D_3_, p_glTexSubImage2D_4_, p_glTexSubImage2D_5_, p_glTexSubImage2D_6_, p_glTexSubImage2D_7_, p_glTexSubImage2D_8_);
    }

    public static void glCopyTexSubImage2D(int p_glCopyTexSubImage2D_0_, int p_glCopyTexSubImage2D_1_, int p_glCopyTexSubImage2D_2_, int p_glCopyTexSubImage2D_3_, int p_glCopyTexSubImage2D_4_, int p_glCopyTexSubImage2D_5_, int p_glCopyTexSubImage2D_6_, int p_glCopyTexSubImage2D_7_)
    {
        GL11.glCopyTexSubImage2D(p_glCopyTexSubImage2D_0_, p_glCopyTexSubImage2D_1_, p_glCopyTexSubImage2D_2_, p_glCopyTexSubImage2D_3_, p_glCopyTexSubImage2D_4_, p_glCopyTexSubImage2D_5_, p_glCopyTexSubImage2D_6_, p_glCopyTexSubImage2D_7_);
    }

    public static void glGetTexImage(int p_glGetTexImage_0_, int p_glGetTexImage_1_, int p_glGetTexImage_2_, int p_glGetTexImage_3_, IntBuffer p_glGetTexImage_4_)
    {
        GL11.glGetTexImage(p_glGetTexImage_0_, p_glGetTexImage_1_, p_glGetTexImage_2_, p_glGetTexImage_3_, p_glGetTexImage_4_);
    }

    public static void glTexParameterf(int p_glTexParameterf_0_, int p_glTexParameterf_1_, float p_glTexParameterf_2_)
    {
        GL11.glTexParameterf(p_glTexParameterf_0_, p_glTexParameterf_1_, p_glTexParameterf_2_);
    }

    public static void glTexParameteri(int p_glTexParameteri_0_, int p_glTexParameteri_1_, int p_glTexParameteri_2_)
    {
        GL11.glTexParameteri(p_glTexParameteri_0_, p_glTexParameteri_1_, p_glTexParameteri_2_);
    }

    public static int glGetTexLevelParameteri(int p_glGetTexLevelParameteri_0_, int p_glGetTexLevelParameteri_1_, int p_glGetTexLevelParameteri_2_)
    {
        return GL11.glGetTexLevelParameteri(p_glGetTexLevelParameteri_0_, p_glGetTexLevelParameteri_1_, p_glGetTexLevelParameteri_2_);
    }

    public static int getActiveTextureUnit()
    {
        return OpenGlHelper.defaultTexUnit + activeTextureUnit;
    }

    public static void bindCurrentTexture()
    {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureState[activeTextureUnit].textureName);
    }

    public static int getBoundTexture()
    {
        return textureState[activeTextureUnit].textureName;
    }

    public static void checkBoundTexture()
    {
        if (Config.isMinecraftThread())
        {
            int i = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
            int j = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
            int k = getActiveTextureUnit();
            int l = getBoundTexture();

            if (l > 0)
            {
                if (i != k || j != l)
                {
                    Config.dbg("checkTexture: act: " + k + ", glAct: " + i + ", tex: " + l + ", glTex: " + j);
                }
            }
        }
    }

    public static void deleteTextures(IntBuffer p_deleteTextures_0_)
    {
        p_deleteTextures_0_.rewind();

        while (p_deleteTextures_0_.position() < p_deleteTextures_0_.limit())
        {
            int i = p_deleteTextures_0_.get();
            deleteTexture(i);
        }

        p_deleteTextures_0_.rewind();
    }

    public static boolean isFogEnabled()
    {
        return fogState.fog.currentState;
    }

    public static void setFogEnabled(boolean p_setFogEnabled_0_)
    {
        fogState.fog.setState(p_setFogEnabled_0_);
    }

    public static void lockAlpha(GlAlphaState p_lockAlpha_0_)
    {
        if (!alphaLock.isLocked())
        {
            getAlphaState(alphaLockState);
            setAlphaState(p_lockAlpha_0_);
            alphaLock.lock();
        }
    }

    public static void unlockAlpha()
    {
        if (alphaLock.unlock())
        {
            setAlphaState(alphaLockState);
        }
    }

    public static void getAlphaState(GlAlphaState p_getAlphaState_0_)
    {
        if (alphaLock.isLocked())
        {
            p_getAlphaState_0_.setState(alphaLockState);
        }
        else
        {
            p_getAlphaState_0_.setState(alphaState.alphaTest.currentState, alphaState.func, alphaState.ref);
        }
    }

    public static void setAlphaState(GlAlphaState p_setAlphaState_0_)
    {
        if (alphaLock.isLocked())
        {
            alphaLockState.setState(p_setAlphaState_0_);
        }
        else
        {
            alphaState.alphaTest.setState(p_setAlphaState_0_.isEnabled());
            alphaFunc(p_setAlphaState_0_.getFunc(), p_setAlphaState_0_.getRef());
        }
    }

    public static void lockBlend(GlBlendState p_lockBlend_0_)
    {
        if (!blendLock.isLocked())
        {
            getBlendState(blendLockState);
            setBlendState(p_lockBlend_0_);
            blendLock.lock();
        }
    }

    public static void unlockBlend()
    {
        if (blendLock.unlock())
        {
            setBlendState(blendLockState);
        }
    }

    public static void getBlendState(GlBlendState p_getBlendState_0_)
    {
        if (blendLock.isLocked())
        {
            p_getBlendState_0_.setState(blendLockState);
        }
        else
        {
            p_getBlendState_0_.setState(blendState.blend.currentState, blendState.srcFactor, blendState.dstFactor, blendState.srcFactorAlpha, blendState.dstFactorAlpha);
        }
    }

    public static void setBlendState(GlBlendState p_setBlendState_0_)
    {
        if (blendLock.isLocked())
        {
            blendLockState.setState(p_setBlendState_0_);
        }
        else
        {
            blendState.blend.setState(p_setBlendState_0_.isEnabled());

            if (!p_setBlendState_0_.isSeparate())
            {
                blendFunc(p_setBlendState_0_.getSrcFactor(), p_setBlendState_0_.getDstFactor());
            }
            else
            {
                tryBlendFuncSeparate(p_setBlendState_0_.getSrcFactor(), p_setBlendState_0_.getDstFactor(), p_setBlendState_0_.getSrcFactorAlpha(), p_setBlendState_0_.getDstFactorAlpha());
            }
        }
    }

    public static void glMultiDrawArrays(int p_glMultiDrawArrays_0_, IntBuffer p_glMultiDrawArrays_1_, IntBuffer p_glMultiDrawArrays_2_)
    {
        GL14.glMultiDrawArrays(p_glMultiDrawArrays_0_, p_glMultiDrawArrays_1_, p_glMultiDrawArrays_2_);

        if (Config.isShaders() && !creatingDisplayList)
        {
            int i = Shaders.activeProgram.getCountInstances();

            if (i > 1)
            {
                for (int j = 1; j < i; ++j)
                {
                    Shaders.uniform_instanceId.setValue(j);
                    GL14.glMultiDrawArrays(p_glMultiDrawArrays_0_, p_glMultiDrawArrays_1_, p_glMultiDrawArrays_2_);
                }

                Shaders.uniform_instanceId.setValue(0);
            }
        }
    }

    static
    {
        for (int i = 0; i < 8; ++i)
        {
            lightState[i] = new GlStateManager.BooleanState(16384 + i);
        }

        for (int j = 0; j < textureState.length; ++j)
        {
            textureState[j] = new GlStateManager.TextureState();
        }
    }

    static class AlphaState
    {
        public GlStateManager.BooleanState alphaTest;
        public int func;
        public float ref;

        private AlphaState()
        {
            this.alphaTest = new GlStateManager.BooleanState(3008);
            this.func = 519;
            this.ref = -1.0F;
        }
    }

    static class BlendState
    {
        public GlStateManager.BooleanState blend;
        public int srcFactor;
        public int dstFactor;
        public int srcFactorAlpha;
        public int dstFactorAlpha;

        private BlendState()
        {
            this.blend = new GlStateManager.BooleanState(3042);
            this.srcFactor = 1;
            this.dstFactor = 0;
            this.srcFactorAlpha = 1;
            this.dstFactorAlpha = 0;
        }
    }

    static class BooleanState
    {
        private final int capability;
        private boolean currentState = false;

        public BooleanState(int capabilityIn)
        {
            this.capability = capabilityIn;
        }

        public void setDisabled()
        {
            this.setState(false);
        }

        public void setEnabled()
        {
            this.setState(true);
        }

        public void setState(boolean state)
        {
            if (state != this.currentState)
            {
                this.currentState = state;

                if (state)
                {
                    GL11.glEnable(this.capability);
                }
                else
                {
                    GL11.glDisable(this.capability);
                }
            }
        }
    }

    static class ClearState
    {
        public double depth;
        public GlStateManager.Color color;
        public int field_179204_c;

        private ClearState()
        {
            this.depth = 1.0D;
            this.color = new GlStateManager.Color(0.0F, 0.0F, 0.0F, 0.0F);
            this.field_179204_c = 0;
        }
    }

    static class Color
    {
        public float red = 1.0F;
        public float green = 1.0F;
        public float blue = 1.0F;
        public float alpha = 1.0F;

        public Color()
        {
        }

        public Color(float redIn, float greenIn, float blueIn, float alphaIn)
        {
            this.red = redIn;
            this.green = greenIn;
            this.blue = blueIn;
            this.alpha = alphaIn;
        }
    }

    static class ColorLogicState
    {
        public GlStateManager.BooleanState colorLogicOp;
        public int opcode;

        private ColorLogicState()
        {
            this.colorLogicOp = new GlStateManager.BooleanState(3058);
            this.opcode = 5379;
        }
    }

    static class ColorMask
    {
        public boolean red;
        public boolean green;
        public boolean blue;
        public boolean alpha;

        private ColorMask()
        {
            this.red = true;
            this.green = true;
            this.blue = true;
            this.alpha = true;
        }
    }

    static class ColorMaterialState
    {
        public GlStateManager.BooleanState colorMaterial;
        public int face;
        public int mode;

        private ColorMaterialState()
        {
            this.colorMaterial = new GlStateManager.BooleanState(2903);
            this.face = 1032;
            this.mode = 5634;
        }
    }

    static class CullState
    {
        public GlStateManager.BooleanState cullFace;
        public int mode;

        private CullState()
        {
            this.cullFace = new GlStateManager.BooleanState(2884);
            this.mode = 1029;
        }
    }

    static class DepthState
    {
        public GlStateManager.BooleanState depthTest;
        public boolean maskEnabled;
        public int depthFunc;

        private DepthState()
        {
            this.depthTest = new GlStateManager.BooleanState(2929);
            this.maskEnabled = true;
            this.depthFunc = 513;
        }
    }

    static class FogState
    {
        public GlStateManager.BooleanState fog;
        public int mode;
        public float density;
        public float start;
        public float end;

        private FogState()
        {
            this.fog = new GlStateManager.BooleanState(2912);
            this.mode = 2048;
            this.density = 1.0F;
            this.start = 0.0F;
            this.end = 1.0F;
        }
    }

    static class PolygonOffsetState
    {
        public GlStateManager.BooleanState polygonOffsetFill;
        public GlStateManager.BooleanState polygonOffsetLine;
        public float factor;
        public float units;

        private PolygonOffsetState()
        {
            this.polygonOffsetFill = new GlStateManager.BooleanState(32823);
            this.polygonOffsetLine = new GlStateManager.BooleanState(10754);
            this.factor = 0.0F;
            this.units = 0.0F;
        }
    }

    static class StencilFunc
    {
        public int field_179081_a;
        public int field_179079_b;
        public int field_179080_c;

        private StencilFunc()
        {
            this.field_179081_a = 519;
            this.field_179079_b = 0;
            this.field_179080_c = -1;
        }
    }

    static class StencilState
    {
        public GlStateManager.StencilFunc field_179078_a;
        public int field_179076_b;
        public int field_179077_c;
        public int field_179074_d;
        public int field_179075_e;

        private StencilState()
        {
            this.field_179078_a = new GlStateManager.StencilFunc();
            this.field_179076_b = -1;
            this.field_179077_c = 7680;
            this.field_179074_d = 7680;
            this.field_179075_e = 7680;
        }
    }

    public enum TexGen
    {
        S,
        T,
        R,
        Q
    }

    static class TexGenCoord
    {
        public GlStateManager.BooleanState textureGen;
        public int coord;
        public int param = -1;

        public TexGenCoord(int p_i46254_1_, int p_i46254_2_)
        {
            this.coord = p_i46254_1_;
            this.textureGen = new GlStateManager.BooleanState(p_i46254_2_);
        }
    }

    static class TexGenState
    {
        public GlStateManager.TexGenCoord s;
        public GlStateManager.TexGenCoord t;
        public GlStateManager.TexGenCoord r;
        public GlStateManager.TexGenCoord q;

        private TexGenState()
        {
            this.s = new GlStateManager.TexGenCoord(8192, 3168);
            this.t = new GlStateManager.TexGenCoord(8193, 3169);
            this.r = new GlStateManager.TexGenCoord(8194, 3170);
            this.q = new GlStateManager.TexGenCoord(8195, 3171);
        }
    }

    static class TextureState
    {
        public GlStateManager.BooleanState texture2DState;
        public int textureName;

        private TextureState()
        {
            this.texture2DState = new GlStateManager.BooleanState(3553);
            this.textureName = 0;
        }
    }
}
