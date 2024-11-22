package net.minecraft.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import net.optifine.DynamicLights;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.GL11;
import wtf.moonlight.MoonLight;
import wtf.moonlight.features.modules.impl.combat.KillAura;
import wtf.moonlight.features.modules.impl.visual.Animations;
import wtf.moonlight.features.modules.impl.visual.Camera;
import wtf.moonlight.utils.misc.SpoofSlotUtils;

public class ItemRenderer
{
    private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
    private static final ResourceLocation RES_UNDERWATER_OVERLAY = new ResourceLocation("textures/misc/underwater.png");
    private final Minecraft mc;
    private ItemStack itemToRender;
    private float equippedProgress;
    private float prevEquippedProgress;
    private final RenderManager renderManager;
    private final RenderItem itemRenderer;
    private int equippedItemSlot = -1;

    public ItemRenderer(Minecraft mcIn)
    {
        this.mc = mcIn;
        this.renderManager = mcIn.getRenderManager();
        this.itemRenderer = mcIn.getRenderItem();
    }

    public void renderItem(EntityLivingBase entityIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform)
    {
        if (heldStack != null)
        {
            Item item = heldStack.getItem();
            Block block = Block.getBlockFromItem(item);
            GlStateManager.pushMatrix();

            if (this.itemRenderer.shouldRenderItemIn3D(heldStack))
            {
                GlStateManager.scale(2.0F, 2.0F, 2.0F);

                if (this.isBlockTranslucent(block) && (!Config.isShaders() || !Shaders.renderItemKeepDepthMask))
                {
                    GlStateManager.depthMask(false);
                }
            } else {
                Animations animations = MoonLight.INSTANCE.getModuleManager().getModule(Animations.class);
                if (animations.isEnabled()) {
                    double factor = 1 - animations.getDownscaleFactor().get();
                    GlStateManager.scale(factor, factor, factor);
                }
            }

            this.itemRenderer.renderItemModelForEntity(heldStack, entityIn, transform);

            if (this.isBlockTranslucent(block))
            {
                GlStateManager.depthMask(true);
            }

            GlStateManager.popMatrix();
        }
    }

    private boolean isBlockTranslucent(Block blockIn)
    {
        return blockIn != null && blockIn.getBlockLayer() == EnumWorldBlockLayer.TRANSLUCENT;
    }

