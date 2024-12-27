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
package wtf.moonlight.features.modules.impl.visual;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.events.impl.render.Render3DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.animations.AnimationUtils;
import wtf.moonlight.utils.player.RotationUtils;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.RenderUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@ModuleInfo(name = "DashTrail", category = ModuleCategory.Visual)
public class DashTrail extends Module {
    private final BoolValue motionsSmoothing = new BoolValue("MotionsSmoothing", false, this);
    private final BoolValue dashSegments = new BoolValue("DashSegments", false, this);
    private final BoolValue dashDots = new BoolValue("DashDots", true, this);
    private final BoolValue lighting = new BoolValue("Lighting", true, this);
    private final SliderValue dashLength = new SliderValue("DashLength", 0.75f, 0.5f, 2.0f, 0.05f, this);
    private static final String format = ".png";
    private final ResourceLocation DASH_CUBIC_BLOOM_TEX = new ResourceLocation("moonlight/texture/dashtrail/dashbloomsample.png");
    private final List<ResourceLocationWithSizes> DASH_CUBIC_TEXTURES = new ArrayList<>();
    private final List<List<ResourceLocationWithSizes>> DASH_CUBIC_ANIMATED_TEXTURES = new ArrayList<>();
    private final Random RANDOM = new Random();
    private final List<DashCubic> DASH_CUBICS = new ArrayList<>();
    private final Tessellator tessellator = Tessellator.getInstance();
    private final WorldRenderer buffer = this.tessellator.getWorldRenderer();

    private void addAll_DASH_CUBIC_TEXTURES() {
        int dashTexturesCount = 21;
        int ct = 0;
        while (ct < dashTexturesCount) {
            this.DASH_CUBIC_TEXTURES.add(new ResourceLocationWithSizes(new ResourceLocation("moonlight/texture/dashtrail/dashcubics/dashcubic" + ++ct + format)));
        }
    }

