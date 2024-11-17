package wtf.moonlight.features.modules.impl.visual;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.WorldEvent;
import wtf.moonlight.events.impl.render.*;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.modules.impl.misc.HackerDetector;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ColorValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.render.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "ESP", category = ModuleCategory.Visual)
public class ESP extends Module {
    public final BoolValue tags = new BoolValue("Tags", true, this);
    public final SliderValue tagsSize = new SliderValue("Tags Size", 0.5f, 0.1f, 2, 0.05f, this, tags::get);
    public final BoolValue tagsBackground = new BoolValue("Tags Background", true, this, tags::get);
    public final BoolValue item = new BoolValue("Item", true, this, tags::get);
    public final BoolValue esp2d = new BoolValue("2D ESP", true, this);
    public final BoolValue box = new BoolValue("Box", true, this, esp2d::get);
    public final ColorValue boxColor = new ColorValue("Box Color", Color.RED, this, () -> esp2d.get() && box.get());
    public final BoolValue healthBar = new BoolValue("Health Bar", true, this, esp2d::get);
    public final ColorValue absorptionColor = new ColorValue("Absorption Color", new Color(255, 255, 50), this, () -> esp2d.get() && healthBar.get());
    public final BoolValue armorBar = new BoolValue("Armor Bar", true, this, esp2d::get);
    public final ColorValue armorBarColor = new ColorValue("Armor Bar Color", new Color(50, 255, 255), this, () -> esp2d.get() && armorBar.get());
    public final BoolValue skeletons = new BoolValue("Skeletons", true, this, esp2d::get);
    public final SliderValue skeletonWidth = new SliderValue("Skeletons Width", 0.5f, 0.5f, 5, 0.5f, this, () -> esp2d.get() && skeletons.get());
    public final ColorValue skeletonsColor = new ColorValue("Skeletons Color", Color.WHITE, this, () -> esp2d.get() && skeletons.get());
    public final Map<EntityPlayer, float[][]> playerRotationMap = new HashMap<>();
    private final Map<EntityPlayer, float[]> entityPosMap = new HashMap<>();

    @Override
    public void onDisable() {
        entityPosMap.clear();
        playerRotationMap.clear();
    }

    @EventTarget
    public void onRenderNameTag(RenderNameTagEvent event) {
        if (tags.get() && entityPosMap.containsKey(event.getEntity()))
            event.setCancelled(true);
    }

