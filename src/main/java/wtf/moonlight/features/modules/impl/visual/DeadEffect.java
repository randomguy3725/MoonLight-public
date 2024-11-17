package wtf.moonlight.features.modules.impl.visual;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.events.impl.render.Render3DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.ColorValue;
import wtf.moonlight.features.values.impl.SliderValue;

import java.awt.*;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glPopAttrib;

@ModuleInfo(name = "DeadEffect", category = ModuleCategory.Visual)
public class DeadEffect extends Module {
    
    public final ColorValue color = new ColorValue("Color", new Color(0,128,255),this);
    public final SliderValue speed = new SliderValue("Speed",20,20,100,this);
    public final SliderValue maxOffset = new SliderValue("Max Offset",1,0.1f,100,0.1f,this);
    public final CopyOnWriteArrayList<Person> popList = new CopyOnWriteArrayList<>();
    private final LinkedList<Long> frames = new LinkedList<>();
    private int fps;

    @EventTarget
    public void onRender3D(Render3DEvent event){
        glPushAttrib(GL_ALL_ATTRIB_BITS);
        boolean texture = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
        boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean hz = GL11.glIsEnabled(2848);

        glDepthMask(false);
        glDisable(GL_DEPTH_TEST);

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);

        popList.forEach(person -> {
            person.update(popList);
            person.modelPlayer.bipedLeftLegwear.showModel = false;
            person.modelPlayer.bipedRightLegwear.showModel = false;
            person.modelPlayer.bipedLeftArmwear.showModel = false;
            person.modelPlayer.bipedRightArmwear.showModel = false;
            person.modelPlayer.bipedBodyWear.showModel = false;
            person.modelPlayer.bipedHead.showModel = true;
            person.modelPlayer.bipedHeadwear.showModel = false;

            GlStateManager.color(color.get().getRed() / 255f, color.get().getGreen() / 255f, color.get().getBlue() / 255f, (float) person.alpha / 255f);
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            renderEntity(person.player, person.modelPlayer, person.player.limbSwing, person.player.limbSwingAmount, person.player.ticksExisted, person.player.rotationYawHead, person.player.rotationPitch, 1);
            GlStateManager.resetColor();

        });

        if (!hz)
            GL11.glDisable(2848);
        if (texture)
            GlStateManager.enableTexture2D();
        if (!blend)
            GlStateManager.disableBlend();
        glPopAttrib();
    }
    @EventTarget
    public void onUpdate(UpdateEvent event) {
        long time = System.nanoTime();

        frames.add(time);

        while (true) {
            long f = frames.getFirst();
            final long ONE_SECOND = 1000000L * 1000L;
            if (time - f > ONE_SECOND) frames.remove();
            else break;
        }

        fps = frames.size();

        for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
            if(playerEntity.deathTime == 1){
                EntityPlayer entity = new EntityPlayer(mc.theWorld, new GameProfile(playerEntity.getUniqueID(), playerEntity.getName())) {
                    @Override
                    public boolean isSpectator() {
                        return false;
                    }
                };
                entity.copyLocationAndAnglesFrom(playerEntity);
                entity.limbSwing = playerEntity.limbSwing;
                entity.limbSwingAmount = playerEntity.limbSwingAmount;
                entity.setSneaking(playerEntity.isSneaking());
                popList.add(new Person(entity));
            }
        }
    }

    public static void renderEntity(EntityLivingBase entity, ModelBase modelBase, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (modelBase instanceof ModelPlayer modelPlayer) {
            modelPlayer.bipedBodyWear.showModel = false;
            modelPlayer.bipedLeftLegwear.showModel = false;
            modelPlayer.bipedRightLegwear.showModel = false;
            modelPlayer.bipedLeftArmwear.showModel = false;
            modelPlayer.bipedRightArmwear.showModel = false;
            modelPlayer.bipedHeadwear.showModel = true;
            modelPlayer.bipedHead.showModel = false;
        }

        float partialTicks = mc.timer.renderPartialTicks;
        double x = entity.posX - mc.getRenderManager().viewerPosX;
        double y = entity.posY - mc.getRenderManager().viewerPosY;
        double z = entity.posZ - mc.getRenderManager().viewerPosZ;

        GlStateManager.pushMatrix();

        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.rotate(180 - entity.rotationYawHead, 0, 1, 0);
        float f4 = prepareScale(entity, scale);
        float yaw = entity.rotationYawHead;

        boolean alpha = GL11.glIsEnabled(GL11.GL_ALPHA_TEST);
        GlStateManager.enableAlpha();
        modelBase.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
        modelBase.setRotationAngles(limbSwing, limbSwingAmount, 0, yaw, entity.rotationPitch, f4, entity);
        modelBase.render(entity, limbSwing, limbSwingAmount, 0, yaw, entity.rotationPitch, f4);

        if (!alpha)
            GlStateManager.disableAlpha();
        GlStateManager.popMatrix();
    }

    private static float prepareScale(EntityLivingBase entity, float scale) {
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        double widthX = entity.getEntityBoundingBox().maxX - entity.getEntityBoundingBox().minX;
        double widthZ = entity.getEntityBoundingBox().maxZ - entity.getEntityBoundingBox().minZ;

        GlStateManager.scale(scale + widthX, scale * entity.height, scale + widthZ);
        float f = 0.0625F;

        GlStateManager.translate(0.0F, -1.501F, 0.0F);
        return f;
    }

    public float getFrametime() {
        return 1.0f / fps;
    }

    public class Person {
        private final EntityPlayer player;
        private final ModelPlayer modelPlayer;
        private double alpha;

        public Person(EntityPlayer player) {
            this.player = player;
            this.modelPlayer = new ModelPlayer(0, false);
            this.alpha = 180;
        }

        public void update(CopyOnWriteArrayList<Person> arrayList) {
            if (alpha <= 0) {
                arrayList.remove(this);
                mc.theWorld.removeEntity(player);
                return;
            }
            this.alpha -= 180 / speed.get() * getFrametime();
            player.posY += maxOffset.get() / speed.get() * getFrametime();
        }
    }
}
