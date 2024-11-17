package net.minecraft.client.entity;

import com.mojang.authlib.GameProfile;
import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.optifine.player.CapeUtils;
import net.optifine.player.PlayerConfigurations;
import net.optifine.reflect.Reflector;
import wtf.moonlight.MoonLight;
import wtf.moonlight.events.impl.player.LookEvent;

public abstract class AbstractClientPlayer extends EntityPlayer
{
    private NetworkPlayerInfo playerInfo;
    private ResourceLocation locationOfCape = null;
    private long reloadCapeTimeMs = 0L;
    private boolean elytraOfCape = false;
    private String nameClear = null;
    private static final ResourceLocation TEXTURE_ELYTRA = new ResourceLocation("textures/entity/elytra.png");

    public AbstractClientPlayer(World worldIn, GameProfile playerProfile)
    {
        super(worldIn, playerProfile);
        this.nameClear = playerProfile.getName();

        if (this.nameClear != null && !this.nameClear.isEmpty())
        {
            this.nameClear = StringUtils.stripControlCodes(this.nameClear);
        }

        CapeUtils.downloadCape(this);
        PlayerConfigurations.getPlayerConfiguration(this);
    }

    public boolean isSpectator()
    {
        NetworkPlayerInfo networkplayerinfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(this.getGameProfile().getId());
        return networkplayerinfo != null && networkplayerinfo.getGameType() == WorldSettings.GameType.SPECTATOR;
    }

    public boolean hasPlayerInfo()
    {
        return this.getPlayerInfo() != null;
    }

    protected NetworkPlayerInfo getPlayerInfo()
    {
        if (this.playerInfo == null)
        {
            this.playerInfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(this.getUniqueID());
        }

        return this.playerInfo;
    }

    public boolean hasSkin()
    {
        NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
        return networkplayerinfo != null && networkplayerinfo.hasLocationSkin();
    }

    public ResourceLocation getLocationSkin()
    {
        NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
        return networkplayerinfo == null ? DefaultPlayerSkin.getDefaultSkin(this.getUniqueID()) : networkplayerinfo.getLocationSkin();
    }

    public ResourceLocation getLocationCape()
    {
        if (!Config.isShowCapes())
        {
            return null;
        }
        else
        {
            if (this.reloadCapeTimeMs != 0L && System.currentTimeMillis() > this.reloadCapeTimeMs)
            {
                CapeUtils.reloadCape(this);
                this.reloadCapeTimeMs = 0L;
            }

            if (this.locationOfCape != null)
            {
                return this.locationOfCape;
            }
            else
            {
                NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
                return networkplayerinfo == null ? null : networkplayerinfo.getLocationCape();
            }
        }
    }

    public static ThreadDownloadImageData getDownloadImageSkin(ResourceLocation resourceLocationIn, String username)
    {
        TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
        ITextureObject itextureobject = texturemanager.getTexture(resourceLocationIn);

        if (itextureobject == null)
        {
            itextureobject = new ThreadDownloadImageData(null, String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", StringUtils.stripControlCodes(username)), DefaultPlayerSkin.getDefaultSkin(getOfflineUUID(username)), new ImageBufferDownload());
            texturemanager.loadTexture(resourceLocationIn, itextureobject);
        }

        return (ThreadDownloadImageData)itextureobject;
    }

    public static ResourceLocation getLocationSkin(String username)
    {
        return new ResourceLocation("skins/" + StringUtils.stripControlCodes(username));
    }

    public String getSkinType()
    {
        NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
        return networkplayerinfo == null ? DefaultPlayerSkin.getSkinType(this.getUniqueID()) : networkplayerinfo.getSkinType();
    }

    public float getFovModifier()
    {
        float f = 1.0F;

        if (this.capabilities.isFlying)
        {
            f *= 1.1F;
        }

        IAttributeInstance iattributeinstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        f = (float)((double)f * ((iattributeinstance.getAttributeValue() / (double)this.capabilities.getWalkSpeed() + 1.0D) / 2.0D));

        if (this.capabilities.getWalkSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f))
        {
            f = 1.0F;
        }

        if (this.isUsingItem() && this.getItemInUse().getItem() == Items.bow)
        {
            int i = this.getItemInUseDuration();
            float f1 = (float)i / 20.0F;

            if (f1 > 1.0F)
            {
                f1 = 1.0F;
            }
            else
            {
                f1 = f1 * f1;
            }

            f *= 1.0F - f1 * 0.15F;
        }

        return Reflector.ForgeHooksClient_getOffsetFOV.exists() ? Reflector.callFloat(Reflector.ForgeHooksClient_getOffsetFOV, this, Float.valueOf(f)): f;
    }

    public String getNameClear()
    {
        return this.nameClear;
    }

    public ResourceLocation getLocationOfCape()
    {
        return this.locationOfCape;
    }

    public void setLocationOfCape(ResourceLocation p_setLocationOfCape_1_)
    {
        this.locationOfCape = p_setLocationOfCape_1_;
    }

    public boolean hasElytraCape()
    {
        ResourceLocation resourcelocation = this.getLocationCape();
        return resourcelocation != null && (resourcelocation != this.locationOfCape || this.elytraOfCape);
    }

    public void setElytraOfCape(boolean p_setElytraOfCape_1_)
    {
        this.elytraOfCape = p_setElytraOfCape_1_;
    }

    public boolean isElytraOfCape()
    {
        return this.elytraOfCape;
    }

    public long getReloadCapeTimeMs()
    {
        return this.reloadCapeTimeMs;
    }

    public void setReloadCapeTimeMs(long p_setReloadCapeTimeMs_1_)
    {
        this.reloadCapeTimeMs = p_setReloadCapeTimeMs_1_;
    }

    public Vec3 getLook(final float partialTicks) {
        float yaw = this.rotationYaw;
        float pitch = this.rotationPitch;

        LookEvent lookEvent = new LookEvent(new float[]{yaw, pitch});
        MoonLight.INSTANCE.getEventManager().call(lookEvent);
        yaw = lookEvent.rotation[0];
        pitch = lookEvent.rotation[1];

        return this.getVectorForRotation(pitch, yaw);
    }

    public Vec3 getLookCustom(float yaw,float pitch)
    {
        return this.getVectorForRotation(pitch, yaw);
    }
}
