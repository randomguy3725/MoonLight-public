package wtf.moonlight.features.modules.impl.visual;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.WorldEvent;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.events.impl.render.Render3DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.RenderUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ModuleInfo(name = "JumpCircles", category = ModuleCategory.Visual)
public class JumpCircles extends Module {
    public List<ArrayList> animatedGroups = Arrays.asList(new ArrayList<>(), new ArrayList<>());

    private final SliderValue maxTime = new SliderValue("Max TIme", 3000, 2000, 8000, 25F, this);
    private final SliderValue radius = new SliderValue("Radius", 2, 1, 3, 0.1F, this);
    private final ModeValue texture = new ModeValue("Texture", new String[]{"KonchalEbal", "CubicalPieces", "Leeches", "Circle"}, "Leeches", this);
    private final BoolValue deepestLight = new BoolValue("Deepest Light", true, this);
    private final String staticLoc = "moonlight/texture/newjumpcircles/default/", animatedLoc = "moonlight/texture/newjumpcircles/animated/";
    private final ResourceLocation JUMP_CIRCLE = new ResourceLocation(staticLoc + "circle.png");
    private final ResourceLocation JUMP_KONCHAL = new ResourceLocation(staticLoc + "konchal.png");
    private boolean jump = false;

    private ResourceLocation jumpTexture(int index, float progress) {
        if (texture.is("CubicalPieces") || texture.is("Leeches")) {
            ArrayList currentGroupTextures = texture.is("CubicalPieces") ? animatedGroups.get(0) : animatedGroups.get(1);
            final boolean animateByProgress = texture.is("Leeches");
            if (texture.is("Leeches")) {
                progress += .6F;
            }
            float frameOffset01 = progress % 1F;
            if (!animateByProgress) {
                final int ms = 1500;
                frameOffset01 = ((System.currentTimeMillis() + index) % ms) / (float) ms;
            }
            return (ResourceLocation) currentGroupTextures.get((int) Math.min(frameOffset01 * (currentGroupTextures.size() - .5F), currentGroupTextures.size()));
        } else {
            return texture.is("Circle") ? JUMP_CIRCLE : JUMP_KONCHAL;
        }
    }

    public void initResources() {
        ResourceLocation loc;
        final int[] groupsFramesLength = new int[]{100/*, 60*/, 200};
        final String[] groupsFramesFormat = new String[]{"jpeg", /*"png", */"png"};
        int groupIndex = groupsFramesLength.length - 1;
        if (animatedGroups.stream().allMatch(ArrayList::isEmpty)) {
            while (groupIndex >= 0) {
                int framesCounter = 0;
                while (framesCounter < groupsFramesLength[groupIndex]) {
                    framesCounter++;
                    loc = new ResourceLocation(animatedLoc + ("animation" + (groupIndex + 1)) + ("/circleframe_" + framesCounter) + ("." + groupsFramesFormat[groupIndex]));
                    animatedGroups.get(groupIndex).add(loc);
                }
                --groupIndex;
            }
        }
    }

    public JumpCircles() {
        initResources();
    }

    private void addCircleForEntity(final Entity entity) {
        Vec3 vec = getVec3dFromEntity(entity).add(0.D, .005D, 0.D);
        BlockPos pos = new BlockPos(vec);
        IBlockState state = mc.theWorld.getBlockState(pos);
        if (state.getBlock() == Blocks.snow) {
            vec = vec.add(0.D, .125D, 0.D);
        }
        circles.add(new JumpRenderer(vec, circles.size()));
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (!mc.thePlayer.onGround) {
            jump = true;
        }
        if (mc.thePlayer.onGround && jump) {
            addCircleForEntity(mc.thePlayer);
            jump = false;
        }
    }

    @EventTarget
    private void onRender3D(Render3DEvent event) {
        if (circles.isEmpty()) return;
        circles.removeIf((final JumpRenderer circle) -> circle.getDeltaTime() >= 1.D);
        if (circles.isEmpty()) return;
        float deepestLightAnim = deepestLight.get() ? 1 : 0, immersiveStrengh = 0;
        if (deepestLightAnim >= 1.F / 255.F) {
            switch (texture.get()) {
                case "Circle", "Emission" -> immersiveStrengh = .1F;
                case "KonchalEbal", "CubicalPieces", "Inusual" -> immersiveStrengh = .075F;
                case "Leeches" -> immersiveStrengh = .2F;
            }
        }
        float finalImmersiveStrengh = immersiveStrengh;
        setupDraw(() -> circles.forEach(circle -> doCircle(circle.pos, radius.get(), 1.F - circle.getDeltaTime(), circle.getIndex() * 30, deepestLightAnim, finalImmersiveStrengh)));
    }

