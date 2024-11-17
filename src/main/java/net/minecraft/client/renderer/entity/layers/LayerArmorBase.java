package net.minecraft.client.renderer.entity.layers;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.CustomItems;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersRender;

public abstract class LayerArmorBase<T extends ModelBase> implements LayerRenderer<EntityLivingBase>
{
    protected static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    protected T modelLeggings;
    protected T modelArmor;
    private final RendererLivingEntity<?> renderer;
    private final float alpha = 1.0F;
    private final float colorR = 1.0F;
    private final float colorG = 1.0F;
    private final float colorB = 1.0F;
    private boolean skipRenderGlint;
    private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();

    public LayerArmorBase(RendererLivingEntity<?> rendererIn)
    {
        this.renderer = rendererIn;
        this.initArmor();
    }

    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
    {
        this.renderLayer(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale, 4);
        this.renderLayer(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale, 3);
        this.renderLayer(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale, 2);
        this.renderLayer(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale, 1);
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }

    private void renderLayer(EntityLivingBase entitylivingbaseIn, float p_177182_2_, float p_177182_3_, float partialTicks, float p_177182_5_, float p_177182_6_, float p_177182_7_, float scale, int armorSlot)
    {
        ItemStack itemstack = this.getCurrentArmor(entitylivingbaseIn, armorSlot);

        if (itemstack != null && itemstack.getItem() instanceof ItemArmor itemarmor)
        {
            T t = this.getArmorModel(armorSlot);
            t.setModelAttributes(this.renderer.getMainModel());
            t.setLivingAnimations(entitylivingbaseIn, p_177182_2_, p_177182_3_, partialTicks);

            if (Reflector.ForgeHooksClient.exists())
            {
                t = this.getArmorModelHook(entitylivingbaseIn, itemstack, armorSlot, t);
            }

            this.setModelPartVisible(t, armorSlot);
            boolean flag = this.isSlotForLeggings(armorSlot);

            if (!Config.isCustomItems() || !CustomItems.bindCustomArmorTexture(itemstack, flag ? 2 : 1, null))
            {
                if (Reflector.ForgeHooksClient_getArmorTexture.exists())
                {
                    this.renderer.bindTexture(this.getArmorResource(entitylivingbaseIn, itemstack, flag ? 2 : 1, null));
                }
                else
                {
                    this.renderer.bindTexture(this.getArmorResource(itemarmor, flag));
                }
            }

            if (Reflector.ForgeHooksClient_getArmorTexture.exists())
            {
                if (ReflectorForge.armorHasOverlay(itemarmor, itemstack))
                {
                    int j = itemarmor.getColor(itemstack);
                    float f3 = (float)(j >> 16 & 255) / 255.0F;
                    float f4 = (float)(j >> 8 & 255) / 255.0F;
                    float f5 = (float)(j & 255) / 255.0F;
                    GlStateManager.color(this.colorR * f3, this.colorG * f4, this.colorB * f5, this.alpha);
                    t.render(entitylivingbaseIn, p_177182_2_, p_177182_3_, p_177182_5_, p_177182_6_, p_177182_7_, scale);

                    if (!Config.isCustomItems() || !CustomItems.bindCustomArmorTexture(itemstack, flag ? 2 : 1, "overlay"))
                    {
                        this.renderer.bindTexture(this.getArmorResource(entitylivingbaseIn, itemstack, flag ? 2 : 1, "overlay"));
                    }
                }

                GlStateManager.color(this.colorR, this.colorG, this.colorB, this.alpha);
                t.render(entitylivingbaseIn, p_177182_2_, p_177182_3_, p_177182_5_, p_177182_6_, p_177182_7_, scale);

                if (!this.skipRenderGlint && itemstack.hasEffect() && (!Config.isCustomItems() || !CustomItems.renderCustomArmorEffect(entitylivingbaseIn, itemstack, t, p_177182_2_, p_177182_3_, partialTicks, p_177182_5_, p_177182_6_, p_177182_7_, scale)))
                {
                    this.renderGlint(entitylivingbaseIn, t, p_177182_2_, p_177182_3_, partialTicks, p_177182_5_, p_177182_6_, p_177182_7_, scale);
                }

                return;
            }

            switch (itemarmor.getArmorMaterial())
            {
                case LEATHER:
                    int i = itemarmor.getColor(itemstack);
                    float f = (float)(i >> 16 & 255) / 255.0F;
                    float f1 = (float)(i >> 8 & 255) / 255.0F;
                    float f2 = (float)(i & 255) / 255.0F;
                    GlStateManager.color(this.colorR * f, this.colorG * f1, this.colorB * f2, this.alpha);
                    t.render(entitylivingbaseIn, p_177182_2_, p_177182_3_, p_177182_5_, p_177182_6_, p_177182_7_, scale);

                    if (!Config.isCustomItems() || !CustomItems.bindCustomArmorTexture(itemstack, flag ? 2 : 1, "overlay"))
                    {
                        this.renderer.bindTexture(this.getArmorResource(itemarmor, flag, "overlay"));
                    }

                case CHAIN:
                case IRON:
                case GOLD:
                case DIAMOND:
                    GlStateManager.color(this.colorR, this.colorG, this.colorB, this.alpha);
                    t.render(entitylivingbaseIn, p_177182_2_, p_177182_3_, p_177182_5_, p_177182_6_, p_177182_7_, scale);
            }

            if (!this.skipRenderGlint && itemstack.isItemEnchanted() && (!Config.isCustomItems() || !CustomItems.renderCustomArmorEffect(entitylivingbaseIn, itemstack, t, p_177182_2_, p_177182_3_, partialTicks, p_177182_5_, p_177182_6_, p_177182_7_, scale)))
            {
                this.renderGlint(entitylivingbaseIn, t, p_177182_2_, p_177182_3_, partialTicks, p_177182_5_, p_177182_6_, p_177182_7_, scale);
            }
        }
    }