    private void rotateArroundXAndY(float angle, float angleY)
    {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(angle, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(angleY, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    private void setLightMapFromPlayer(AbstractClientPlayer clientPlayer)
    {
        int i = this.mc.theWorld.getCombinedLight(new BlockPos(clientPlayer.posX, clientPlayer.posY + (double)clientPlayer.getEyeHeight(), clientPlayer.posZ), 0);

        if (Config.isDynamicLights())
        {
            i = DynamicLights.getCombinedLight(this.mc.getRenderViewEntity(), i);
        }

        float f = (float)(i & 65535);
        float f1 = (float)(i >> 16);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f, f1);
    }

    private void rotateWithPlayerRotations(EntityPlayerSP entityplayerspIn, float partialTicks)
    {
        float f = entityplayerspIn.prevRenderArmPitch + (entityplayerspIn.renderArmPitch - entityplayerspIn.prevRenderArmPitch) * partialTicks;
        float f1 = entityplayerspIn.prevRenderArmYaw + (entityplayerspIn.renderArmYaw - entityplayerspIn.prevRenderArmYaw) * partialTicks;
        GlStateManager.rotate((entityplayerspIn.rotationPitch - f) * 0.1F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((entityplayerspIn.rotationYaw - f1) * 0.1F, 0.0F, 1.0F, 0.0F);
    }

    private float getMapAngleFromPitch(float pitch)
    {
        float f = 1.0F - pitch / 45.0F + 0.1F;
        f = MathHelper.clamp_float(f, 0.0F, 1.0F);
        f = -MathHelper.cos(f * (float)Math.PI) * 0.5F + 0.5F;
        return f;
    }

    private void renderRightArm(RenderPlayer renderPlayerIn)
    {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(54.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(64.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-62.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(0.25F, -0.85F, 0.75F);
        renderPlayerIn.renderRightArm(this.mc.thePlayer);
        GlStateManager.popMatrix();
    }

    private void renderLeftArm(RenderPlayer renderPlayerIn)
    {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(92.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(41.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(-0.3F, -1.1F, 0.45F);
        renderPlayerIn.renderLeftArm(this.mc.thePlayer);
        GlStateManager.popMatrix();
    }

    private void renderPlayerArms(AbstractClientPlayer clientPlayer)
    {
        this.mc.getTextureManager().bindTexture(clientPlayer.getLocationSkin());
        Render<AbstractClientPlayer> render = this.renderManager.getEntityRenderObject(this.mc.thePlayer);
        RenderPlayer renderplayer = (RenderPlayer)render;

        if (!clientPlayer.isInvisible())
        {
            GlStateManager.disableCull();
            this.renderRightArm(renderplayer);
            this.renderLeftArm(renderplayer);
            GlStateManager.enableCull();
        }
    }

    private void renderItemMap(AbstractClientPlayer clientPlayer, float pitch, float equipmentProgress, float swingProgress)
    {
        float f = -0.4F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
        float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI * 2.0F);
        float f2 = -0.2F * MathHelper.sin(swingProgress * (float)Math.PI);
        GlStateManager.translate(f, f1, f2);
        float f3 = this.getMapAngleFromPitch(pitch);
        GlStateManager.translate(0.0F, 0.04F, -0.72F);
        GlStateManager.translate(0.0F, equipmentProgress * -1.2F, 0.0F);
        GlStateManager.translate(0.0F, f3 * -0.5F, 0.0F);
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f3 * -85.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
        this.renderPlayerArms(clientPlayer);
        float f4 = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float f5 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
        GlStateManager.rotate(f4 * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f5 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f5 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.38F, 0.38F, 0.38F);
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(-1.0F, -1.0F, 0.0F);
        GlStateManager.scale(0.015625F, 0.015625F, 0.015625F);
        this.mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GL11.glNormal3f(0.0F, 0.0F, -1.0F);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(-7.0D, 135.0D, 0.0D).tex(0.0D, 1.0D).endVertex();
        worldrenderer.pos(135.0D, 135.0D, 0.0D).tex(1.0D, 1.0D).endVertex();
        worldrenderer.pos(135.0D, -7.0D, 0.0D).tex(1.0D, 0.0D).endVertex();
        worldrenderer.pos(-7.0D, -7.0D, 0.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
        MapData mapdata = Items.filled_map.getMapData(this.itemToRender, this.mc.theWorld);

        if (mapdata != null)
        {
            this.mc.entityRenderer.getMapItemRenderer().renderMap(mapdata, false);
        }
    }

    private void renderPlayerArm(AbstractClientPlayer clientPlayer, float equipProgress, float swingProgress)
    {
        float f = -0.3F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
        float f1 = 0.4F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI * 2.0F);
        float f2 = -0.4F * MathHelper.sin(swingProgress * (float)Math.PI);
        GlStateManager.translate(f, f1, f2);
        GlStateManager.translate(0.64000005F, -0.6F, -0.71999997F);
        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float f3 = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float f4 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
        GlStateManager.rotate(f4 * 70.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f3 * -20.0F, 0.0F, 0.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(clientPlayer.getLocationSkin());
        GlStateManager.translate(-1.0F, 3.6F, 3.5F);
        GlStateManager.rotate(120.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(1.0F, 1.0F, 1.0F);
        GlStateManager.translate(5.6F, 0.0F, 0.0F);
        Render<AbstractClientPlayer> render = this.renderManager.getEntityRenderObject(this.mc.thePlayer);
        GlStateManager.disableCull();
        RenderPlayer renderplayer = (RenderPlayer)render;
        renderplayer.renderRightArm(this.mc.thePlayer);
        GlStateManager.enableCull();
    }

    private void doItemUsedTransformations(float swingProgress)
    {
        float f = -0.4F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
        float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI * 2.0F);
        float f2 = -0.2F * MathHelper.sin(swingProgress * (float)Math.PI);
        GlStateManager.translate(f, f1, f2);
    }

    private void performDrinking(AbstractClientPlayer clientPlayer, float partialTicks)
    {
        float f = (float)clientPlayer.getItemInUseCount() - partialTicks + 1.0F;
        float f1 = f / (float)this.itemToRender.getMaxItemUseDuration();
        float f2 = MathHelper.abs(MathHelper.cos(f / 4.0F * (float)Math.PI) * 0.1F);

        if (f1 >= 0.8F)
        {
            f2 = 0.0F;
        }

        GlStateManager.translate(0.0F, f2, 0.0F);
        float f3 = 1.0F - (float)Math.pow(f1, 27.0D);
        GlStateManager.translate(f3 * 0.6F, f3 * -0.5F, f3 * 0.0F);
        GlStateManager.rotate(f3 * 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f3 * 10.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f3 * 30.0F, 0.0F, 0.0F, 1.0F);
    }

    private void transformFirstPersonItem(float equipProgress, float swingProgress)
    {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float f = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
        GlStateManager.rotate(f * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f1 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }

    private void doBowTransformations(float partialTicks, AbstractClientPlayer clientPlayer)
    {
        GlStateManager.rotate(-18.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(-12.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-8.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(-0.9F, 0.2F, 0.0F);
        float f = (float)this.itemToRender.getMaxItemUseDuration() - ((float)clientPlayer.getItemInUseCount() - partialTicks + 1.0F);
        float f1 = f / 20.0F;
        f1 = (f1 * f1 + f1 * 2.0F) / 3.0F;

        if (f1 > 1.0F)
        {
            f1 = 1.0F;
        }

        if (f1 > 0.1F)
        {
            float f2 = MathHelper.sin((f - 0.1F) * 1.3F);
            float f3 = f1 - 0.1F;
            float f4 = f2 * f3;
            GlStateManager.translate(f4 * 0.0F, f4 * 0.01F, f4 * 0.0F);
        }

        GlStateManager.translate(f1 * 0.0F, f1 * 0.0F, f1 * 0.1F);
        GlStateManager.scale(1.0F, 1.0F, 1.0F + f1 * 0.2F);
    }

    private void doBlockTransformations()
    {
        GlStateManager.translate(-0.5F, 0.2F, 0.0F);
        GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
    }

    private int i = 0;

    public void renderItemInFirstPerson(float partialTicks)
    {
        if (!Config.isShaders() || !Shaders.isSkipRenderHand())
        {
            Animations animations = MoonLight.INSTANCE.getModuleManager().getModule(Animations.class);
            float f = 1.0F - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
            AbstractClientPlayer abstractclientplayer = this.mc.thePlayer;
            float f1 = abstractclientplayer.getSwingProgress(partialTicks);
            float f2 = abstractclientplayer.prevRotationPitch + (abstractclientplayer.rotationPitch - abstractclientplayer.prevRotationPitch) * partialTicks;
            float f3 = abstractclientplayer.prevRotationYaw + (abstractclientplayer.rotationYaw - abstractclientplayer.prevRotationYaw) * partialTicks;
            float var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * MathHelper.PI);
            this.rotateArroundXAndY(f2, f3);
            this.setLightMapFromPlayer(abstractclientplayer);
            this.rotateWithPlayerRotations((EntityPlayerSP)abstractclientplayer, partialTicks);
            GlStateManager.enableRescaleNormal();
            GlStateManager.pushMatrix();
            KillAura aura = MoonLight.INSTANCE.getModuleManager()
                    .getModule(KillAura.class);
            i++;
            if (animations.isEnabled()){
                if (mc.thePlayer.getHeldItem() != null)
                    GL11.glTranslated(animations.getX().get(), animations.getY().get(), animations.getZ().get());
            }

            if (this.itemToRender != null)
            {
                if (this.itemToRender.getItem() instanceof ItemMap)
                {
                    this.renderItemMap(abstractclientplayer, f2, f, f1);
                }

                else if (abstractclientplayer.getItemInUseCount() > 0 || (aura.isHoldingSword() &&(aura.forceDisplayBlocking.get() && aura.renderBlocking || !aura.forceDisplayBlocking.get() && aura.isBlocking || animations.getBlockWhenSwing().get() && mc.thePlayer.isSwingInProgress)))
                {
                    EnumAction enumaction = this.itemToRender.getItemUseAction();

                    switch (enumaction)
                    {
                        case NONE:
                            this.transformFirstPersonItem(f, 0.0F);
                            break;

                        case EAT:
                        case DRINK:
                            this.performDrinking(abstractclientplayer, partialTicks);
                            this.transformFirstPersonItem(f, animations.isEnabled() && animations.getOld().get() ? f1 : 0);
                            break;

                        case BLOCK:
                            if (animations.isEnabled()) {
                                if (mc.thePlayer.getHeldItem() != null)
                                    GL11.glTranslated(animations.getBx().get(), animations.getBy().get(), animations.getBz().get());
                                if (animations.getRotating().get()){
                                    GL11.glRotated(i, 0, 0, 1);
                                }
                                float var151 = MathHelper.sin((float) (MathHelper.sqrt_float(f1) * Math.PI));

                                if(animations.getOld().get()){
                                    transformFirstPersonItem(f, f1);
                                    doBlockTransformations();
                                } else {
                                    switch (animations.getType().get()) {
                                        case "Push":
                                            GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
                                            GlStateManager.translate(0.0F, f1 * -0.6F, 0.0F);

                                            GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);

                                            GlStateManager.rotate(var9 * -20.0F, 0.0F, 1.0F, 0.0F);
                                            GlStateManager.rotate(var151 * -20.0F, 0.0F, 0.0F, 1.0F);

                                            GlStateManager.scale(0.4F, 0.4F, 0.4F);
                                            this.doBlockTransformations();
                                            break;
                                        case "Punch":
                                            transformFirstPersonItem(f, 0.0f);
                                            doBlockTransformations();
                                            GlStateManager.translate(0.1f, 0.2f, 0.3f);
                                            GlStateManager.rotate(-var9 * 30.0f, -5.0f, 0.0f, 9.0f);
                                            GlStateManager.rotate(-var9 * 10.0f, 1.0f, -0.4f, -0.5f);
                                            break;
                                        case "Stella":
                                            transformFirstPersonItem(-0.1f, f1);
                                            GlStateManager.translate(-0.5F, 0.4F, -0.2F);
                                            GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
                                            GlStateManager.rotate(-70.0F, 1.0F, 0.0F, 0.0F);
                                            GlStateManager.rotate(40.0F, 0.0F, 1.0F, 0.0F);
                                            break;
                                        case "1.7":
                                            this.transformFirstPersonItem(f, f1);
                                            this.doBlockTransformations();
                                            break;
                                        case "Styles":
                                            this.transformFirstPersonItem(f, 0.0F);
                                            this.doBlockTransformations();
                                            float var11 = MathHelper.sin((float) (MathHelper.sqrt_float(f1) * Math.PI));
                                            GlStateManager.translate(-0.05f, 0.2f, 0.0f);
                                            GlStateManager.rotate(-var11 * 70.0f / 2.0f, -8.0f, -0.0f, 9.0f);
                                            GlStateManager.rotate(-var11 * 70.0f, 1.0f, -0.4f, -0.0f);
                                            break;
                                        case "Swank":
                                            this.transformFirstPersonItem(f / 2.0F, f1);
                                            GlStateManager.rotate(var151 * 30.0F, -var151, -0.0F, 9.0F);
                                            GlStateManager.rotate(var151 * 40.0F, 1.0F, -var151, -0.0F);
                                            GlStateManager.translate(-0.5F, 0.4f, 0.0F);
                                            GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
                                            GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
                                            GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
                                            break;
                                        case "Swang":
                                            this.transformFirstPersonItem(f / 2.0F, f1);
                                            float var152 = MathHelper.sin((float) (MathHelper.sqrt_float(f1) * Math.PI));
                                            GlStateManager.rotate(var152 * 30.0F / 2.0F, -var152, -0.0F, 9.0F);
                                            GlStateManager.rotate(var152 * 40.0F, 1.0F, -var152 / 2.0F, -0.0F);
                                            GlStateManager.translate(-0.5F, 0.4f, 0.0F);
                                            GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
                                            GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
                                            GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
                                            break;
                                        case "Swong":
                                            this.transformFirstPersonItem(f / 2.0F, 0.0F);
                                            float var153 = MathHelper.sin((float) (f1 * f1 * Math.PI));
                                            GlStateManager.rotate(-var153 * 40.0F / 2.0F, var153 / 2.0F, -0.0F, 9.0F);
                                            GlStateManager.rotate(-var153 * 30.0F, 1.0F, var153 / 2.0F, -0.0F);
                                            GlStateManager.translate(-0.5F, 0.4f, 0.0F);
                                            GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
                                            GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
                                            GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
                                            break;
                                        case "Swaing":
                                            this.transformFirstPersonItem(f / 2.0F, -0.2F);
                                            float var154 = MathHelper.sin((float) (f1 * f1 * Math.PI));
                                            GlStateManager.rotate(-var154 / 19.0F, var154 / 20.0F, -0.0F, 9.0F);
                                            GlStateManager.rotate(-var154 * 30.0F, 10.0F, var154 / 50.0F, 0.0F);
                                            GlStateManager.translate(-0.5F, 0.4f, 0.0F);
                                            GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
                                            GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
                                            GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
                                            break;
                                        case "Swing":
                                            this.transformFirstPersonItem(f / 2.0F, f1);
                                            GlStateManager.translate(-0.5F, 0.4f, 0.0F);
                                            GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
                                            GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
                                            GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
                                            break;
                                        case "Smooth":
                                            transformFirstPersonItem(f / 1.5F, 0.0f);
                                            doBlockTransformations();
                                            GlStateManager.translate(-0.05f, 0.3f, 0.3f);
                                            GlStateManager.rotate(-var9 * 140.0f, 8.0f, 0.0f, 8.0f);
                                            GlStateManager.rotate(var9 * 90.0f, 8.0f, 0.0f, 8.0f);
                                            break;
                                        case "Sigma":
                                            transformFirstPersonItem(f * 0.5f, 0);
                                            GlStateManager.rotate(-var9 * 55 / 2.0F, -8.0F, -0.0F, 9.0F);
                                            GlStateManager.rotate(-var9 * 45, 1.0F, var9 / 2, -0.0F);
                                            doBlockTransformations();
                                            GL11.glTranslated(1.2, 0.3, 0.5);
                                            GL11.glTranslatef(-1, this.mc.thePlayer.isSneaking() ? -0.1F : -0.2F, 0.2F);
                                            break;
                                        case "Slide":
                                            this.transformFirstPersonItem(f, 0.0f);
                                            this.doBlockTransformations();
                                            GlStateManager.translate(-0.4f, 0.3f, 0.0f);
                                            GlStateManager.rotate(-var9 * 35.0f, -8.0f, -0.0f, 9.0f);
                                            GlStateManager.rotate(-var9 * 70.0f, 1.0f, -0.4f, -0.0f);
                                            GL11.glTranslatef(-0.05f, this.mc.thePlayer.isSneaking() ? -0.2f : 0.0f, 0.1f);
                                            break;
                                        case "Interia":
                                            transformFirstPersonItem(0.05f, f1);
                                            GlStateManager.translate(-0.5F, 0.5F, 0.0F);
                                            GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
                                            GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
                                            GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
                                            break;
                                        case "Ethereal":
                                            transformFirstPersonItem(f, 0.0f);
                                            doBlockTransformations();
                                            GlStateManager.translate(-0.05f, 0.2f, 0.2f);
                                            GlStateManager.rotate(-var9 * 70.0f / 2.0f, -8.0f, -0.0f, 9.0f);
                                            GlStateManager.rotate(-var9 * 70.0f, 1.0f, -0.4f, -0.0f);
                                            break;
                                        case "Old Exhibition":
                                            GL11.glTranslated(-0.04D, 0.13D, 0.0D);
                                            transformFirstPersonItem(f / 2.5F, 0.0f);
                                            GlStateManager.rotate(-var9 * 40.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                                            GlStateManager.rotate(-var9 * 30.0F, 1.0F, var9 / 3.0F, -0.0F);
                                            doBlockTransformations();
                                            break;
                                        case "Exhibition":
                                            GL11.glTranslated(-0.03, var151 * 0.062f, var151 * 0);
                                            GL11.glTranslated(0.025D, 0.09615D, 0.0D);
                                            transformFirstPersonItem(f / 3, 0.0F);
                                            GlStateManager.rotate(-var151 * 9f, -var151 / 20F, -var151 / 20F, 1);
                                            GlStateManager.rotate(-var151 * 55F, 1.2F, var151 / 4F, 0.36F);
                                            if (mc.thePlayer.isSneaking()) {
                                                GlStateManager.translate(-0.05, -0.05, 0);
                                            }
                                            this.doBlockTransformations();
                                            break;
                                        case "Spinning":
                                            GL11.glTranslated(-0.04D, 0.1, 0.0D);
                                            transformFirstPersonItem(f / 2.5F, 0.0f);
                                            GlStateManager.rotate(-90, 1, 0, 0.2f);
                                            GlStateManager.rotate(i, 0, -1, 0);
                                            break;
                                    }
                                }
                                break;
                            } else {
                                transformFirstPersonItem(f, 0.0F);
                                doBlockTransformations();
                            }
                            break;

                        case BOW:
                            this.transformFirstPersonItem(f, animations.isEnabled() && animations.getOld().get() ? f1 : 0);
                            this.doBowTransformations(partialTicks, abstractclientplayer);
                    }
                }
                else
                {
                    if (animations.isEnabled()) {
                        if (!animations.getHit().is("Smooth")) {
                            this.doItemUsedTransformations(f1);
                        }
                    } else {
                        this.doItemUsedTransformations(f1);
                    }
                    this.transformFirstPersonItem(f, f1);
                }

                if (animations.isEnabled() && animations.getOld().get() && !itemRenderer.shouldRenderItemIn3D(itemToRender)) {
                    float scale = 1.5F / 1.7F;
                    GlStateManager.scale(scale, scale, scale);
                    GlStateManager.rotate(5.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.translate(-0.29F, 0.149F, -0.0328F);
                }

                this.renderItem(abstractclientplayer, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
            }
            else if (!abstractclientplayer.isInvisible())
            {
                this.renderPlayerArm(abstractclientplayer, f, f1);
            }

            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
        }
    }

    public void renderOverlays(float partialTicks)
    {
        GlStateManager.disableAlpha();

        if (this.mc.thePlayer.isEntityInsideOpaqueBlock())
        {
            IBlockState iblockstate = this.mc.theWorld.getBlockState(new BlockPos(this.mc.thePlayer));
            BlockPos blockpos = new BlockPos(this.mc.thePlayer);
            EntityPlayer entityplayer = this.mc.thePlayer;

            for (int i = 0; i < 8; ++i)
            {
                double d0 = entityplayer.posX + (double)(((float)((i >> 0) % 2) - 0.5F) * entityplayer.width * 0.8F);
                double d1 = entityplayer.posY + (double)(((float)((i >> 1) % 2) - 0.5F) * 0.1F);
                double d2 = entityplayer.posZ + (double)(((float)((i >> 2) % 2) - 0.5F) * entityplayer.width * 0.8F);
                BlockPos blockpos1 = new BlockPos(d0, d1 + (double)entityplayer.getEyeHeight(), d2);
                IBlockState iblockstate1 = this.mc.theWorld.getBlockState(blockpos1);

                if (iblockstate1.getBlock().isVisuallyOpaque())
                {
                    iblockstate = iblockstate1;
                    blockpos = blockpos1;
                }
            }

            if (iblockstate.getBlock().getRenderType() != -1)
            {
                Object object = Reflector.getFieldValue(Reflector.RenderBlockOverlayEvent_OverlayType_BLOCK);

                if (!Reflector.callBoolean(Reflector.ForgeEventFactory_renderBlockOverlay, this.mc.thePlayer, Float.valueOf(partialTicks), object, iblockstate, blockpos))
                {
                    this.renderBlockInHand(partialTicks, this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(iblockstate));
                }
            }
        }

        if (!this.mc.thePlayer.isSpectator())
        {
            if (this.mc.thePlayer.isInsideOfMaterial(Material.water) && !Reflector.callBoolean(Reflector.ForgeEventFactory_renderWaterOverlay, this.mc.thePlayer, Float.valueOf(partialTicks)))
            {
                this.renderWaterOverlayTexture(partialTicks);
            }

            if (this.mc.thePlayer.isBurning() && !Reflector.callBoolean(Reflector.ForgeEventFactory_renderFireOverlay, this.mc.thePlayer, Float.valueOf(partialTicks)))
            {
                this.renderFireInFirstPerson(partialTicks);
            }
        }

        GlStateManager.enableAlpha();
    }

    private void renderBlockInHand(float partialTicks, TextureAtlasSprite atlas)
    {
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        float f = 0.1F;
        GlStateManager.color(0.1F, 0.1F, 0.1F, 0.5F);
        GlStateManager.pushMatrix();
        float f1 = -1.0F;
        float f2 = 1.0F;
        float f3 = -1.0F;
        float f4 = 1.0F;
        float f5 = -0.5F;
        float f6 = atlas.getMinU();
        float f7 = atlas.getMaxU();
        float f8 = atlas.getMinV();
        float f9 = atlas.getMaxV();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(-1.0D, -1.0D, -0.5D).tex(f7, f9).endVertex();
        worldrenderer.pos(1.0D, -1.0D, -0.5D).tex(f6, f9).endVertex();
        worldrenderer.pos(1.0D, 1.0D, -0.5D).tex(f6, f8).endVertex();
        worldrenderer.pos(-1.0D, 1.0D, -0.5D).tex(f7, f8).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderWaterOverlayTexture(float partialTicks)
    {
        if (!Config.isShaders() || Shaders.isUnderwaterOverlay())
        {
            this.mc.getTextureManager().bindTexture(RES_UNDERWATER_OVERLAY);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            float f = this.mc.thePlayer.getBrightness(partialTicks);
            GlStateManager.color(f, f, f, 0.5F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.pushMatrix();
            float f1 = 4.0F;
            float f2 = -1.0F;
            float f3 = 1.0F;
            float f4 = -1.0F;
            float f5 = 1.0F;
            float f6 = -0.5F;
            float f7 = -this.mc.thePlayer.rotationYaw / 64.0F;
            float f8 = this.mc.thePlayer.rotationPitch / 64.0F;
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(-1.0D, -1.0D, -0.5D).tex(4.0F + f7, 4.0F + f8).endVertex();
            worldrenderer.pos(1.0D, -1.0D, -0.5D).tex(0.0F + f7, 4.0F + f8).endVertex();
            worldrenderer.pos(1.0D, 1.0D, -0.5D).tex(0.0F + f7, 0.0F + f8).endVertex();
            worldrenderer.pos(-1.0D, 1.0D, -0.5D).tex(4.0F + f7, 0.0F + f8).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
        }
    }

    private void renderFireInFirstPerson(float partialTicks)
    {
        if(MoonLight.INSTANCE.getModuleManager().getModule(Camera.class).isEnabled() && MoonLight.INSTANCE.getModuleManager().getModule(Camera.class).setting.isEnabled("No Fire"))
            return;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);
        GlStateManager.depthFunc(519);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        float f = 1.0F;

        for (int i = 0; i < 2; ++i)
        {
            GlStateManager.pushMatrix();
            TextureAtlasSprite textureatlassprite = this.mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/fire_layer_1");
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            float f1 = textureatlassprite.getMinU();
            float f2 = textureatlassprite.getMaxU();
            float f3 = textureatlassprite.getMinV();
            float f4 = textureatlassprite.getMaxV();
            float f5 = (0.0F - f) / 2.0F;
            float f6 = f5 + f;
            float f7 = 0.0F - f / 2.0F;
            float f8 = f7 + f;
            float f9 = -0.5F;
            GlStateManager.translate((float)(-(i * 2 - 1)) * 0.24F, -0.3F, 0.0F);
            GlStateManager.rotate((float)(i * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.setSprite(textureatlassprite);
            worldrenderer.pos(f5, f7, f9).tex(f2, f4).endVertex();
            worldrenderer.pos(f6, f7, f9).tex(f1, f4).endVertex();
            worldrenderer.pos(f6, f8, f9).tex(f1, f3).endVertex();
            worldrenderer.pos(f5, f8, f9).tex(f2, f3).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
    }

    public void updateEquippedItem()
    {
        this.prevEquippedProgress = this.equippedProgress;
        EntityPlayer entityplayer = this.mc.thePlayer;
        ItemStack itemstack = SpoofSlotUtils.getSpoofedStack();
        boolean flag = false;

        if (this.itemToRender != null && itemstack != null)
        {
            if (!this.itemToRender.getIsItemStackEqual(itemstack))
            {
                if (Reflector.ForgeItem_shouldCauseReequipAnimation.exists())
                {
                    boolean flag1 = Reflector.callBoolean(this.itemToRender.getItem(), Reflector.ForgeItem_shouldCauseReequipAnimation, this.itemToRender, itemstack, Boolean.valueOf(this.equippedItemSlot != SpoofSlotUtils.getSpoofedSlot()));

                    if (!flag1)
                    {
                        this.itemToRender = itemstack;
                        this.equippedItemSlot = SpoofSlotUtils.getSpoofedSlot();
                        return;
                    }
                }

                flag = true;
            }
        }
        else flag = this.itemToRender != null || itemstack != null;

        float f2 = 0.4F;
        float f = flag ? 0.0F : 1.0F;
        float f1 = MathHelper.clamp_float(f - this.equippedProgress, -f2, f2);
        this.equippedProgress += f1;

        if (this.equippedProgress < 0.1F)
        {
            this.itemToRender = itemstack;
            this.equippedItemSlot = SpoofSlotUtils.getSpoofedSlot();

            if (Config.isShaders())
            {
                Shaders.setItemToRenderMain(itemstack);
            }
        }
    }

    public void resetEquippedProgress()
    {
        this.equippedProgress = 0.0F;
    }

    public void resetEquippedProgress2()
    {
        this.equippedProgress = 0.0F;
    }
}