    @EventTarget
    public void onWorld(WorldEvent event) {
        entityPosMap.clear();
        playerRotationMap.clear();
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {

        if (!esp2d.get() && !tags.get()) {
            return;
        }

        for (EntityPlayer player : entityPosMap.keySet()) {
            if ((player.getDistanceToEntity(mc.thePlayer) < 1.0F && mc.gameSettings.thirdPersonView == 0) ||
                    !RenderUtils.isBBInFrustum(player.getEntityBoundingBox()))
                continue;

            final float[] positions = entityPosMap.get(player);
            final float x = positions[0];
            final float y = positions[1];
            final float x2 = positions[2];
            final float y2 = positions[3];

            final float health = player.getHealth();
            final float maxHealth = player.getMaxHealth();
            final float healthPercentage = health / maxHealth;

            if (tags.get()) {
                final FontRenderer fontRenderer = mc.fontRendererObj;

                final String hacker = getModule(HackerDetector.class).isHacker(player) ? EnumChatFormatting.RED + "[Hacker] " + EnumChatFormatting.RESET : "";
                final String name = hacker + player.getDisplayName().getFormattedText() + " " + (MathUtils.roundToHalf(player.getHealth())) + EnumChatFormatting.RED + "❤";
                float halfWidth = (float) fontRenderer.getStringWidth(name) / 2 * tagsSize.get();
                final float xDif = x2 - x;
                final float middle = x + (xDif / 2);
                final float textHeight = fontRenderer.FONT_HEIGHT * tagsSize.get();
                float renderY = y - textHeight - 2;

                final float left = middle - halfWidth - 1;
                final float right = middle + halfWidth + 1;

                if (tagsBackground.get()) {
                    Gui.drawRect(left, renderY - 1, right, renderY + textHeight + 1, 0x96000000);
                }

                fontRenderer.drawScaledString(name, middle - halfWidth, renderY + 0.5F, tagsSize.get(), -1);

                if (item.get()) {
                    List<ItemStack> items = new ArrayList<>();
                    if (player.getHeldItem() != null) {
                        items.add(player.getHeldItem());
                    }
                    for (int index = 3; index >= 0; index--) {
                        ItemStack stack = player.inventory.armorInventory[index];
                        if (stack != null) {
                            items.add(stack);
                        }
                    }
                    float armorX = middle - ((float) (items.size() * 18) / 2) * tagsSize.get();

                    for (ItemStack stack : items) {
                        RenderUtils.renderItemStack(stack, armorX, renderY - 25 * tagsSize.get(), tagsSize.get() + tagsSize.get() / 2, true);
                        armorX += 18 * tagsSize.get();
                    }
                }
            }

            if (esp2d.get()) {
                glDisable(GL_TEXTURE_2D);
                GLUtils.startBlend();

                if (armorBar.get()) {
                    final float armorPercentage = player.getTotalArmorValue() / 20.0F;
                    final float armorBarWidth = (x2 - x) * armorPercentage;

                    glColor4ub((byte) 0, (byte) 0, (byte) 0, (byte) 0x96);
                    glBegin(GL_QUADS);

                    // Background
                    {
                        glVertex2f(x, y2 + 0.5F);
                        glVertex2f(x, y2 + 2.5F);

                        glVertex2f(x2, y2 + 2.5F);
                        glVertex2f(x2, y2 + 0.5F);
                    }

                    if (armorPercentage > 0) {
                        RenderUtils.color(armorBarColor.get().getRGB());

                        // Bar
                        {
                            glVertex2f(x + 0.5F, y2 + 1);
                            glVertex2f(x + 0.5F, y2 + 2);

                            glVertex2f(x + armorBarWidth - 0.5F, y2 + 2);
                            glVertex2f(x + armorBarWidth - 0.5F, y2 + 1);
                        }
                    }

                    if (!healthBar.get())
                        glEnd();
                }

                if (healthBar.get()) {
                    float healthBarLeft = x - 2.5F;
                    float healthBarRight = x - 0.5F;

                    glColor4ub((byte) 0, (byte) 0, (byte) 0, (byte) 0x96);

                    if (!armorBar.get())
                        glBegin(GL_QUADS);

                    // Background
                    {
                        glVertex2f(healthBarLeft, y);
                        glVertex2f(healthBarLeft, y2);

                        glVertex2f(healthBarRight, y2);
                        glVertex2f(healthBarRight, y);
                    }

                    healthBarLeft += 0.5F;
                    healthBarRight -= 0.5F;

                    final float heightDif = y - y2;
                    final float healthBarHeight = heightDif * healthPercentage;

                    final float topOfHealthBar = y2 + 0.5F + healthBarHeight;

                    final int color = ColorUtils.getColorFromPercentage(healthPercentage);

                    RenderUtils.color(color);

                    // Bar
                    {
                        glVertex2f(healthBarLeft, topOfHealthBar);
                        glVertex2f(healthBarLeft, y2 - 0.5F);

                        glVertex2f(healthBarRight, y2 - 0.5F);
                        glVertex2f(healthBarRight, topOfHealthBar);
                    }


                    final float absorption = player.getAbsorptionAmount();

                    final float absorptionPercentage = Math.min(1.0F, absorption / 20.0F);

                    final int absorptionColor = this.absorptionColor.get().getRGB();

                    final float absorptionHeight = heightDif * absorptionPercentage;

                    final float topOfAbsorptionBar = y2 + 0.5F + absorptionHeight;

                    RenderUtils.color(absorptionColor);

                    // Absorption Bar
                    {
                        glVertex2f(healthBarLeft, topOfAbsorptionBar);
                        glVertex2f(healthBarLeft, y2 - 0.5F);

                        glVertex2f(healthBarRight, y2 - 0.5F);
                        glVertex2f(healthBarRight, topOfAbsorptionBar);
                    }

                    if (!box.get())
                        glEnd();
                }

                if (box.get()) {
                    glColor4ub((byte) 0, (byte) 0, (byte) 0, (byte) 0x96);
                    if (!healthBar.get())
                        glBegin(GL_QUADS);

                    // Background
                    {
                        // Left
                        glVertex2f(x, y);
                        glVertex2f(x, y2);
                        glVertex2f(x + 1.5F, y2);
                        glVertex2f(x + 1.5F, y);

                        // Right
                        glVertex2f(x2 - 1.5F, y);
                        glVertex2f(x2 - 1.5F, y2);
                        glVertex2f(x2, y2);
                        glVertex2f(x2, y);

                        // Top
                        glVertex2f(x + 1.5F, y);
                        glVertex2f(x + 1.5F, y + 1.5F);
                        glVertex2f(x2 - 1.5F, y + 1.5F);
                        glVertex2f(x2 - 1.5F, y);

                        // Bottom
                        glVertex2f(x + 1.5F, y2 - 1.5F);
                        glVertex2f(x + 1.5F, y2);
                        glVertex2f(x2 - 1.5F, y2);
                        glVertex2f(x2 - 1.5F, y2 - 1.5F);
                    }

                    RenderUtils.color(boxColor.get().getRGB());

                    // Box
                    {
                        // Left
                        glVertex2f(x + 0.5F, y + 0.5F);
                        glVertex2f(x + 0.5F, y2 - 0.5F);
                        glVertex2f(x + 1, y2 - 0.5F);
                        glVertex2f(x + 1, y + 0.5F);

                        // Right
                        glVertex2f(x2 - 1, y + 0.5F);
                        glVertex2f(x2 - 1, y2 - 0.5F);
                        glVertex2f(x2 - 0.5F, y2 - 0.5F);
                        glVertex2f(x2 - 0.5F, y + 0.5F);

                        // Top
                        glVertex2f(x + 0.5F, y + 0.5F);
                        glVertex2f(x + 0.5F, y + 1);
                        glVertex2f(x2 - 0.5F, y + 1);
                        glVertex2f(x2 - 0.5F, y + 0.5F);

                        // Bottom
                        glVertex2f(x + 0.5F, y2 - 1);
                        glVertex2f(x + 0.5F, y2 - 0.5F);
                        glVertex2f(x2 - 0.5F, y2 - 0.5F);
                        glVertex2f(x2 - 0.5F, y2 - 1);
                    }

                    glEnd();

                }

                glEnable(GL_TEXTURE_2D);
                GLUtils.endBlend();
            }
        }
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        final boolean skeletons = this.skeletons.get();
        final boolean project2D = esp2d.get() || tags.get();
        if (project2D && !entityPosMap.isEmpty())
            entityPosMap.clear();

        if (skeletons) {
            glLineWidth(skeletonWidth.get());
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glEnable(GL_LINE_SMOOTH);
            RenderUtils.color(skeletonsColor.get().getRGB());
            glDisable(GL_DEPTH_TEST);
            glDisable(GL_TEXTURE_2D);
            glDepthMask(false);
        }

        final float partialTicks = event.getPartialTicks();

        for (final EntityPlayer player : mc.theWorld.playerEntities) {
            if (project2D) {
                final double posX = (MathUtils.interpolate(player.prevPosX, player.posX, partialTicks) -
                        mc.getRenderManager().viewerPosX);
                final double posY = (MathUtils.interpolate(player.prevPosY, player.posY, partialTicks) -
                        mc.getRenderManager().viewerPosY);
                final double posZ = (MathUtils.interpolate(player.prevPosZ, player.posZ, partialTicks) -
                        mc.getRenderManager().viewerPosZ);

                final double halfWidth = player.width / 2.0D;
                final AxisAlignedBB bb = new AxisAlignedBB(posX - halfWidth, posY, posZ - halfWidth,
                        posX + halfWidth, posY + player.height + (player.isSneaking() ? -0.2D : 0.1D), posZ + halfWidth).expand(0.1, 0.1, 0.1);

                final double[][] vectors = {{bb.minX, bb.minY, bb.minZ},
                        {bb.minX, bb.maxY, bb.minZ},
                        {bb.minX, bb.maxY, bb.maxZ},
                        {bb.minX, bb.minY, bb.maxZ},
                        {bb.maxX, bb.minY, bb.minZ},
                        {bb.maxX, bb.maxY, bb.minZ},
                        {bb.maxX, bb.maxY, bb.maxZ},
                        {bb.maxX, bb.minY, bb.maxZ}};

                float[] projection;
                final float[] position = new float[]{Float.MAX_VALUE, Float.MAX_VALUE, -1.0F, -1.0F};

                for (final double[] vec : vectors) {
                    projection = GLUtils.project2D((float) vec[0], (float) vec[1], (float) vec[2], event.getScaledResolution().getScaleFactor());
                    if (projection != null && projection[2] >= 0.0F && projection[2] < 1.0F) {
                        final float pX = projection[0];
                        final float pY = projection[1];
                        position[0] = Math.min(position[0], pX);
                        position[1] = Math.min(position[1], pY);
                        position[2] = Math.max(position[2], pX);
                        position[3] = Math.max(position[3], pY);
                    }
                }

                entityPosMap.put(player, position);
            }

            if (skeletons) {
                drawSkeleton(partialTicks, player);
            }
        }

        if (skeletons) {
            glDepthMask(true);
            glDisable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
            glDisable(GL_LINE_SMOOTH);
            glEnable(GL_DEPTH_TEST);
        }
    }

    private void drawSkeleton(float pt, EntityPlayer player) {
        float[][] entPos;
        if ((entPos = playerRotationMap.get(player)) != null) {
            glPushMatrix();
            float x = (float) (MathUtils.interpolate(player.prevPosX, player.posX, pt) -
                    mc.getRenderManager().renderPosX);
            float y = (float) (MathUtils.interpolate(player.prevPosY, player.posY, pt) -
                    mc.getRenderManager().renderPosY);
            float z = (float) (MathUtils.interpolate(player.prevPosZ, player.posZ, pt) -
                    mc.getRenderManager().renderPosZ);
            glTranslated(x, y, z);
            boolean sneaking = player.isSneaking();

            final float xOff = MathUtils.interpolate(player.prevRenderYawOffset, player.renderYawOffset, pt);
            float yOff = sneaking ? 0.6F : 0.75F;
            glRotatef(-xOff, 0.0F, 1.0F, 0.0F);
            glTranslatef(0.0F, 0.0F, sneaking ? -0.235F : 0.0F);

            // Right leg
            glPushMatrix();
            glTranslatef(-0.125F, yOff, 0.0F);
            if (entPos[3][0] != 0.0F)
                glRotatef(entPos[3][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
            if (entPos[3][1] != 0.0F)
                glRotatef(entPos[3][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
            if (entPos[3][2] != 0.0F)
                glRotatef(entPos[3][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
            glBegin(GL_LINE_STRIP);
            glVertex3i(0, 0, 0);
            glVertex3f(0.0F, -yOff, 0.0F);
            glEnd();
            glPopMatrix();

            // Left leg
            glPushMatrix();
            glTranslatef(0.125F, yOff, 0.0F);
            if (entPos[4][0] != 0.0F)
                glRotatef(entPos[4][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
            if (entPos[4][1] != 0.0F)
                glRotatef(entPos[4][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
            if (entPos[4][2] != 0.0F)
                glRotatef(entPos[4][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
            glBegin(GL_LINE_STRIP);
            glVertex3i(0, 0, 0);
            glVertex3f(0.0F, -yOff, 0.0F);
            glEnd();
            glPopMatrix();

            glTranslatef(0.0F, 0.0F, sneaking ? 0.25F : 0.0F);
            glPushMatrix();
            glTranslatef(0.0F, sneaking ? -0.05F : 0.0F, sneaking ? -0.01725F : 0.0F);

            // Right arm
            glPushMatrix();
            glTranslatef(-0.375F, yOff + 0.55F, 0.0F);
            if (entPos[1][0] != 0.0F)
                glRotatef(entPos[1][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
            if (entPos[1][1] != 0.0F)
                glRotatef(entPos[1][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
            if (entPos[1][2] != 0.0F)
                glRotatef(-entPos[1][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
            glBegin(GL_LINE_STRIP);
            glVertex3i(0, 0, 0);
            glVertex3f(0.0F, -0.5F, 0.0F);
            glEnd();
            glPopMatrix();

            // Left arm
            glPushMatrix();
            glTranslatef(0.375F, yOff + 0.55F, 0.0F);
            if (entPos[2][0] != 0.0F)
                glRotatef(entPos[2][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
            if (entPos[2][1] != 0.0F)
                glRotatef(entPos[2][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
            if (entPos[2][2] != 0.0F)
                glRotatef(-entPos[2][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
            glBegin(GL_LINE_STRIP);
            glVertex3i(0, 0, 0);
            glVertex3f(0.0F, -0.5F, 0.0F);
            glEnd();
            glPopMatrix();

            glRotatef(xOff - player.rotationYawHead, 0.0F, 1.0F, 0.0F);

            // Head
            glPushMatrix();
            glTranslatef(0.0F, yOff + 0.55F, 0.0F);
            if (entPos[0][0] != 0.0F)
                glRotatef(entPos[0][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
            glBegin(GL_LINE_STRIP);
            glVertex3i(0, 0, 0);
            glVertex3f(0.0F, 0.3F, 0.0F);
            glEnd();
            glPopMatrix();

            glPopMatrix();

            glRotatef(sneaking ? 25.0F : 0.0F, 1.0F, 0.0F, 0.0F);
            glTranslatef(0.0F, sneaking ? -0.16175F : 0.0F, sneaking ? -0.48025F : 0.0F);

            // Pelvis
            glPushMatrix();
            glTranslated(0.0F, yOff, 0.0F);
            glBegin(GL_LINE_STRIP);
            glVertex3f(-0.125F, 0.0F, 0.0F);
            glVertex3f(0.125F, 0.0F, 0.0F);
            glEnd();
            glPopMatrix();

            // Body
            glPushMatrix();
            glTranslatef(0.0F, yOff, 0.0F);
            glBegin(GL_LINE_STRIP);
            glVertex3i(0, 0, 0);
            glVertex3f(0.0F, 0.55F, 0.0F);
            glEnd();
            glPopMatrix();

            // Chest
            glPushMatrix();
            glTranslatef(0.0F, yOff + 0.55F, 0.0F);
            glBegin(GL_LINE_STRIP);
            glVertex3f(-0.375F, 0.0F, 0.0F);
            glVertex3f(0.375F, 0.0F, 0.0F);
            glEnd();
            glPopMatrix();

            glPopMatrix();
        }
    }

    public boolean isValid(Entity entity) {
        if (entity instanceof EntityPlayer player) {

            if (!player.isEntityAlive()) {
                return false;
            }

            if (player == mc.thePlayer) {
                return false;
            }

            return RenderUtils.isBBInFrustum(entity.getEntityBoundingBox()) && mc.theWorld.playerEntities.contains(player);
        }

        return false;
    }

    public boolean shouldDrawSkeletons() {
        return isEnabled() && skeletons.get();
    }

    public void addEntity(EntityPlayer e, ModelPlayer model) {
        playerRotationMap.put(e, new float[][]{
                {model.bipedHead.rotateAngleX, model.bipedHead.rotateAngleY, model.bipedHead.rotateAngleZ},
                {model.bipedRightArm.rotateAngleX, model.bipedRightArm.rotateAngleY, model.bipedRightArm.rotateAngleZ},
                {model.bipedLeftArm.rotateAngleX, model.bipedLeftArm.rotateAngleY, model.bipedLeftArm.rotateAngleZ},
                {model.bipedRightLeg.rotateAngleX, model.bipedRightLeg.rotateAngleY, model.bipedRightLeg.rotateAngleZ},
                {model.bipedLeftLeg.rotateAngleX, model.bipedLeftLeg.rotateAngleY, model.bipedLeftLeg.rotateAngleZ}
        });
    }
}