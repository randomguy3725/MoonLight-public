/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & opZywl & lucas]
 */
package wtf.moonlight.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.InstanceAccess;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static java.lang.Math.PI;
import static net.minecraft.client.gui.Gui.drawModalRectWithCustomSizedTexture;
import static net.minecraft.client.renderer.GlStateManager.disableBlend;
import static net.minecraft.client.renderer.GlStateManager.enableTexture2D;
import static org.lwjgl.opengl.GL11.*;

public class RenderUtils implements InstanceAccess {
    private static final Frustum FRUSTUM = new Frustum();
    public static final Pattern COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");

    public static void drawRect(float left, float top, float width, float height, int color) {
        float right = left + width, bottom = top + height;
        if (left < right) {
            float i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            float j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, top, 0.0D).endVertex();
        worldrenderer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void bindTexture(int texture) {
        GlStateManager.bindTexture(texture);
    }

    public static void color(double red, double green, double blue, double alpha) {
        GL11.glColor4d(red, green, blue, alpha);
    }

    public static void color(int color) {
        glColor4ub(
                (byte) (color >> 16 & 0xFF),
                (byte) (color >> 8 & 0xFF),
                (byte) (color & 0xFF),
                (byte) (color >> 24 & 0xFF));
    }

    public static void resetColor() {
        color(1, 1, 1, 1);
    }

    public static void setAlphaLimit(float limit) {
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, (float) (limit * .01));
    }

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        return createFrameBuffer(framebuffer, false);
    }

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer, boolean depth) {
        if (framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, depth);
        }
        return framebuffer;
    }

    public static void enableScissor(final double x, final double y, final double width, final double height) {
        int scaleFactor = 1;
        while (scaleFactor < 2 && mc.displayWidth / (scaleFactor + 1) >= 320 && mc.displayHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }
        GL11.glScissor((int) (x * scaleFactor),
                (int) (Minecraft.getMinecraft().displayHeight - (y + height) * scaleFactor),
                (int) (width * scaleFactor), (int) (height * scaleFactor));


        GL11.glEnable(GL11.GL_SCISSOR_TEST);
    }

    public static void disableScissor(){
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }


    public static void scissor(final double x, final double y, final double width, final double height) {
        int scaleFactor = 1;
        while (scaleFactor < 2 && mc.displayWidth / (scaleFactor + 1) >= 320 && mc.displayHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }
        GL11.glScissor((int) (x * scaleFactor),
                (int) (Minecraft.getMinecraft().displayHeight - (y + height) * scaleFactor),
                (int) (width * scaleFactor), (int) (height * scaleFactor));
    }


    public static void drawImage(ResourceLocation image, float x, float y, int width, int height) {
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glDepthMask(false);
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(image);
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        glDepthMask(true);
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
    }

    public static void drawImage(ResourceLocation image, double x, double y, double z, double width, double height, int color1, int color2, int color3, int color4) {
        mc.getTextureManager().bindTexture(image);
        drawImage(x, y, z, width, height, color1, color2, color3, color4);
    }


    public static void drawImage(double x, double y, double z, double width, double height, int color1, int color2, int color3, int color4) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        boolean blend = glIsEnabled(GL_BLEND);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE);
        glShadeModel(GL_SMOOTH);
        glAlphaFunc(GL_GREATER, 0);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        worldRenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldRenderer.pos((float) x, (float) (y + height), (float) (z)).tex(0, 1 - 0.01f).color(color1).endVertex();
        worldRenderer.pos((float) (x + width), (float) (y + height), (float) (z)).tex(1, 1 - 0.01f).color(color2).endVertex();
        worldRenderer.pos((float) (x + width), (float) y, (float) z).tex(1, 0).color(color3).endVertex();
        worldRenderer.pos((float) x, (float) y, (float) z).tex(0, 0).color(color4).endVertex();
        tessellator.draw();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        glShadeModel(GL_FLAT);
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ZERO);
        if (!blend)
            GlStateManager.disableBlend();
    }
    public static void drawImage(ResourceLocation resource, float x, float y, float x2, float y2, int c) {
        mc.getTextureManager().bindTexture(resource);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(9, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldRenderer.pos(x, y2).tex(0.0, 1.0).color(c).endVertex();
        worldRenderer.pos(x2, y2).tex(1.0, 1.0).color(c).endVertex();
        worldRenderer.pos(x2, y).tex(1.0, 0.0).color(c).endVertex();
        worldRenderer.pos(x, y).tex(0.0, 0.0).color(c).endVertex();
        GL11.glShadeModel(7425);
        GL11.glDepthMask(false);
        tessellator.draw();
        GL11.glDepthMask(true);
        GL11.glShadeModel(7424);
    }

    public static void drawImage(ResourceLocation resource, float x, float y, float x2, float y2, int c, int c2, int c3, int c4) {
        mc.getTextureManager().bindTexture(resource);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(9, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldRenderer.pos(x, y2).tex(0.0, 1.0).color(c).endVertex();
        worldRenderer.pos(x2, y2).tex(1.0, 1.0).color(c2).endVertex();
        worldRenderer.pos(x2, y).tex(1.0, 0.0).color(c3).endVertex();
        worldRenderer.pos(x, y).tex(0.0, 0.0).color(c4).endVertex();
        GL11.glShadeModel(7425);
        GL11.glDepthMask(false);
        tessellator.draw();
        GL11.glDepthMask(true);
        GL11.glShadeModel(7424);
    }

    public static void setColor(int color) {
        GL11.glColor4ub((byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF), (byte) (color >> 24 & 0xFF));
    }

    public static void drawCircle(float x, float y, float start, float end, float radius, float width, boolean filled, int color) {
        float i;
        float endOffset;
        if (start > end) {
            endOffset = end;
            end = start;
            start = endOffset;
        }

        GlStateManager.enableBlend();
        GL11.glDisable(GL_TEXTURE_2D);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(width);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (i = end; i >= start; i--) {
            setColor(color);
            float cos = MathHelper.cos((float) (i * Math.PI / 180)) * radius;
            float sin = MathHelper.sin((float) (i * Math.PI / 180)) * radius;
            GL11.glVertex2f(x + cos, y + sin);
        }
        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        if (filled) {
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
            for (i = end; i >= start; i--) {
                setColor(color);
                float cos = MathHelper.cos((float) (i * Math.PI / 180)) * radius;
                float sin = MathHelper.sin((float) (i * Math.PI / 180)) * radius;
                GL11.glVertex2f(x + cos, y + sin);
            }
            GL11.glEnd();
        }

        GL11.glEnable(GL_TEXTURE_2D);
        GlStateManager.disableBlend();
        resetColor();
    }

    public static void drawArrow(double x, double y, int lineWidth, int color, double length) {
        glEnable(3042);
        glDisable(3553);
        glBlendFunc(770, 771);
        glEnable(2848);
        GL11.glPushMatrix();
        GL11.glLineWidth(lineWidth);
        color(color);
        GL11.glBegin(GL_LINE_STRIP);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x + 3, y + length);
        GL11.glVertex2d(x + 3 * 2, y);
        GL11.glEnd();
        GL11.glPopMatrix();
        glEnable(3553);
        glDisable(3042);
        glDisable(2848);
        enableTexture2D();
        disableBlend();
        glColor4f(1, 1, 1, 1);
    }

    public static void drawArrow(final float x, final float y, final float size, final boolean rotate) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 1.0f);
        GLUtils.startBlend();
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(1.0f);
        GL11.glDisable(3553);
        GL11.glBegin(4);
        if (rotate) {
            GL11.glVertex2f(size, size / 2.0f);
            GL11.glVertex2f(size / 2.0f, 0.0f);
            GL11.glVertex2f(0.0f, size / 2.0f);
        } else {
            GL11.glVertex2f(0.0f, 0.0f);
            GL11.glVertex2f(size / 2.0f, size / 2.0f);
            GL11.glVertex2f(size, 0.0f);
        }
        GL11.glEnd();
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GLUtils.endBlend();
        GL11.glPopMatrix();
    }

    public static void drawCheck(double x, double y, int lineWidth, int color) {
        glEnable(3042);
        glDisable(3553);
        glBlendFunc(770, 771);
        glEnable(2848);

        GL11.glPushMatrix();
        GL11.glLineWidth(lineWidth);
        setColor(color);
        GL11.glBegin(GL_LINE_STRIP);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x + 2, y + 3);
        GL11.glVertex2d(x + 6, y - 2);
        GL11.glEnd();
        GL11.glPopMatrix();

        glEnable(3553);
        glDisable(3042);
        glDisable(2848);
        enableTexture2D();
        disableBlend();
        glColor4f(1, 1, 1, 1);
    }

    public static double deltaTime() {
        return Minecraft.getDebugFPS() > 0 ? (1.0000 / Minecraft.getDebugFPS()) : 1;
    }

    public static float animate(float end, float start, float multiple) {
        return (1 - MathHelper.clamp_float((float) (deltaTime() * multiple), 0, 1)) * end + MathHelper.clamp_float((float) (deltaTime() * multiple), 0, 1) * start;
    }

    public static boolean isBBInFrustum(EntityLivingBase entity) {
        return isBBInFrustum(entity.getEntityBoundingBox());
    }

    public static boolean isBBInFrustum(AxisAlignedBB aabb) {
        FRUSTUM.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        return FRUSTUM.isBoundingBoxInFrustum(aabb);
    }

    public static void scaleStart(float x, float y, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(scale, scale, 1);
        GlStateManager.translate(-x, -y, 0);
    }

    public static void scaleEnd() {
        GlStateManager.popMatrix();
    }

    public static void customRotatedObject2D(float oXpos, float oYpos, float oWidth, float oHeight, double rotate) {
        GlStateManager.translate(oXpos + oWidth / 2, oYpos + oHeight / 2, 0);
        GL11.glRotated(rotate, 0.0, 0.0, 1.0);
        GlStateManager.translate(-oXpos - oWidth / 2, -oYpos - oHeight / 2, 0);
    }

    public static void setupOrientationMatrix(double x, double y, double z) {
        GlStateManager.translate(x - mc.getRenderManager().viewerPosX, y - mc.getRenderManager().viewerPosY, z - mc.getRenderManager().viewerPosZ);
    }

    public static void renderBlock(BlockPos blockPos, int color, boolean outline, boolean shade) {
        renderBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1, 1, 1, color, outline, shade);
    }

    public static void renderBox(int x, int y, int z, double x2, double y2, double z2, int color, boolean outline, boolean shade) {
        double xPos = x - mc.getRenderManager().viewerPosX;
        double yPos = y - mc.getRenderManager().viewerPosY;
        double zPos = z - mc.getRenderManager().viewerPosZ;
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(xPos, yPos, zPos, xPos + x2, yPos + y2, zPos + z2);
        drawAxisAlignedBB(axisAlignedBB, shade, outline,color);
    }

    public static void drawAxisAlignedBB(AxisAlignedBB axisAlignedBB, boolean outline, int color) {
        drawAxisAlignedBB(axisAlignedBB,outline,true,color);
    }

    public static void drawAxisAlignedBB(AxisAlignedBB axisAlignedBB,boolean filled, boolean outline, int color) {
        drawSelectionBoundingBox(axisAlignedBB, outline, filled,color);
    }

    public static void drawOutlineBoundingBox(final AxisAlignedBB bb,Color color) {
        RenderGlobal.drawOutlinedBoundingBox(bb,color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha());
    }

    public static void drawFilledBoundingBox(final AxisAlignedBB bb,Color color) {


        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(bb.minX, bb.minY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.maxY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.minY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.maxY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.minY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.maxY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.minY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.maxY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        tessellator.draw();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(bb.maxX, bb.maxY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.minY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.maxY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.minY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.maxY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.minY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.maxY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.minY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        tessellator.draw();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(bb.minX, bb.maxY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.maxY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.maxY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.maxY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.maxY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.maxY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.maxY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.maxY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        tessellator.draw();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(bb.minX, bb.minY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.minY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.minY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.minY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.minY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.minY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.minY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.minY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        tessellator.draw();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(bb.minX, bb.minY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.maxY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.minY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.maxY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.minY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.maxY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.minY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.maxY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        tessellator.draw();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(bb.minX, bb.maxY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.minY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.maxY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.minX, bb.minY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.maxY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.minY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.maxY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        worldrenderer.pos(bb.maxX, bb.minY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        tessellator.draw();
    }

    public static void drawSelectionBoundingBox(final AxisAlignedBB bb, final boolean outline, final boolean filled,int color) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GL11.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GL11.glDisable(2929);
        GlStateManager.depthMask(false);
        GlStateManager.pushMatrix();

        if (outline) {
            drawOutlineBoundingBox(bb,new Color(color,true));
        }
        if (filled) {
            drawFilledBoundingBox(bb,new Color(color,true));
        }

        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GL11.glEnable(2929);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawRoundedRect(float x, float y, float width, float height, float radius, int color) {
        float x1 = x + width, // @off
                y1 = y + height;
        final float f = (color >> 24 & 0xFF) / 255.0F,
                f1 = (color >> 16 & 0xFF) / 255.0F,
                f2 = (color >> 8 & 0xFF) / 255.0F,
                f3 = (color & 0xFF) / 255.0F; // @on
        GL11.glPushAttrib(0);
        GL11.glScaled(0.5, 0.5, 0.5);

        x *= 2;
        y *= 2;
        x1 *= 2;
        y1 *= 2;

        glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(f1, f2, f3, f);
        GlStateManager.enableBlend();
        glEnable(GL11.GL_LINE_SMOOTH);

        GL11.glBegin(GL11.GL_POLYGON);
        final double v = PI / 180;

        for (int i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(x + radius + MathHelper.sin((float) (i * v)) * (radius * -1), y + radius + MathHelper.cos((float) (i * v)) * (radius * -1));
        }

        for (int i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(x + radius + MathHelper.sin((float) (i * v)) * (radius * -1), y1 - radius + MathHelper.cos((float) (i * v)) * (radius * -1));
        }

        for (int i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(x1 - radius + MathHelper.sin((float) (i * v)) * radius, y1 - radius + MathHelper.cos((float) (i * v)) * radius);
        }

        for (int i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(x1 - radius + MathHelper.sin((float) (i * v)) * radius, y + radius + MathHelper.cos((float) (i * v)) * radius);
        }

        GL11.glEnd();

        glEnable(GL11.GL_TEXTURE_2D);
        glDisable(GL11.GL_LINE_SMOOTH);
        glEnable(GL11.GL_TEXTURE_2D);

        GL11.glScaled(2, 2, 2);

        GL11.glPopAttrib();
        GL11.glColor4f(1, 1, 1, 1);
    }

    public static void renderPlayer2D(EntityLivingBase abstractClientPlayer, final float x, final float y, final float size, float radius, int color) {
        if (abstractClientPlayer instanceof AbstractClientPlayer player) {
            StencilUtils.initStencilToWrite();
            RenderUtils.drawRoundedRect(x, y, size, size, radius, -1);
            StencilUtils.readStencilBuffer(1);
            RenderUtils.color(color);
            GLUtils.startBlend();
            mc.getTextureManager().bindTexture(player.getLocationSkin());
            Gui.drawScaledCustomSizeModalRect(x, y, (float) 8.0, (float) 8.0, 8, 8, size, size, 64.0F, 64.0F);
            GLUtils.endBlend();
            StencilUtils.uninitStencilBuffer();
        }
    }

    public static void renderItemStack(ItemStack stack, double x, double y, float scale) {
        renderItemStack(stack, x, y, scale, false);
    }

    public static void renderItemStack(ItemStack stack, double x, double y, float scale, boolean enchantedText) {
        renderItemStack(stack, x, y, scale, enchantedText, scale);
    }

    public static void renderItemStack(ItemStack stack, double x, double y, float scale, boolean enchantedText, float textScale) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, x);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
        mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, stack, 0, 0);
        if (enchantedText)
            renderEnchantText(stack, 0, 0, textScale);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void renderItemStack(EntityPlayer target, float x, float y, float scale, boolean enchantedText, float textScale) {
        List<ItemStack> items = new ArrayList<>();
        if (target.getHeldItem() != null) {
            items.add(target.getHeldItem());
        }
        for (int index = 3; index >= 0; index--) {
            ItemStack stack = target.inventory.armorInventory[index];
            if (stack != null) {
                items.add(stack);
            }
        }
        float i = x;

        for (ItemStack stack : items) {
            RenderUtils.renderItemStack(stack, i, y, scale, enchantedText, textScale);
            i += 16;
        }
    }

    public static void renderEnchantText(ItemStack stack, double x, double y, float scale) {
        int unBreakingLevel;
        RenderHelper.disableStandardItemLighting();
        double height = y;
        if (stack.getItem() instanceof ItemArmor) {
            int protectionLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack);
            int unBreakingLevel2 = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
            int thornLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack);
            if (protectionLevel > 0) {
                drawEnchantTag("P" + getColor(protectionLevel) + protectionLevel, x, height, scale);
                height += 8 * scale;
            }
            if (unBreakingLevel2 > 0) {
                drawEnchantTag("U" + getColor(unBreakingLevel2) + unBreakingLevel2, x, height, scale);
                height += 8 * scale;
            }
            if (thornLevel > 0) {
                drawEnchantTag("T" + getColor(thornLevel) + thornLevel, x, height, scale);
                height += 8 * scale;
            }
        }
        if (stack.getItem() instanceof ItemBow) {
            int powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
            int punchLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
            int flameLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack);
            unBreakingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
            if (powerLevel > 0) {
                drawEnchantTag("Pow" + getColor(powerLevel) + powerLevel, x, height, scale);
                height += 8 * scale;
            }
            if (punchLevel > 0) {
                drawEnchantTag("Pun" + getColor(punchLevel) + punchLevel, x, height, scale);
                height += 8 * scale;
            }
            if (flameLevel > 0) {
                drawEnchantTag("F" + getColor(flameLevel) + flameLevel, x, height, scale);
                height += 8 * scale;
            }
            if (unBreakingLevel > 0) {
                drawEnchantTag("U" + getColor(unBreakingLevel) + unBreakingLevel, x, height, scale);
                height += 8 * scale;
            }
        }
        if (stack.getItem() instanceof ItemSword) {
            int sharpnessLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack);
            int knockBackLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, stack);
            int fireAspectLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack);
            unBreakingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
            if (sharpnessLevel > 0) {
                drawEnchantTag("S" + getColor(sharpnessLevel) + sharpnessLevel, x, height, scale);
                height += 8 * scale;
            }
            if (knockBackLevel > 0) {
                drawEnchantTag("K" + getColor(knockBackLevel) + knockBackLevel, x, height, scale);
                height += 8 * scale;
            }
            if (fireAspectLevel > 0) {
                drawEnchantTag("F" + getColor(fireAspectLevel) + fireAspectLevel, x, height, scale);
                height += 8 * scale;
            }
            if (unBreakingLevel > 0) {
                drawEnchantTag("U" + getColor(unBreakingLevel) + unBreakingLevel, x, height, scale);
                height += 8 * scale;
            }
        }
        if (stack.getRarity() == EnumRarity.EPIC) {
            GlStateManager.pushMatrix();
            GlStateManager.disableDepth();
            GL11.glTranslated(x, y, x);
            GL11.glScaled(scale, scale, scale);
            mc.fontRendererObj.drawOutlinedString("God", (float) (x), (float) height, 1.0f, new Color(255, 255, 0).getRGB(), new Color(100, 100, 0, 140).getRGB());
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        }
    }

    private static void drawEnchantTag(String text, double x, double y, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GL11.glTranslated(x, y, x);
        GL11.glScaled(scale, scale, scale);
        mc.fontRendererObj.drawOutlinedString(text, (float) 0, (float) 0, 1.0f, -1, new Color(0, 0, 0, 140).getRGB());
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    private static String getColor(final int n) {
        if (n != 1) {
            if (n == 2) {
                return "§a";
            }
            if (n == 3) {
                return "§3";
            }
            if (n == 4) {
                return "§4";
            }
            if (n >= 5) {
                return "§e";
            }
        }
        return "§f";
    }

    public static String stripColor(final String input) {
        return COLOR_PATTERN.matcher(input).replaceAll("");
    }

    public static String sessionTime() {
        int elapsedTime = ((int)System.currentTimeMillis() - INSTANCE.getStartTime()) / 1000;
        String days = elapsedTime > 86400 ? elapsedTime / 86400 + "d " : "";
        elapsedTime = !days.isEmpty() ? elapsedTime % 86400 : elapsedTime;
        String hours = elapsedTime > 3600 ? elapsedTime / 3600 + "h " : "";
        elapsedTime = !hours.isEmpty() ? elapsedTime % 3600 : elapsedTime;
        String minutes = elapsedTime > 60 ? elapsedTime / 60 + "m " : "";
        elapsedTime = !minutes.isEmpty() ? elapsedTime % 60 : elapsedTime;
        String seconds = elapsedTime > 0 ? elapsedTime + "s " : "";
        return days + hours + minutes + seconds;
    }

    public static void drawExhiRect(float x, float y, float x2, float y2, float alpha) {
        x2 = x + x2;
        y2 = y + y2;
        Gui.drawRect(x - 3.5F, y - 3.5F, x2 + 3.5F, y2 + 3.5F, new Color(0, 0, 0, alpha).getRGB());
        Gui.drawRect(x - 3F, y - 3F, x2 + 3F, y2 + 3F, new Color(50F / 255F, 50F / 255F, 50F / 255F, alpha).getRGB());
        Gui.drawRect(x - 2.5F, y - 2.5F, x2 + 2.5F, y2 + 2.5F, new Color(26F / 255F, 26F / 255F, 26F / 255F, alpha).getRGB());
        Gui.drawRect(x - 0.5F, y - 0.5F, x2 + 0.5F, y2 + 0.5F, new Color(50F / 255F, 50F / 255F, 50F / 255F, alpha).getRGB());
        Gui.drawRect(x, y, x2, y2, new Color(18F / 255F, 18 / 255F, 18F / 255F, alpha).getRGB());
    }

    public static void drawGradientRect(final double left, final double top, double right, double bottom, final boolean sideways, final int startColor, final int endColor) {
        right = left + right;
        bottom = top + bottom;
        GL11.glDisable(3553);
        GLUtils.startBlend();
        GL11.glShadeModel(7425);
        GL11.glBegin(7);
        color(startColor);
        if (sideways) {
            GL11.glVertex2d(left, top);
            GL11.glVertex2d(left, bottom);
            color(endColor);
            GL11.glVertex2d(right, bottom);
            GL11.glVertex2d(right, top);
        } else {
            GL11.glVertex2d(left, top);
            color(endColor);
            GL11.glVertex2d(left, bottom);
            GL11.glVertex2d(right, bottom);
            color(startColor);
            GL11.glVertex2d(right, top);
        }
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glShadeModel(7424);
        //GLUtils.endBlend();
        GL11.glEnable(3553);
    }

    public static void drawBorder(float x, float y, float width, float height, final float outlineThickness, int outlineColor) {
        glEnable(GL_LINE_SMOOTH);
        color(outlineColor);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTexture2D();

        glLineWidth(outlineThickness);
        float cornerValue = (float) (outlineThickness * .19);

        glBegin(GL_LINES);
        glVertex2d(x, y - cornerValue);
        glVertex2d(x, y + height + cornerValue);
        glVertex2d(x + width, y + height + cornerValue);
        glVertex2d(x + width, y - cornerValue);
        glVertex2d(x, y);
        glVertex2d(x + width, y);
        glVertex2d(x, y + height);
        glVertex2d(x + width, y + height);
        glEnd();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();

        glDisable(GL_LINE_SMOOTH);
    }

    public static void drawBorderedRect(float x, float y, float width, float height, final float outlineThickness, int rectColor, int outlineColor) {
        drawRect(x,y,width,height,rectColor);
        drawBorder(x,y,width,height,outlineThickness,outlineColor);
    }
    public static void drawEntityOnScreen(float yaw, float pitch, EntityLivingBase entityLivingBase) {
        GlStateManager.resetColor();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, 0.0f, 50.0f);
        GlStateManager.scale(-50.0f, 50.0f, 50.0f);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        float renderYawOffset = entityLivingBase.renderYawOffset;
        float rotationYaw = entityLivingBase.rotationYaw;
        float rotationPitch = entityLivingBase.rotationPitch;
        float prevRotationYawHead = entityLivingBase.prevRotationYawHead;
        float rotationYawHead = entityLivingBase.rotationYawHead;
        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate((float)(-Math.atan(pitch / 40.0f) * 20.0), 1.0f, 0.0f, 0.0f);
        entityLivingBase.renderYawOffset = yaw - 0.4f;
        entityLivingBase.rotationYaw = yaw - 0.2f;
        entityLivingBase.rotationPitch = pitch;
        entityLivingBase.rotationYawHead = entityLivingBase.rotationYaw;
        entityLivingBase.prevRotationYawHead = entityLivingBase.rotationYaw;
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        RenderManager renderManager = mc.getRenderManager();
        renderManager.setPlayerViewY(180.0f);
        renderManager.setRenderShadow(false);
        renderManager.renderEntityWithPosYaw(entityLivingBase, 0.0, 0.0, 0.0, 0.0f, 1.0f);
        renderManager.setRenderShadow(true);
        entityLivingBase.renderYawOffset = renderYawOffset;
        entityLivingBase.rotationYaw = rotationYaw;
        entityLivingBase.rotationPitch = rotationPitch;
        entityLivingBase.prevRotationYawHead = prevRotationYawHead;
        entityLivingBase.rotationYawHead = rotationYawHead;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.resetColor();
    }

    public static void drawEntityOnScreen(float posX, float posY, float scale, EntityLivingBase ent) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.color(255, 255, 255);
        GlStateManager.translate(posX, posY, 50.0F);
        GlStateManager.scale(-scale, scale, scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        final RenderManager rendermanager = mc.getRenderManager();
        rendermanager.setPlayerViewY(1F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntityWithPosYaw(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
        rendermanager.setRenderShadow(true);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public static void drawElipse(float x, float y, float rx, float ry, float start, float end, float radius, int color, int stage1) {
        float sin;
        float cos;
        float i;
        GlStateManager.color(0, 0, 0, 0);
        float endOffset;
        if (start > end) {
            endOffset = end;
            end = start;
            start = endOffset;
        }
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(2);
        glBegin(GL11.GL_LINE_STRIP);
        for (i = start; i <= end; i += 2) {

            double stage = (i - start) / 360;

            int red = ((color >> 16) & 255);
            int green = ((color >> 8) & 255);
            int blue = ((color & 255));

            GL11.glColor4f(red / 255f, green / 255f, blue / 255f, 1);

            cos = (float) Math.cos(i * Math.PI / 180) * (radius / ry);
            sin = (float) Math.sin(i * Math.PI / 180) * (radius / rx);
            glVertex2f((x + cos), (y + sin));
        }
        glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        if (stage1 != -1) {
            cos = (float) Math.cos((start - 15) * Math.PI / 180) * (radius / ry);
            sin = (float) Math.sin((start - 15) * Math.PI / 180) * (radius / rx);

            switch (stage1) {
                case 0: {
                    Fonts.interBold.get(15).drawCenteredString("W", (x + cos), (y + sin), -1);
                    break;
                }
                case 1: {
                    Fonts.interBold.get(15).drawCenteredString("N", (x + cos), (y + sin), -1);
                    break;
                }
                case 2: {
                    Fonts.interBold.get(15).drawCenteredString("E", (x + cos), (y + sin), -1);

                    break;
                }
                case 3: {
                    Fonts.interBold.get(15).drawCenteredString("S", (x + cos), (y + sin), -1);
                    break;
                }
            }
        }
    }
    public static void drawEllipsCompass(int yaw, float x, float y, float x2, float y2, float radius, int color, boolean dir) {
        if (dir) {
            drawElipse(x, y, x2, y2, 15 + yaw, 75 + yaw, radius, color, 0);
            drawElipse(x, y, x2, y2, 105 + yaw, 165 + yaw, radius, color, 1);
            drawElipse(x, y, x2, y2, 195 + yaw, 255 + yaw, radius, color, 2);
            drawElipse(x, y, x2, y2, 285 + yaw, 345 + yaw, radius, color, 3);
        } else {
            drawElipse(x, y, x2, y2, 15 + yaw, 75 + yaw, radius, color, -1);
            drawElipse(x, y, x2, y2, 105 + yaw, 165 + yaw, radius, color, -1);
            drawElipse(x, y, x2, y2, 195 + yaw, 255 + yaw, radius, color, -1);
            drawElipse(x, y, x2, y2, 285 + yaw, 345 + yaw, radius, color, -1);
        }
    }

    public static void drawTracerPointer(float x, float y, float size, float tracerWidth, int color) {
        boolean blend = GL11.glIsEnabled(GL_BLEND);
        GL11.glEnable(GL_BLEND);
        boolean depth = GL11.glIsEnabled(GL_DEPTH_TEST);
        glDisable(GL_DEPTH_TEST);

        GL11.glDisable(GL_TEXTURE_2D);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL_LINE_SMOOTH);
        GL11.glPushMatrix();

        color(color);
        GL11.glBegin(7);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d((x - size * tracerWidth), (y + size));
        GL11.glVertex2d(x, (y + size - 3.63f));
        GL11.glVertex2d(x, y);
        GL11.glEnd();

        color(ColorUtils.darker(color, 0.8f));
        GL11.glBegin(7);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x, (y + size - 3.63f));
        GL11.glVertex2d((x + size * tracerWidth), (y + size));
        GL11.glVertex2d(x, y);
        GL11.glEnd();


        color(ColorUtils.darker(color, 0.6f));
        GL11.glBegin(7);
        GL11.glVertex2d((x - size * tracerWidth), (y + size));
        GL11.glVertex2d((x + size * tracerWidth), (y + size));
        GL11.glVertex2d(x, (y + size - 3.63f));
        GL11.glVertex2d((x - size * tracerWidth), (y + size));
        GL11.glEnd();
        GL11.glPopMatrix();

        GL11.glEnable(GL_TEXTURE_2D);
        if (!blend)
            GL11.glDisable(GL_BLEND);
        GL11.glDisable(GL_LINE_SMOOTH);

        if (depth)
            glEnable(GL_DEPTH_TEST);
    }

    public static void drawGradientCircle(float x, float y ,float start, float end,float radius,int color1,int color2) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(2f);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        var i = end;
        while (i >= start) {
            int c = ColorUtils.getGradientOffset(
                    new Color(color1),
                    new Color(color2),
                    (Math.abs(System.currentTimeMillis() / 360.0 + (i * 34 / 360) * 56 / 100) / 10)
            ).getRGB();
            float r = (c >> 16 & 0xFF) / 255f;
            float g = (c >> 8 & 0xFF) / 255f;
            float b = (c & 0xFF) / 255f;
            float a = (c >> 24 & 0xFF) / 255f;
            GlStateManager.color(r,g,b,a);
            GL11.glVertex2f((float) (x + Math.cos(i * PI / 180) * (radius * 1.001f)), (float) (y + Math.sin(i * PI / 180) * (radius * 1.001f)));
            i -= 360f / 90.0f;
        }
        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void color(int color, float alpha) {
        float f = (float)(color >> 16 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f2 = (float)(color & 0xFF) / 255.0f;
        GL11.glColor4f(f, f1, f2, alpha / 255.0f);
    }


    public static void drawFilledCircleNoGL(final float x, final float y, final double r, final int c, final int quality) {
        final float f = ((c >> 24) & 0xff) / 255F;
        final float f1 = ((c >> 16) & 0xff) / 255F;
        final float f2 = ((c >> 8) & 0xff) / 255F;
        final float f3 = (c & 0xff) / 255F;

        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);

        for (int i = 0; i <= 360 / quality; i++) {
            final double x2 = Math.sin(((i * quality * Math.PI) / 180)) * r;
            final double y2 = Math.cos(((i * quality * Math.PI) / 180)) * r;
            GL11.glVertex2d(x + x2, y + y2);
        }

        GL11.glEnd();
    }

    public static void glDrawTriangle(double x, double y, double x1, double y1, double x2, double y2, int colour) {
        GL11.glDisable(3553);
        GLUtils.startBlend();
        GL11.glEnable(2881);
        GL11.glHint(3155, 4354);
        RenderUtils.color(colour);
        GL11.glBegin(4);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x1, y1);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glEnable(3553);
        GLUtils.endBlend();
        GL11.glDisable(2881);
        GL11.glHint(3155, 4352);
    }

    public static void drawGoodCircle(double x, double y, float radius, int color) {
        color(color);
        GLUtils.setup2DRendering(() -> {
            glEnable(GL_POINT_SMOOTH);
            glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
            glPointSize(radius * (2 * Minecraft.getMinecraft().gameSettings.guiScale));
            GLUtils.render(GL_POINTS, () -> glVertex2d(x, y));
        });
    }
}