    private void addAll_DASH_CUBIC_ANIMATED_TEXTURES() {
        int[] dashGroupsNumber = new int[]{11, 23, 32, 16, 32};
        int /*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & opZywl & lucas]
 */
packageNumber = 0;
        for (Integer dashFragsNumber : dashGroupsNumber) {
            ++/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & opZywl & lucas]
 */
packageNumber;
            ArrayList<ResourceLocationWithSizes> animatedTexuresList = new ArrayList<>();
            int fragNumber = 0;
            while (fragNumber < dashFragsNumber) {
                animatedTexuresList.add(new ResourceLocationWithSizes(new ResourceLocation("moonlight/texture/dashtrail/dashcubics/group_dashs/group" + /*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & opZywl & lucas]
 */
packageNumber + "/dashcubic" + ++fragNumber + format)));
            }
            if (animatedTexuresList.isEmpty()) continue;
            this.DASH_CUBIC_ANIMATED_TEXTURES.add(animatedTexuresList);
        }
    }

    public DashTrail() {
        this.addAll_DASH_CUBIC_TEXTURES();
        this.addAll_DASH_CUBIC_ANIMATED_TEXTURES();
        this.RANDOM.setSeed(1234567891L);
    }

    private int getColorDashCubic() {
        return getModule(Interface.class).color(0);
    }

    private int[] getTextureResolution(ResourceLocation location) {
        try {
            InputStream stream = mc.getResourceManager().getResource(location).getInputStream();
            BufferedImage image = ImageIO.read(stream);
            return new int[]{image.getWidth(), image.getHeight()};
        } catch (Exception e) {
            e.printStackTrace();
            return new int[]{0, 0};
        }
    }

    private int randomTextureNumber() {
        return this.RANDOM.nextInt(this.DASH_CUBIC_TEXTURES.size());
    }

    private int randomAnimatedTexturesGroupNumber() {
        return this.RANDOM.nextInt(this.DASH_CUBIC_ANIMATED_TEXTURES.size());
    }

    private ResourceLocationWithSizes getDashCubicTextureRandom(int random) {
        return this.DASH_CUBIC_TEXTURES.get(random);
    }

    private List<ResourceLocationWithSizes> getDashCubicAnimatedTextureGroupRandom(int random) {
        return this.DASH_CUBIC_ANIMATED_TEXTURES.get(random);
    }

    private boolean hasChancedAnimatedTexutreSet() {
        return this.RANDOM.nextInt(100) > 40;
    }

    private void setDashElementsRender(Runnable render, boolean texture2d, boolean bloom) {
        GL11.glPushMatrix();
        GlStateManager.tryBlendFuncSeparate(770, bloom ? 32772 : 771, 1, 0);
        GL11.glEnable(3042);
        GL11.glLineWidth(1.0f);
        if (!texture2d) {
            GL11.glDisable(3553);
        } else {
            GL11.glEnable(3553);
        }
        GlStateManager.disableLight(0);
        GlStateManager.disableLight(1);
        GlStateManager.disableColorMaterial();
        mc.entityRenderer.disableLightmap();
        GL11.glDisable(2896);
        GL11.glShadeModel(7425);
        GL11.glDisable(3008);
        GL11.glDisable(2884);
        GL11.glDepthMask(false);
        GL11.glTexParameteri(3553, 10241, 9729);
        render.run();
        GL11.glDepthMask(true);
        GL11.glEnable(2884);
        GL11.glEnable(3008);
        GL11.glLineWidth(1.0f);
        GL11.glShadeModel(7424);
        GL11.glEnable(3553);
        GlStateManager.resetColor();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glPopMatrix();
    }

    private List<DashCubic> DASH_CUBICS_FILTERED() {
        return this.DASH_CUBICS.stream().filter(Objects::nonNull).filter(dashCubic -> dashCubic.alphaPC.getAnim() > 0.05f).toList();
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        this.DASH_CUBICS.stream().filter(dashCubic -> dashCubic.getTimePC() >= 1.0f && dashCubic.alphaPC.to != 0.0f).forEach(dashCubic -> dashCubic.alphaPC.to = 0.0f);
        this.DASH_CUBICS.removeIf(dashCubic -> dashCubic.getTimePC() >= 1.0f && dashCubic.alphaPC.to == 0.0f && (double) dashCubic.alphaPC.getAnim() < 0.02);
        List<DashCubic> filteredCubics = this.DASH_CUBICS_FILTERED();
        int next = 0;
        int max = this.motionsSmoothing.get() ? filteredCubics.size() : -1;
        for (DashCubic dashCubic2 : filteredCubics) {
            dashCubic2.motionCubicProcess(++next < max ? filteredCubics.get(next) : null);
        }
    }

    private int getRandomTimeAnimationPerTime() {
        return (int) ((float) (550 + this.RANDOM.nextInt(300)) * this.dashLength.get());
    }

    public void onEntityMove(EntityLivingBase baseIn, Vec3 prev) {
        Vec3 pos = baseIn.getPositionVector();
        double dx = pos.xCoord - prev.xCoord;
        double dy = pos.yCoord - prev.yCoord;
        double dz = pos.zCoord - prev.zCoord;
        double entitySpeed = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double entitySpeedXZ = Math.sqrt(dx * dx + dz * dz);
        if (entitySpeedXZ < (double) 0.04f) {
            return;
        }
        boolean animated = true;
        boolean[] dashDops = this.getDashPops();
        int countMax = (int) MathHelper.clamp_float((int) (entitySpeed / 0.045), 1, 16);
        for (int count = 0; count < countMax; ++count) {
            this.DASH_CUBICS.add(new DashCubic(new DashBase(baseIn, 0.04f, new DashTexture(animated), (float) count / (float) countMax, this.getRandomTimeAnimationPerTime()), dashDops[0] || dashDops[1]));
        }
    }

    boolean[] getDashPops() {
        return new boolean[]{this.dashSegments.get(), this.dashDots.get()};
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        float partialTicks = event.getPartialTicks();

        Frustum frustum = new Frustum(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        boolean[] dashDops = this.getDashPops();
        List<DashCubic> FILTERED_LEVEL2_CUBICS = this.DASH_CUBICS_FILTERED().stream().filter(dashCubic -> frustum.isBoundingBoxInFrustum(new AxisAlignedBB(dashCubic.getRenderPosX(partialTicks), dashCubic.getRenderPosY(partialTicks), dashCubic.getRenderPosZ(partialTicks)).expandXyz(0.2 * (double) dashCubic.alphaPC.getAnim()))).toList();
        if (dashDops[0] || dashDops[1]) {
            GL11.glTranslated(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY, -mc.getRenderManager().viewerPosZ);
            if (dashDops[1]) {
                this.setDashElementsRender(() -> {
                    GL11.glEnable(2832);
                    GL11.glPointSize(2.0f);
                    GL11.glBegin(0);
                    FILTERED_LEVEL2_CUBICS.forEach(dashCubic -> {
                        double[] renderDashPos = new double[]{dashCubic.getRenderPosX(partialTicks), dashCubic.getRenderPosY(partialTicks), dashCubic.getRenderPosZ(partialTicks)};
                        dashCubic.DASH_SPARKS_LIST.forEach(spark -> {
                            double[] renderSparkPos = new double[]{spark.getRenderPosX(partialTicks), spark.getRenderPosY(partialTicks), spark.getRenderPosZ(partialTicks)};
                            float aPC = (float) (spark.alphaPC() * (double) dashCubic.alphaPC.anim);
                            aPC = ((double) aPC > 0.5 ? 1.0f - aPC : aPC) * 2.0f;
                            aPC = Math.min(aPC, 1.0f);
                            int c = ColorUtils.interpolateColor(dashCubic.color, -1, aPC);
                            RenderUtils.color(ColorUtils.applyOpacity(c, (float) ColorUtils.getAlphaFromColor(c) * aPC));
                            GL11.glVertex3d(renderSparkPos[0] + renderDashPos[0], renderSparkPos[1] + renderDashPos[1], renderSparkPos[2] + renderDashPos[2]);
                            GL11.glVertex3d(-renderSparkPos[0] + renderDashPos[0], -renderSparkPos[1] + renderDashPos[1], -renderSparkPos[2] + renderDashPos[2]);
                        });
                    });
                    GL11.glEnd();
                }, false, false);
            }
            if (dashDops[0]) {
                this.setDashElementsRender(() -> FILTERED_LEVEL2_CUBICS.forEach(dashCubic -> {
                    double[] renderDashPos = new double[]{dashCubic.getRenderPosX(partialTicks), dashCubic.getRenderPosY(partialTicks), dashCubic.getRenderPosZ(partialTicks)};
                    GL11.glBegin(7);
                    dashCubic.DASH_SPARKS_LIST.forEach(spark -> {
                        double[] renderSparkPos = new double[]{spark.getRenderPosX(partialTicks), spark.getRenderPosY(partialTicks), spark.getRenderPosZ(partialTicks)};
                        float aPC = (float) spark.alphaPC() * dashCubic.alphaPC.anim * (1.0f - dashCubic.getTimePC() / 2.0f);
                        aPC = (double) aPC > 0.5 ? 1.0f - aPC : aPC;
                        aPC = Math.min(aPC, 1.0f);
                        int c = ColorUtils.interpolateColor(dashCubic.color, -1, 1.0f - aPC);
                        RenderUtils.color(ColorUtils.applyOpacity(c, (float) ColorUtils.getAlphaFromColor(c) * aPC / 2.0f));
                        GL11.glVertex3d(renderSparkPos[0] + renderDashPos[0], renderSparkPos[1] + renderDashPos[1], renderSparkPos[2] + renderDashPos[2]);
                        GL11.glVertex3d(-renderSparkPos[0] + renderDashPos[0], -renderSparkPos[1] + renderDashPos[1], -renderSparkPos[2] + renderDashPos[2]);
                    });
                    GL11.glEnd();
                }), false, true);
            }
            GL11.glTranslated(mc.getRenderManager().viewerPosX, mc.getRenderManager().viewerPosY, mc.getRenderManager().viewerPosZ);
        }
        if (!FILTERED_LEVEL2_CUBICS.isEmpty()) {
            this.setDashElementsRender(() -> {
                GL11.glTranslated(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY, -mc.getRenderManager().viewerPosZ);
                FILTERED_LEVEL2_CUBICS.forEach(dashCubic -> dashCubic.drawDash(partialTicks, false));
                this.bindResource(this.DASH_CUBIC_BLOOM_TEX);
                FILTERED_LEVEL2_CUBICS.forEach(dashCubic -> dashCubic.drawDash(partialTicks, true));
            }, true, true);
        }
    }

    private void bindResource(ResourceLocation toBind) {
        mc.getTextureManager().bindTexture(toBind);
    }

    private void drawBindedTexture(float x, float y, float x2, float y2, int c, int c2, int c3, int c4) {
        this.buffer.begin(9, DefaultVertexFormats.POSITION_TEX_COLOR);
        this.buffer.pos(x, y).tex(0.0, 0.0).color(c).endVertex();
        this.buffer.pos(x, y2).tex(0.0, 1.0).color(c2).endVertex();
        this.buffer.pos(x2, y2).tex(1.0, 1.0).color(c3).endVertex();
        this.buffer.pos(x2, y).tex(1.0, 0.0).color(c4).endVertex();
        this.tessellator.draw();
    }

    private void drawBindedTexture(float x, float y, float x2, float y2, int c) {
        this.drawBindedTexture(x, y, x2, y2, c, c, c, c);
    }

    private void set3dDashPos(double[] renderPos, Runnable renderPart, float[] rotateImageValues) {
        GL11.glPushMatrix();
        GL11.glTranslated(renderPos[0], renderPos[1], renderPos[2]);
        GL11.glRotated(-rotateImageValues[0], 0.0, 1.0, 0.0);
        GL11.glRotated(rotateImageValues[1], mc.gameSettings.thirdPersonView == 2 ? -1.0 : 1.0, 0.0, 0.0);
        GL11.glScaled(-0.1f, -0.1f, 0.1f);
        renderPart.run();
        GL11.glPopMatrix();
    }

    void addDashSparks(DashCubic cubic) {
        cubic.DASH_SPARKS_LIST.add(new DashSpark());
    }

    void dashSparksRemoveAuto(DashCubic cubic) {
        if (!cubic.DASH_SPARKS_LIST.isEmpty()) {
            if (cubic.addDops) {
                cubic.DASH_SPARKS_LIST.removeIf(DashSpark::toRemove);
            } else {
                cubic.DASH_SPARKS_LIST.clear();
            }
        }
    }

    private class ResourceLocationWithSizes {
        private final ResourceLocation source;
        private final int[] resolution;

        private ResourceLocationWithSizes(ResourceLocation source) {
            this.source = source;
            this.resolution = getTextureResolution(source);
        }

        private ResourceLocation getResource() {
            return this.source;
        }

        private int[] getResolution() {
            return this.resolution;
        }
    }

    private class DashCubic {
        private final AnimationUtils alphaPC = new AnimationUtils(0.0f, 1.0f, 0.035f);
        private final long startTime = System.currentTimeMillis();
        private final DashBase base;
        private final int color = getColorDashCubic();
        private final float[] rotate = new float[]{0.0f, 0.0f};
        List<DashSpark> DASH_SPARKS_LIST = new ArrayList<>();
        private final boolean addDops;

        private DashCubic(DashBase base, boolean addDops) {
            this.base = base;
            this.addDops = addDops;
            if (Math.sqrt(base.motionX * base.motionX + base.motionZ * base.motionZ) < 5.0E-4) {
                this.rotate[0] = (float) (360.0 * Math.random());
                this.rotate[1] = Module.mc.getRenderManager().playerViewX;
            } else {
                float motionYaw = base.getMotionYaw();
                this.rotate[0] = motionYaw - 45.0f - 15.0f - (base.entity.prevRotationYaw - base.entity.rotationYaw) * 3.0f;
                float yawDiff = RotationUtils.getAngleDifference(motionYaw + 26.3f, base.entity.rotationYaw);
                this.rotate[1] = yawDiff < 10.0f || yawDiff > 160.0f ? -90.0f : Module.mc.getRenderManager().playerViewX;
            }
        }

        private double getRenderPosX(float pTicks) {
            return this.base.prevPosX + (this.base.posX - this.base.prevPosX) * (double) pTicks;
        }

        private double getRenderPosY(float pTicks) {
            return this.base.prevPosY + (this.base.posY - this.base.prevPosY) * (double) pTicks;
        }

        private double getRenderPosZ(float pTicks) {
            return this.base.prevPosZ + (this.base.posZ - this.base.prevPosZ) * (double) pTicks;
        }

        private float getTimePC() {
            return (float) (System.currentTimeMillis() - this.startTime) / (float) this.base.rMTime;
        }

        private void motionCubicProcess(DashCubic nextCubic) {
            if (nextCubic != null && nextCubic.base.entity.getEntityId() != this.base.entity.getEntityId()) {
                nextCubic = null;
            }
            this.base.prevPosX = this.base.posX;
            this.base.prevPosY = this.base.posY;
            this.base.prevPosZ = this.base.posZ;
            this.base.motionX = (nextCubic != null ? nextCubic.base.motionX : this.base.motionX) / (double) 1.05f;
            this.base.posX = this.base.posX + 5.0 * this.base.motionX;
            this.base.motionY = (nextCubic != null ? nextCubic.base.motionY : this.base.motionY) / (double) 1.05f;
            this.base.posY = this.base.posY + 5.0 * this.base.motionY / (this.base.motionY < 0.0 ? 1.0 : 3.5);
            this.base.motionZ = (nextCubic != null ? nextCubic.base.motionZ : this.base.motionZ) / (double) 1.05f;
            this.base.posZ = this.base.posZ + 5.0 * this.base.motionZ;
            if (this.addDops) {
                if ((double) this.getTimePC() < 0.3 && RANDOM.nextInt(12) > 5) {
                    for (int i = 0; i < (getDashPops()[0] ? 1 : 3); ++i) {
                        addDashSparks(this);
                    }
                }
                this.DASH_SPARKS_LIST.forEach(DashSpark::motionSparkProcess);
            }
            dashSparksRemoveAuto(this);
        }

        private void drawDash(float partialTicks, boolean isBloomRenderer) {
            ResourceLocationWithSizes texureSized = this.base.dashTexture.getResourceWithSizes();
            if (texureSized == null) {
                return;
            }
            float aPC = this.alphaPC.getAnim();
            float alphaPC = 3f;
            float scale = 0.02f * aPC;
            float extX = (float) texureSized.getResolution()[0] * scale;
            float extY = (float) texureSized.getResolution()[1] * scale;
            double[] renderPos = new double[]{this.getRenderPosX(partialTicks), this.getRenderPosY(partialTicks), this.getRenderPosZ(partialTicks)};
            if (isBloomRenderer) {
                set3dDashPos(renderPos, () -> {
                    float extXY = (float) Math.sqrt(extX * extX + extY * extY);
                    float timePcOf = 1.0f - this.getTimePC();
                    timePcOf = timePcOf > 1.0f ? 1.0f : (Math.max(timePcOf, 0.0f));
                    drawBindedTexture(-extXY * 2.0f, -extXY * 2.0f, extXY * 2.0f, extXY * 2.0f, ColorUtils.swapAlpha(ColorUtils.getOverallColorFrom(this.color, -1, 0.15f), (lighting.get() ? 8.0f : 18.0f) * timePcOf * alphaPC + (lighting.get() ? 6.0f : 7.0f) * alphaPC));
                    if (lighting.get()) {
                        drawBindedTexture(-(extXY *= 2.0f + 2.5f * timePcOf) * 2.0f, -extXY * 2.0f, extXY * 2.0f, extXY * 2.0f, ColorUtils.swapAlpha(ColorUtils.getOverallColorFrom(this.color, -1, 0.15f), 6.0f * timePcOf * alphaPC + 3.0f * alphaPC));
                    }
                }, new float[]{Module.mc.getRenderManager().playerViewY, Module.mc.getRenderManager().playerViewX});
            } else {
                set3dDashPos(renderPos, () -> {
                    bindResource(texureSized.getResource());
                    drawBindedTexture(-extX / 2.0f, -extY / 2.0f, extX / 2.0f, extY / 2.0f, ColorUtils.darker(ColorUtils.interpolateColor(this.color, -1, 0.7f), 1.0f));
                }, this.rotate);
            }
        }
    }

    private class DashBase {
        private EntityLivingBase entity;
        private double motionX;
        private double motionY;
        private double motionZ;
        private double posX;
        private double posY;
        private double posZ;
        private double prevPosX;
        private double prevPosY;
        private double prevPosZ;
        private int rMTime;
        private DashTexture dashTexture;

        private double eMotionX() {
            return -(this.entity.prevPosX - this.entity.posX);
        }

        private double eMotionY() {
            return -(this.entity.prevPosY - this.entity.posY);
        }

        private double eMotionZ() {
            return -(this.entity.prevPosZ - this.entity.posZ);
        }

        private DashBase(EntityLivingBase entity, float speedDash, DashTexture dashTexture, float offsetTickPC, int rmTime) {
            if (entity == null) {
                return;
            }
            this.rMTime = rmTime;
            this.entity = entity;
            this.motionX = this.eMotionX();
            this.motionY = this.eMotionY();
            this.motionZ = this.eMotionZ();
            this.posX = entity.lastTickPosX - this.motionX * (double) offsetTickPC + ((double) -0.0875f + (double) 0.175f * Math.random());
            this.posY = entity.lastTickPosY - this.motionY * (double) offsetTickPC + ((double) entity.height / 1.0 / 3.0 + (double) entity.height / 1.0 / 4.0 * Math.random() * (double) 0.7f);
            this.posZ = entity.lastTickPosZ - this.motionZ * (double) offsetTickPC + ((double) -0.0875f + (double) 0.175f * Math.random());
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            this.motionX *= speedDash;
            this.motionY *= speedDash;
            this.motionZ *= speedDash;
            this.dashTexture = dashTexture;
        }

        private int getMotionYaw() {
            int motionYaw = (int) Math.toDegrees(Math.atan2(this.motionZ, this.motionX) - 90.0);
            motionYaw = motionYaw < 0 ? motionYaw + 360 : motionYaw;
            return motionYaw;
        }
    }

    private class DashTexture {
        private final List<ResourceLocationWithSizes> TEXTURES;
        private final boolean animated;
        private long timeAfterSpawn;
        private long animationPerTime;

        private boolean isAnimated() {
            return this.animated;
        }

        private DashTexture(boolean animated) {
            boolean bl = this.animated = animated && hasChancedAnimatedTexutreSet();
            if (this.animated) {
                this.timeAfterSpawn = System.currentTimeMillis();
                this.TEXTURES = getDashCubicAnimatedTextureGroupRandom(randomAnimatedTexturesGroupNumber());
                this.animationPerTime = getRandomTimeAnimationPerTime();
            } else {
                this.TEXTURES = new ArrayList<>();
                this.TEXTURES.add(getDashCubicTextureRandom(randomTextureNumber()));
            }
        }

        private ResourceLocationWithSizes getResourceWithSizes() {
            ResourceLocationWithSizes fragTexure;
            float fragCount;
            if (this.isAnimated() && (fragCount = (float) this.TEXTURES.size()) > 0.0f && (fragTexure = this.TEXTURES.get((int) MathHelper.clamp_float((float) ((int) (System.currentTimeMillis() - this.timeAfterSpawn) % (int) this.animationPerTime) / (float) this.animationPerTime * fragCount, 0.0f, fragCount))) != null) {
                return fragTexure;
            }
            return this.TEXTURES.get(0);
        }
    }

    private static class DashSpark {
        double posX;
        double posY;
        double posZ;
        double prevPosX;
        double prevPosY;
        double prevPosZ;
        double speed = Math.random() / 50.0;
        double radianYaw = Math.random() * 360.0;
        double radianPitch = -90.0 + Math.random() * 180.0;
        long startTime = System.currentTimeMillis();

        DashSpark() {
        }

        double timePC() {
            return MathHelper.clamp_float((float) (System.currentTimeMillis() - this.startTime) / 1000.0f, 0.0f, 1.0f);
        }

        double alphaPC() {
            return 1.0 - this.timePC();
        }

        boolean toRemove() {
            return this.timePC() == 1.0;
        }

        void motionSparkProcess() {
            double radYaw = Math.toRadians(this.radianYaw);
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            this.posX += Math.sin(radYaw) * this.speed;
            this.posY += Math.cos(Math.toRadians(this.radianPitch - 90.0)) * this.speed;
            this.posZ += Math.cos(radYaw) * this.speed;
        }

        double getRenderPosX(float partialTicks) {
            return this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks;
        }

        double getRenderPosY(float partialTicks) {
            return this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks;
        }

        double getRenderPosZ(float partialTicks) {
            return this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks;
        }
    }
}