    private void doCircle(final Vec3 pos, double maxRadius, float deltaTime, int index, float immersiveShift, float immersiveIntense) {
        boolean immersive = immersiveShift >= 1.F / 255.F;
        float waveDelta = valWave01(1.F - deltaTime);
        float alphaPC = (float) easeOutCirc(valWave01(1 - deltaTime));
        if (deltaTime < .5F) alphaPC *= (float) easeInOutExpo(alphaPC);
        float radius = (float) ((deltaTime > .5F ? easeOutElastic(waveDelta * waveDelta) : easeOutBounce(waveDelta)) * maxRadius);
        double rotate = easeInOutElastic(waveDelta) * 90.D / (1.D + waveDelta);
        ResourceLocation res = jumpTexture(index, deltaTime);
        mc.getTextureManager().bindTexture(res);
        mc.getTextureManager().getTexture(res).setBlurMipmap(true, true);
        GlStateManager.pushMatrix();
        GlStateManager.translate(pos.xCoord - radius / 2.D, pos.yCoord, pos.zCoord - radius / 2.D);
        GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        RenderUtils.customRotatedObject2D(0, 0, radius, radius, rotate);
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldRenderer.pos(0, 0, 0).tex(0, 0).color(getColor(alphaPC)).endVertex();
        worldRenderer.pos(0, radius, 0).tex(0, 1).color(getColor(alphaPC)).endVertex();
        worldRenderer.pos(radius, radius, 0).tex(1, 1).color(getColor(alphaPC)).endVertex();
        worldRenderer.pos(radius, 0, 0).tex(1, 0).color(getColor(alphaPC)).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
        if (immersive) {
            int[] colors = new int[4];
            colors[0] = getColor(alphaPC);
            colors[1] = getColor(alphaPC);
            colors[2] = getColor(alphaPC);
            colors[3] = getColor(alphaPC);
            GlStateManager.pushMatrix();
            GlStateManager.translate(pos.xCoord, pos.yCoord, pos.zCoord);
            GL11.glRotated(rotate, 0.0f, 1.0f, 0.0f);
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            float polygons = 40, extMaxY = radius / 3.5F, extMaxXZ = radius / 7.F, minAPC = immersiveIntense * immersiveShift;
            float aPC;
            for (int i = 1; i < (int) polygons; i++) {
                float iPC = i / polygons, extY = extMaxY * i / polygons - extMaxY / polygons;
                if ((aPC = MathUtils.lerp(alphaPC * minAPC, 0, iPC)) * 255 < 1) continue;
                float radiusPost = radius + (float) easeOutCirc(valWave01(iPC - 1.5F / polygons)) * extMaxXZ;
                worldRenderer.pos(-radiusPost / 2.F, extY, -radiusPost / 2.F).tex(0, 0).color(ColorUtils.darker(colors[0], aPC)).endVertex();
                worldRenderer.pos(-radiusPost / 2.F, extY, radiusPost / 2.F).tex(0, 1).color(ColorUtils.darker(colors[1], aPC)).endVertex();
                worldRenderer.pos(radiusPost / 2.F, extY, radiusPost / 2.F).tex(1, 1).color(ColorUtils.darker(colors[2], aPC)).endVertex();
                worldRenderer.pos(radiusPost / 2.F, extY, -radiusPost / 2.F).tex(1, 0).color(ColorUtils.darker(colors[3], aPC)).endVertex();
            }
            tessellator.draw();
            GlStateManager.popMatrix();
        }
    }

    @EventTarget
    private void onWorld(WorldEvent event) {
        reset();
    }

    @Override
    public void onEnable() {
        reset();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        reset();
        super.onDisable();
    }

    private void reset() {
        if (!circles.isEmpty()) circles.clear();
    }


    private final static List<JumpRenderer> circles = new java.util.ArrayList<>();

    private static Vec3 getVec3dFromEntity(final Entity entityIn) {
        final float PT = mc.timer.renderPartialTicks;
        final double dx = entityIn.posX - entityIn.lastTickPosX, dy = entityIn.posY - entityIn.lastTickPosY, dz = entityIn.posZ - entityIn.lastTickPosZ;
        return new Vec3((entityIn.lastTickPosX + dx * PT + dx * 2.D), (entityIn.lastTickPosY + dy * PT), (entityIn.lastTickPosZ + dz * PT + dz * 2.D));
    }

