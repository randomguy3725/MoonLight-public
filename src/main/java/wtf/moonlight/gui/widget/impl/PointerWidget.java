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
package wtf.moonlight.gui.widget.impl;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import wtf.moonlight.events.impl.render.Shader2DEvent;
import wtf.moonlight.gui.widget.Widget;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.render.RenderUtils;

import java.awt.*;

public class PointerWidget extends Widget {
    public PointerWidget() {
        super("Pointer");
        this.x = 0.5f;
        this.y = 0.7f;
        height = 50;
        width = 50;
    }

    @Override
    public void onShader(Shader2DEvent event) {
        GlStateManager.pushMatrix();

        float pitch = Math.abs(90f / clamp2(mc.thePlayer.rotationPitch, 42, 90f));
        RenderUtils.drawEllipsCompass(-(int) mc.thePlayer.rotationYaw, renderX, renderY, pitch, 1f, 68 - 2, setting.color(), false);
        RenderUtils.drawEllipsCompass(-(int) mc.thePlayer.rotationYaw, renderX, renderY, pitch, 1f, 68 - 2.5f, setting.color(), false);

        RenderUtils.drawEllipsCompass(-(int) mc.thePlayer.rotationYaw, renderX, renderY, pitch, 1f, 68, Color.WHITE.getRGB(), true);

        GlStateManager.popMatrix();

        float tracerRadius = renderX;
        float yOffset = renderY;

        GlStateManager.pushMatrix();
        GlStateManager.translate(renderX, renderY, 0);
        GL11.glRotatef(90f / Math.abs(90f / clamp2(mc.thePlayer.rotationPitch, 42, 90f)) - 90 - 12 ,1.0f, 0.0f, 0.0f);
        GlStateManager.translate(-renderX, -renderY, 0);

        for (EntityPlayer e : mc.theWorld.playerEntities) {
            if (e != mc.thePlayer) {
                GL11.glPushMatrix();
                float yaw = getRotations(e) - mc.thePlayer.rotationYaw;
                GL11.glTranslatef(tracerRadius, yOffset, 0.0F);
                GL11.glRotatef(yaw, 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(-tracerRadius, -yOffset, 0.0F);
                RenderUtils.drawTracerPointer(tracerRadius, yOffset - 68, 2.28f * 5,0.44F,-1);
                GL11.glTranslatef(tracerRadius, yOffset, 0.0F);
                GL11.glRotatef(-yaw, 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(-tracerRadius, -yOffset, 0.0F);
                GL11.glColor4f(1F, 1F, 1F, 1F);
                GL11.glPopMatrix();
            }
        }
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GlStateManager.popMatrix();
    }

    @Override
    public void render() {

        GlStateManager.pushMatrix();
        
        float pitch = Math.abs(90f / clamp2(mc.thePlayer.rotationPitch, 42, 90f));
        RenderUtils.drawEllipsCompass(-(int) mc.thePlayer.rotationYaw, renderX, renderY, pitch, 1f, 68 - 2, setting.color(), false);
        RenderUtils.drawEllipsCompass(-(int) mc.thePlayer.rotationYaw, renderX, renderY, pitch, 1f, 68 - 2.5f, setting.color(), false);

        RenderUtils.drawEllipsCompass(-(int) mc.thePlayer.rotationYaw, renderX, renderY, pitch, 1f, 68, Color.WHITE.getRGB(), true);
        
        GlStateManager.popMatrix();

        float tracerRadius = renderX;
        float yOffset = renderY;

        GlStateManager.pushMatrix();
        GlStateManager.translate(renderX, renderY, 0);
        GL11.glRotatef(90f / Math.abs(90f / clamp2(mc.thePlayer.rotationPitch, 42, 90f)) - 90 - 12 ,1.0f, 0.0f, 0.0f);
        GlStateManager.translate(-renderX, -renderY, 0);

        for (EntityPlayer e : mc.theWorld.playerEntities) {
            if (e != mc.thePlayer) {
                GL11.glPushMatrix();
                float yaw = getRotations(e) - mc.thePlayer.rotationYaw;
                GL11.glTranslatef(tracerRadius, yOffset, 0.0F);
                GL11.glRotatef(yaw, 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(-tracerRadius, -yOffset, 0.0F);
                RenderUtils.drawTracerPointer(tracerRadius, yOffset - 68, 2.28f * 5,0.44F,-1);
                GL11.glTranslatef(tracerRadius, yOffset, 0.0F);
                GL11.glRotatef(-yaw, 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(-tracerRadius, -yOffset, 0.0F);
                GL11.glColor4f(1F, 1F, 1F, 1F);
                GL11.glPopMatrix();
            }
        }
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GlStateManager.popMatrix();
    }

    @Override
    public boolean shouldRender() {
        return setting.isEnabled() && setting.elements.isEnabled("Pointer");
    }
    public float clamp2(float num, float min, float max) {
        if (num < min) {
            return min;
        } else {
            return Math.min(num, max);
        }
    }
    public float getRotations(Entity entity) {
        double x = MathUtils.interpolate(entity.lastTickPosX, entity.posX) - MathUtils.interpolate(mc.thePlayer.lastTickPosX, mc.thePlayer.posX);
        double z = MathUtils.interpolate(entity.lastTickPosZ, entity.posZ) - MathUtils.interpolate(mc.thePlayer.lastTickPosZ, mc.thePlayer.posZ);
        return (float) -(Math.atan2(x, z) * (180 / Math.PI));
    }
}