    public ItemStack getCurrentArmor(EntityLivingBase entitylivingbaseIn, int armorSlot)
    {
        return entitylivingbaseIn.getCurrentArmor(armorSlot - 1);
    }

    public T getArmorModel(int armorSlot)
    {
        return this.isSlotForLeggings(armorSlot) ? this.modelLeggings : this.modelArmor;
    }

    private boolean isSlotForLeggings(int armorSlot)
    {
        return armorSlot == 2;
    }

    private void renderGlint(EntityLivingBase entitylivingbaseIn, T modelbaseIn, float p_177183_3_, float p_177183_4_, float partialTicks, float p_177183_6_, float p_177183_7_, float p_177183_8_, float scale)
    {
        if (!Config.isShaders() || !Shaders.isShadowPass)
        {
            float f = (float)entitylivingbaseIn.ticksExisted + partialTicks;
            this.renderer.bindTexture(ENCHANTED_ITEM_GLINT_RES);

            if (Config.isShaders())
            {
                ShadersRender.renderEnchantedGlintBegin();
            }

            GlStateManager.enableBlend();
            GlStateManager.depthFunc(514);
            GlStateManager.depthMask(false);
            float f1 = 0.5F;
            GlStateManager.color(f1, f1, f1, 1.0F);

            for (int i = 0; i < 2; ++i)
            {
                GlStateManager.disableLighting();
                GlStateManager.blendFunc(768, 1);
                float f2 = 0.76F;
                GlStateManager.color(0.5F * f2, 0.25F * f2, 0.8F * f2, 1.0F);
                GlStateManager.matrixMode(5890);
                GlStateManager.loadIdentity();
                float f3 = 0.33333334F;
                GlStateManager.scale(f3, f3, f3);
                GlStateManager.rotate(30.0F - (float)i * 60.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.translate(0.0F, f * (0.001F + (float)i * 0.003F) * 20.0F, 0.0F);
                GlStateManager.matrixMode(5888);
                modelbaseIn.render(entitylivingbaseIn, p_177183_3_, p_177183_4_, p_177183_6_, p_177183_7_, p_177183_8_, scale);
            }

            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.enableLighting();
            GlStateManager.depthMask(true);
            GlStateManager.depthFunc(515);
            GlStateManager.disableBlend();

            if (Config.isShaders())
            {
                ShadersRender.renderEnchantedGlintEnd();
            }
        }
    }

    private ResourceLocation getArmorResource(ItemArmor p_177181_1_, boolean p_177181_2_)
    {
        return this.getArmorResource(p_177181_1_, p_177181_2_, null);
    }

    private ResourceLocation getArmorResource(ItemArmor p_177178_1_, boolean p_177178_2_, String p_177178_3_)
    {
        String s = String.format("textures/models/armor/%s_layer_%d%s.png", p_177178_1_.getArmorMaterial().getName(), Integer.valueOf(p_177178_2_ ? 2 : 1), p_177178_3_ == null ? "" : String.format("_%s", p_177178_3_));
        ResourceLocation resourcelocation = ARMOR_TEXTURE_RES_MAP.get(s);

        if (resourcelocation == null)
        {
            resourcelocation = new ResourceLocation(s);
            ARMOR_TEXTURE_RES_MAP.put(s, resourcelocation);
        }

        return resourcelocation;
    }

    protected abstract void initArmor();

    protected abstract void setModelPartVisible(T model, int armorSlot);

    protected T getArmorModelHook(EntityLivingBase p_getArmorModelHook_1_, ItemStack p_getArmorModelHook_2_, int p_getArmorModelHook_3_, T p_getArmorModelHook_4_)
    {
        return p_getArmorModelHook_4_;
    }

    public ResourceLocation getArmorResource(Entity p_getArmorResource_1_, ItemStack p_getArmorResource_2_, int p_getArmorResource_3_, String p_getArmorResource_4_)
    {
        ItemArmor itemarmor = (ItemArmor)p_getArmorResource_2_.getItem();
        String s = itemarmor.getArmorMaterial().getName();
        String s1 = "minecraft";
        int i = s.indexOf(58);

        if (i != -1)
        {
            s1 = s.substring(0, i);
            s = s.substring(i + 1);
        }

        String s2 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", s1, s, Integer.valueOf(this.isSlotForLeggings(p_getArmorResource_3_) ? 2 : 1), p_getArmorResource_4_ == null ? "" : String.format("_%s", p_getArmorResource_4_));
        s2 = Reflector.callString(Reflector.ForgeHooksClient_getArmorTexture, p_getArmorResource_1_, p_getArmorResource_2_, s2, Integer.valueOf(p_getArmorResource_3_), p_getArmorResource_4_);
        ResourceLocation resourcelocation = ARMOR_TEXTURE_RES_MAP.get(s2);

        if (resourcelocation == null)
        {
            resourcelocation = new ResourceLocation(s2);
            ARMOR_TEXTURE_RES_MAP.put(s2, resourcelocation);
        }

        return resourcelocation;
    }

    public void renderGlintCape(EntityLivingBase var1, ModelBase var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
        if ((!Config.isCustomItems() || CustomItems.isUseGlint()) && var2 instanceof ModelPlayer) {
            float var10 = (float)var1.ticksExisted + var5;
            this.renderer.bindTexture(ENCHANTED_ITEM_GLINT_RES);
            if (Config.isShaders()) {
                ShadersRender.renderEnchantedGlintBegin();
            }

            GlStateManager.enableBlend();
            GlStateManager.depthFunc(514);
            GlStateManager.depthMask(false);
            float var11 = 0.5F;
            GlStateManager.color(var11, var11, var11, 1.0F);

            for(int var12 = 0; var12 < 2; ++var12) {
                GlStateManager.disableLighting();
                GlStateManager.blendFunc(768, 1);
                float var13 = 0.76F;
                GlStateManager.color(0.5F * var13, 0.25F * var13, 0.8F * var13, 1.0F);
                GlStateManager.matrixMode(5890);
                GlStateManager.loadIdentity();
                float var14 = 0.33333334F;
                GlStateManager.scale(var14, var14, var14);
                GlStateManager.rotate(30.0F - (float)var12 * 60.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.translate(0.0F, var10 * (0.001F + (float)var12 * 0.003F) * 20.0F, 0.0F);
                GlStateManager.matrixMode(5888);
                ((ModelPlayer)var2).renderGlintCape(var1, var3, var4, var6, var7, var8, var9);
            }

            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.enableLighting();
            GlStateManager.depthMask(true);
            GlStateManager.depthFunc(515);
            GlStateManager.disableBlend();
            if (Config.isShaders()) {
                ShadersRender.renderEnchantedGlintEnd();
            }
        }

    }
}