    private void setupDraw(final Runnable render) {
        final boolean light = GL11.glIsEnabled(GL11.GL_LIGHTING);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0);
        GlStateManager.depthMask(false);
        GlStateManager.disableCull();
        if (light) GlStateManager.disableLighting();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        //GL_ONE_MINUS_CONSTANT_ALPHA
        GlStateManager.blendFunc(770, 1);
        RenderUtils.setupOrientationMatrix(0, 0, 0);
        render.run();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.color(1.F, 1.F, 1.F);
        GlStateManager.shadeModel(GL11.GL_FLAT);
        if (light) GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.alphaFunc(GL11.GL_GREATER, .1F);
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
    }

    private int getColor(float alphaPC) {
        int colorize = getModule(Interface.class).color();
        return ColorUtils.getOverallColorFrom(colorize, new Color(255, 255, 255, (int) (255.F * alphaPC)).getRGB(), .1F);
    }

    private final Tessellator tessellator = Tessellator.getInstance();
    private final WorldRenderer worldRenderer = tessellator.getWorldRenderer();

    private final class JumpRenderer {
        private final long time = System.currentTimeMillis();
        private final Vec3 pos;
        int index;

        private JumpRenderer(Vec3 pos, int index) {
            this.pos = pos;
            this.index = index;
        }

        private float getDeltaTime() {
            return (System.currentTimeMillis() - time) / maxTime.get();
        }

        private int getIndex() {
            return this.index;
        }
    }

    public static double easeOutBounce(double value) {
        double n1 = 7.5625, d1 = 2.75;
        if (value < 1 / d1) {
            return n1 * value * value;
        } else if (value < 2 / d1) {
            return n1 * (value -= 1.5 / d1) * value + 0.75;
        } else if (value < 2.5 / d1) {
            return n1 * (value -= 2.25 / d1) * value + 0.9375;
        } else {
            return n1 * (value -= 2.625 / d1) * value + 0.984375;
        }
    }

    public static double easeInOutElastic(double value) {
        double c5 = (2 * Math.PI) / 4.5;
        return value < 0 ? 0 : value > 1 ? 1 : value < 0.5 ? -(Math.pow(2, 20 * value - 10) * Math.sin((20 * value - 11.125) * c5)) / 2 : (Math.pow(2, -20 * value + 10) * Math.sin((20 * value - 11.125) * c5)) / 2 + 1;
    }

    public static double easeOutElastic(double value) {
        double c4 = (2 * Math.PI) / 3;
        return value < 0 ? 0 : value > 1 ? 1 : Math.pow(2, -10 * value) * Math.sin((value * 10 - 0.75) * c4) + 1;
    }

    public static double easeOutBack(double value) {
        double c1 = 1.70158, c3 = c1 + 1;
        return 1 + c3 * Math.pow(value - 1, 3) + c1 * Math.pow(value - 1, 2);
    }

    public static double easeOutCubic(double value) {
        return 1 - Math.pow(1 - value, 3);
    }

    public static float valWave01(float value) {
        return (value > .5 ? 1 - value : value) * 2.F;
    }

    public static double easeInOutQuad(double value) {
        return value < .5D ? 2.D * value * value : 1.D - Math.pow(-2.D * value + 2.D, 2.D) / 2.D;
    }

    public static double easeInOutQuadWave(double value) {
        value = (value > .5D ? 1.D - value : value) * 2.D;
        value = value < .5D ? 2.D * value * value : 1.D - Math.pow(-2.D * value + 2.D, 2.D) / 2.D;
        value = value > 1.D ? 1.D : value < 0.D ? 0.D : value;
        return value;
    }

    public static double easeInCircle(double value) {
        return 1 - Math.sqrt(1 - Math.pow(value, 2));
    }

    public static double easeOutCirc(double value) {
        return Math.sqrt(1 - Math.pow(value - 1, 2));
    }

    public static double easeInOutExpo(double value) {
        return value < 0 ? 0 : value > 1 ? 1 : value < 0.5 ? Math.pow(2, 20 * value - 10) / 2 : (2 - Math.pow(2, -20 * value + 10)) / 2;
    }

    public static float lerp(float value, float to, float pc) {
        return value + pc * (to - value);
    }
}