package net.optifine.shaders;

import com.google.common.base.Charsets;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.optifine.CustomBlockLayers;
import net.optifine.CustomColors;
import net.optifine.GlErrors;
import net.optifine.Lang;
import net.optifine.config.ConnectedParser;
import net.optifine.expr.IExpressionBool;
import net.optifine.reflect.Reflector;
import net.optifine.render.GlAlphaState;
import net.optifine.render.GlBlendState;
import net.optifine.shaders.config.EnumShaderOption;
import net.optifine.shaders.config.MacroProcessor;
import net.optifine.shaders.config.MacroState;
import net.optifine.shaders.config.PropertyDefaultFastFancyOff;
import net.optifine.shaders.config.PropertyDefaultTrueFalse;
import net.optifine.shaders.config.RenderScale;
import net.optifine.shaders.config.ScreenShaderOptions;
import net.optifine.shaders.config.ShaderLine;
import net.optifine.shaders.config.ShaderOption;
import net.optifine.shaders.config.ShaderOptionProfile;
import net.optifine.shaders.config.ShaderOptionRest;
import net.optifine.shaders.config.ShaderPackParser;
import net.optifine.shaders.config.ShaderParser;
import net.optifine.shaders.config.ShaderProfile;
import net.optifine.shaders.uniform.CustomUniforms;
import net.optifine.shaders.uniform.ShaderUniform1f;
import net.optifine.shaders.uniform.ShaderUniform1i;
import net.optifine.shaders.uniform.ShaderUniform2i;
import net.optifine.shaders.uniform.ShaderUniform3f;
import net.optifine.shaders.uniform.ShaderUniform4f;
import net.optifine.shaders.uniform.ShaderUniform4i;
import net.optifine.shaders.uniform.ShaderUniformM4;
import net.optifine.shaders.uniform.ShaderUniforms;
import net.optifine.shaders.uniform.Smoother;
import net.optifine.texture.InternalFormat;
import net.optifine.texture.PixelFormat;
import net.optifine.texture.PixelType;
import net.optifine.texture.TextureType;
import net.optifine.util.EntityUtils;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.StrUtils;
import net.optifine.util.TimedEvent;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjglx.opengl.GLContext;
import org.lwjglx.util.glu.GLU;
import org.lwjglx.util.vector.Vector4f;

public class Shaders
{
    static Minecraft mc = Minecraft.getMinecraft();
    static EntityRenderer entityRenderer;
    public static boolean isInitializedOnce = false;
    public static boolean isShaderPackInitialized = false;
    public static GLCapabilities capabilities;
    public static String glVersionString;
    public static String glVendorString;
    public static String glRendererString;
    public static boolean hasGlGenMipmap = false;
    public static int countResetDisplayLists = 0;
    private static int renderDisplayWidth = 0;
    private static int renderDisplayHeight = 0;
    public static int renderWidth = 0;
    public static int renderHeight = 0;
    public static boolean isRenderingWorld = false;
    public static boolean isRenderingSky = false;
    public static boolean isCompositeRendered = false;
    public static boolean isRenderingDfb = false;
    public static boolean isShadowPass = false;
    public static boolean isEntitiesGlowing = false;
    public static boolean isSleeping;
    private static boolean isRenderingFirstPersonHand;
    private static boolean isHandRenderedMain;
    private static boolean isHandRenderedOff;
    private static boolean skipRenderHandMain;
    private static boolean skipRenderHandOff;
    public static boolean renderItemKeepDepthMask = false;
    public static boolean itemToRenderMainTranslucent = false;
    public static boolean itemToRenderOffTranslucent = false;
    static float[] sunPosition = new float[4];
    static float[] moonPosition = new float[4];
    static float[] shadowLightPosition = new float[4];
    static float[] upPosition = new float[4];
    static float[] shadowLightPositionVector = new float[4];
    static float[] upPosModelView = new float[] {0.0F, 100.0F, 0.0F, 0.0F};
    static float[] sunPosModelView = new float[] {0.0F, 100.0F, 0.0F, 0.0F};
    static float[] moonPosModelView = new float[] {0.0F, -100.0F, 0.0F, 0.0F};
    private static final float[] tempMat = new float[16];
    static float clearColorR;
    static float clearColorG;
    static float clearColorB;
    static float skyColorR;
    static float skyColorG;
    static float skyColorB;
    static long worldTime = 0L;
    static long lastWorldTime = 0L;
    static long diffWorldTime = 0L;
    static float celestialAngle = 0.0F;
    static float sunAngle = 0.0F;
    static float shadowAngle = 0.0F;
    static int moonPhase = 0;
    static long systemTime = 0L;
    static long lastSystemTime = 0L;
    static long diffSystemTime = 0L;
    static int frameCounter = 0;
    static float frameTime = 0.0F;
    static float frameTimeCounter = 0.0F;
    static int systemTimeInt32 = 0;
    static float rainStrength = 0.0F;
    static float wetness = 0.0F;
    public static float wetnessHalfLife = 600.0F;
    public static float drynessHalfLife = 200.0F;
    public static float eyeBrightnessHalflife = 10.0F;
    static boolean usewetness = false;
    static int isEyeInWater = 0;
    static int eyeBrightness = 0;
    static float eyeBrightnessFadeX = 0.0F;
    static float eyeBrightnessFadeY = 0.0F;
    static float eyePosY = 0.0F;
    static float centerDepth = 0.0F;
    static float centerDepthSmooth = 0.0F;
    static float centerDepthSmoothHalflife = 1.0F;
    static boolean centerDepthSmoothEnabled = false;
    static int superSamplingLevel = 1;
    static float nightVision = 0.0F;
    static float blindness = 0.0F;
    static boolean lightmapEnabled = false;
    static boolean fogEnabled = true;
    public static int entityAttrib = 10;
    public static int midTexCoordAttrib = 11;
    public static int tangentAttrib = 12;
    public static boolean useEntityAttrib = false;
    public static boolean useMidTexCoordAttrib = false;
    public static boolean useTangentAttrib = false;
    public static boolean progUseEntityAttrib = false;
    public static boolean progUseMidTexCoordAttrib = false;
    public static boolean progUseTangentAttrib = false;
    private static boolean progArbGeometryShader4 = false;
    private static int progMaxVerticesOut = 3;
    private static boolean hasGeometryShaders = false;
    public static int atlasSizeX = 0;
    public static int atlasSizeY = 0;
    private static final ShaderUniforms shaderUniforms = new ShaderUniforms();
    public static ShaderUniform4f uniform_entityColor = shaderUniforms.make4f("entityColor");
    public static ShaderUniform1i uniform_entityId = shaderUniforms.make1i("entityId");
    public static ShaderUniform1i uniform_blockEntityId = shaderUniforms.make1i("blockEntityId");
    public static ShaderUniform1i uniform_texture = shaderUniforms.make1i("texture");
    public static ShaderUniform1i uniform_lightmap = shaderUniforms.make1i("lightmap");
    public static ShaderUniform1i uniform_normals = shaderUniforms.make1i("normals");
    public static ShaderUniform1i uniform_specular = shaderUniforms.make1i("specular");
    public static ShaderUniform1i uniform_shadow = shaderUniforms.make1i("shadow");
    public static ShaderUniform1i uniform_watershadow = shaderUniforms.make1i("watershadow");
    public static ShaderUniform1i uniform_shadowtex0 = shaderUniforms.make1i("shadowtex0");
    public static ShaderUniform1i uniform_shadowtex1 = shaderUniforms.make1i("shadowtex1");
    public static ShaderUniform1i uniform_depthtex0 = shaderUniforms.make1i("depthtex0");
    public static ShaderUniform1i uniform_depthtex1 = shaderUniforms.make1i("depthtex1");
    public static ShaderUniform1i uniform_shadowcolor = shaderUniforms.make1i("shadowcolor");
    public static ShaderUniform1i uniform_shadowcolor0 = shaderUniforms.make1i("shadowcolor0");
    public static ShaderUniform1i uniform_shadowcolor1 = shaderUniforms.make1i("shadowcolor1");
    public static ShaderUniform1i uniform_noisetex = shaderUniforms.make1i("noisetex");
    public static ShaderUniform1i uniform_gcolor = shaderUniforms.make1i("gcolor");
    public static ShaderUniform1i uniform_gdepth = shaderUniforms.make1i("gdepth");
    public static ShaderUniform1i uniform_gnormal = shaderUniforms.make1i("gnormal");
    public static ShaderUniform1i uniform_composite = shaderUniforms.make1i("composite");
    public static ShaderUniform1i uniform_gaux1 = shaderUniforms.make1i("gaux1");
    public static ShaderUniform1i uniform_gaux2 = shaderUniforms.make1i("gaux2");
    public static ShaderUniform1i uniform_gaux3 = shaderUniforms.make1i("gaux3");
    public static ShaderUniform1i uniform_gaux4 = shaderUniforms.make1i("gaux4");
    public static ShaderUniform1i uniform_colortex0 = shaderUniforms.make1i("colortex0");
    public static ShaderUniform1i uniform_colortex1 = shaderUniforms.make1i("colortex1");
    public static ShaderUniform1i uniform_colortex2 = shaderUniforms.make1i("colortex2");
    public static ShaderUniform1i uniform_colortex3 = shaderUniforms.make1i("colortex3");
    public static ShaderUniform1i uniform_colortex4 = shaderUniforms.make1i("colortex4");
    public static ShaderUniform1i uniform_colortex5 = shaderUniforms.make1i("colortex5");
    public static ShaderUniform1i uniform_colortex6 = shaderUniforms.make1i("colortex6");
    public static ShaderUniform1i uniform_colortex7 = shaderUniforms.make1i("colortex7");
    public static ShaderUniform1i uniform_gdepthtex = shaderUniforms.make1i("gdepthtex");
    public static ShaderUniform1i uniform_depthtex2 = shaderUniforms.make1i("depthtex2");
    public static ShaderUniform1i uniform_tex = shaderUniforms.make1i("tex");
    public static ShaderUniform1i uniform_heldItemId = shaderUniforms.make1i("heldItemId");
    public static ShaderUniform1i uniform_heldBlockLightValue = shaderUniforms.make1i("heldBlockLightValue");
    public static ShaderUniform1i uniform_heldItemId2 = shaderUniforms.make1i("heldItemId2");
    public static ShaderUniform1i uniform_heldBlockLightValue2 = shaderUniforms.make1i("heldBlockLightValue2");
    public static ShaderUniform1i uniform_fogMode = shaderUniforms.make1i("fogMode");
    public static ShaderUniform1f uniform_fogDensity = shaderUniforms.make1f("fogDensity");
    public static ShaderUniform3f uniform_fogColor = shaderUniforms.make3f("fogColor");
    public static ShaderUniform3f uniform_skyColor = shaderUniforms.make3f("skyColor");
    public static ShaderUniform1i uniform_worldTime = shaderUniforms.make1i("worldTime");
    public static ShaderUniform1i uniform_worldDay = shaderUniforms.make1i("worldDay");
    public static ShaderUniform1i uniform_moonPhase = shaderUniforms.make1i("moonPhase");
    public static ShaderUniform1i uniform_frameCounter = shaderUniforms.make1i("frameCounter");
    public static ShaderUniform1f uniform_frameTime = shaderUniforms.make1f("frameTime");
    public static ShaderUniform1f uniform_frameTimeCounter = shaderUniforms.make1f("frameTimeCounter");
    public static ShaderUniform1f uniform_sunAngle = shaderUniforms.make1f("sunAngle");
    public static ShaderUniform1f uniform_shadowAngle = shaderUniforms.make1f("shadowAngle");
    public static ShaderUniform1f uniform_rainStrength = shaderUniforms.make1f("rainStrength");
    public static ShaderUniform1f uniform_aspectRatio = shaderUniforms.make1f("aspectRatio");
    public static ShaderUniform1f uniform_viewWidth = shaderUniforms.make1f("viewWidth");
    public static ShaderUniform1f uniform_viewHeight = shaderUniforms.make1f("viewHeight");
    public static ShaderUniform1f uniform_near = shaderUniforms.make1f("near");
    public static ShaderUniform1f uniform_far = shaderUniforms.make1f("far");
    public static ShaderUniform3f uniform_sunPosition = shaderUniforms.make3f("sunPosition");
    public static ShaderUniform3f uniform_moonPosition = shaderUniforms.make3f("moonPosition");
    public static ShaderUniform3f uniform_shadowLightPosition = shaderUniforms.make3f("shadowLightPosition");
    public static ShaderUniform3f uniform_upPosition = shaderUniforms.make3f("upPosition");
    public static ShaderUniform3f uniform_previousCameraPosition = shaderUniforms.make3f("previousCameraPosition");
    public static ShaderUniform3f uniform_cameraPosition = shaderUniforms.make3f("cameraPosition");
    public static ShaderUniformM4 uniform_gbufferModelView = shaderUniforms.makeM4("gbufferModelView");
    public static ShaderUniformM4 uniform_gbufferModelViewInverse = shaderUniforms.makeM4("gbufferModelViewInverse");
    public static ShaderUniformM4 uniform_gbufferPreviousProjection = shaderUniforms.makeM4("gbufferPreviousProjection");
    public static ShaderUniformM4 uniform_gbufferProjection = shaderUniforms.makeM4("gbufferProjection");
    public static ShaderUniformM4 uniform_gbufferProjectionInverse = shaderUniforms.makeM4("gbufferProjectionInverse");
    public static ShaderUniformM4 uniform_gbufferPreviousModelView = shaderUniforms.makeM4("gbufferPreviousModelView");
    public static ShaderUniformM4 uniform_shadowProjection = shaderUniforms.makeM4("shadowProjection");
    public static ShaderUniformM4 uniform_shadowProjectionInverse = shaderUniforms.makeM4("shadowProjectionInverse");
    public static ShaderUniformM4 uniform_shadowModelView = shaderUniforms.makeM4("shadowModelView");
    public static ShaderUniformM4 uniform_shadowModelViewInverse = shaderUniforms.makeM4("shadowModelViewInverse");
    public static ShaderUniform1f uniform_wetness = shaderUniforms.make1f("wetness");
    public static ShaderUniform1f uniform_eyeAltitude = shaderUniforms.make1f("eyeAltitude");
    public static ShaderUniform2i uniform_eyeBrightness = shaderUniforms.make2i("eyeBrightness");
    public static ShaderUniform2i uniform_eyeBrightnessSmooth = shaderUniforms.make2i("eyeBrightnessSmooth");
    public static ShaderUniform2i uniform_terrainTextureSize = shaderUniforms.make2i("terrainTextureSize");
    public static ShaderUniform1i uniform_terrainIconSize = shaderUniforms.make1i("terrainIconSize");
    public static ShaderUniform1i uniform_isEyeInWater = shaderUniforms.make1i("isEyeInWater");
    public static ShaderUniform1f uniform_nightVision = shaderUniforms.make1f("nightVision");
    public static ShaderUniform1f uniform_blindness = shaderUniforms.make1f("blindness");
    public static ShaderUniform1f uniform_screenBrightness = shaderUniforms.make1f("screenBrightness");
    public static ShaderUniform1i uniform_hideGUI = shaderUniforms.make1i("hideGUI");
    public static ShaderUniform1f uniform_centerDepthSmooth = shaderUniforms.make1f("centerDepthSmooth");
    public static ShaderUniform2i uniform_atlasSize = shaderUniforms.make2i("atlasSize");
    public static ShaderUniform4i uniform_blendFunc = shaderUniforms.make4i("blendFunc");
    public static ShaderUniform1i uniform_instanceId = shaderUniforms.make1i("instanceId");
    static double previousCameraPositionX;
    static double previousCameraPositionY;
    static double previousCameraPositionZ;
    static double cameraPositionX;
    static double cameraPositionY;
    static double cameraPositionZ;
    static int cameraOffsetX;
    static int cameraOffsetZ;
    static int shadowPassInterval = 0;
    public static boolean needResizeShadow = false;
    static int shadowMapWidth = 1024;
    static int shadowMapHeight = 1024;
    static int spShadowMapWidth = 1024;
    static int spShadowMapHeight = 1024;
    static float shadowMapFOV = 90.0F;
    static float shadowMapHalfPlane = 160.0F;
    static boolean shadowMapIsOrtho = true;
    static float shadowDistanceRenderMul = -1.0F;
    static int shadowPassCounter = 0;
    static int preShadowPassThirdPersonView;
    public static boolean shouldSkipDefaultShadow = false;
    static boolean waterShadowEnabled = false;
    static final int MaxDrawBuffers = 8;
    static final int MaxColorBuffers = 8;
    static final int MaxDepthBuffers = 3;
    static final int MaxShadowColorBuffers = 8;
    static final int MaxShadowDepthBuffers = 2;
    static int usedColorBuffers = 0;
    static int usedDepthBuffers = 0;
    static int usedShadowColorBuffers = 0;
    static int usedShadowDepthBuffers = 0;
    static int usedColorAttachs = 0;
    static int usedDrawBuffers = 0;
    static int dfb = 0;
    static int sfb = 0;
    private static final int[] gbuffersFormat = new int[8];
    public static boolean[] gbuffersClear = new boolean[8];
    public static Vector4f[] gbuffersClearColor = new Vector4f[8];
    private static final Programs programs = new Programs();
    public static final Program ProgramNone = programs.getProgramNone();
    public static final Program ProgramShadow = programs.makeShadow("shadow", ProgramNone);
    public static final Program ProgramShadowSolid = programs.makeShadow("shadow_solid", ProgramShadow);
    public static final Program ProgramShadowCutout = programs.makeShadow("shadow_cutout", ProgramShadow);
    public static final Program ProgramBasic = programs.makeGbuffers("gbuffers_basic", ProgramNone);
    public static final Program ProgramTextured = programs.makeGbuffers("gbuffers_textured", ProgramBasic);
    public static final Program ProgramTexturedLit = programs.makeGbuffers("gbuffers_textured_lit", ProgramTextured);
    public static final Program ProgramSkyBasic = programs.makeGbuffers("gbuffers_skybasic", ProgramBasic);
    public static final Program ProgramSkyTextured = programs.makeGbuffers("gbuffers_skytextured", ProgramTextured);
    public static final Program ProgramClouds = programs.makeGbuffers("gbuffers_clouds", ProgramTextured);
    public static final Program ProgramTerrain = programs.makeGbuffers("gbuffers_terrain", ProgramTexturedLit);
    public static final Program ProgramTerrainSolid = programs.makeGbuffers("gbuffers_terrain_solid", ProgramTerrain);
    public static final Program ProgramTerrainCutoutMip = programs.makeGbuffers("gbuffers_terrain_cutout_mip", ProgramTerrain);
    public static final Program ProgramTerrainCutout = programs.makeGbuffers("gbuffers_terrain_cutout", ProgramTerrain);
    public static final Program ProgramDamagedBlock = programs.makeGbuffers("gbuffers_damagedblock", ProgramTerrain);
    public static final Program ProgramBlock = programs.makeGbuffers("gbuffers_block", ProgramTerrain);
    public static final Program ProgramBeaconBeam = programs.makeGbuffers("gbuffers_beaconbeam", ProgramTextured);
    public static final Program ProgramItem = programs.makeGbuffers("gbuffers_item", ProgramTexturedLit);
    public static final Program ProgramEntities = programs.makeGbuffers("gbuffers_entities", ProgramTexturedLit);
    public static final Program ProgramEntitiesGlowing = programs.makeGbuffers("gbuffers_entities_glowing", ProgramEntities);
    public static final Program ProgramArmorGlint = programs.makeGbuffers("gbuffers_armor_glint", ProgramTextured);
    public static final Program ProgramSpiderEyes = programs.makeGbuffers("gbuffers_spidereyes", ProgramTextured);
    public static final Program ProgramHand = programs.makeGbuffers("gbuffers_hand", ProgramTexturedLit);
    public static final Program ProgramWeather = programs.makeGbuffers("gbuffers_weather", ProgramTexturedLit);
    public static final Program ProgramDeferredPre = programs.makeVirtual("deferred_pre");
    public static final Program[] ProgramsDeferred = programs.makeDeferreds("deferred", 16);
    public static final Program ProgramDeferred = ProgramsDeferred[0];
    public static final Program ProgramWater = programs.makeGbuffers("gbuffers_water", ProgramTerrain);
    public static final Program ProgramHandWater = programs.makeGbuffers("gbuffers_hand_water", ProgramHand);
    public static final Program ProgramCompositePre = programs.makeVirtual("composite_pre");
    public static final Program[] ProgramsComposite = programs.makeComposites("composite", 16);
    public static final Program ProgramComposite = ProgramsComposite[0];
    public static final Program ProgramFinal = programs.makeComposite("final");
    public static final int ProgramCount = programs.getCount();
    public static final Program[] ProgramsAll = programs.getPrograms();
    public static Program activeProgram = ProgramNone;
    public static int activeProgramID = 0;
    private static final ProgramStack programStack = new ProgramStack();
    private static boolean hasDeferredPrograms = false;
    static IntBuffer activeDrawBuffers = null;
    private static int activeCompositeMipmapSetting = 0;
    public static Properties loadedShaders = null;
    public static Properties shadersConfig = null;
    public static ITextureObject defaultTexture = null;
    public static boolean[] shadowHardwareFilteringEnabled = new boolean[2];
    public static boolean[] shadowMipmapEnabled = new boolean[2];
    public static boolean[] shadowFilterNearest = new boolean[2];
    public static boolean[] shadowColorMipmapEnabled = new boolean[8];
    public static boolean[] shadowColorFilterNearest = new boolean[8];
    public static boolean configTweakBlockDamage = false;
    public static boolean configCloudShadow = false;
    public static float configHandDepthMul = 0.125F;
    public static float configRenderResMul = 1.0F;
    public static float configShadowResMul = 1.0F;
    public static int configTexMinFilB = 0;
    public static int configTexMinFilN = 0;
    public static int configTexMinFilS = 0;
    public static int configTexMagFilB = 0;
    public static int configTexMagFilN = 0;
    public static int configTexMagFilS = 0;
    public static boolean configShadowClipFrustrum = true;
    public static boolean configNormalMap = true;
    public static boolean configSpecularMap = true;
    public static PropertyDefaultTrueFalse configOldLighting = new PropertyDefaultTrueFalse("oldLighting", "Classic Lighting", 0);
    public static PropertyDefaultTrueFalse configOldHandLight = new PropertyDefaultTrueFalse("oldHandLight", "Old Hand Light", 0);
    public static int configAntialiasingLevel = 0;
    public static final int texMinFilRange = 3;
    public static final int texMagFilRange = 2;
    public static final String[] texMinFilDesc = new String[] {"Nearest", "Nearest-Nearest", "Nearest-Linear"};
    public static final String[] texMagFilDesc = new String[] {"Nearest", "Linear"};
    public static final int[] texMinFilValue = new int[] {9728, 9984, 9986};
    public static final int[] texMagFilValue = new int[] {9728, 9729};
    private static IShaderPack shaderPack = null;
    public static boolean shaderPackLoaded = false;
    public static String currentShaderName;
    public static final String SHADER_PACK_NAME_NONE = "OFF";
    public static final String SHADER_PACK_NAME_DEFAULT = "(internal)";
    public static final String SHADER_PACKS_DIR_NAME = "shaderpacks";
    public static final String OPTIONS_FILE_NAME = "optionsshaders.txt";
    public static final File shaderPacksDir;
    static File configFile;
    private static ShaderOption[] shaderPackOptions = null;
    private static Set<String> shaderPackOptionSliders = null;
    static ShaderProfile[] shaderPackProfiles = null;
    static Map<String, ScreenShaderOptions> shaderPackGuiScreens = null;
    static Map<String, IExpressionBool> shaderPackProgramConditions = new HashMap();
    public static final String PATH_SHADERS_PROPERTIES = "/shaders/shaders.properties";
    public static PropertyDefaultFastFancyOff shaderPackClouds = new PropertyDefaultFastFancyOff("clouds", "Clouds", 0);
    public static PropertyDefaultTrueFalse shaderPackOldLighting = new PropertyDefaultTrueFalse("oldLighting", "Classic Lighting", 0);
    public static PropertyDefaultTrueFalse shaderPackOldHandLight = new PropertyDefaultTrueFalse("oldHandLight", "Old Hand Light", 0);
    public static PropertyDefaultTrueFalse shaderPackDynamicHandLight = new PropertyDefaultTrueFalse("dynamicHandLight", "Dynamic Hand Light", 0);
    public static PropertyDefaultTrueFalse shaderPackShadowTranslucent = new PropertyDefaultTrueFalse("shadowTranslucent", "Shadow Translucent", 0);
    public static PropertyDefaultTrueFalse shaderPackUnderwaterOverlay = new PropertyDefaultTrueFalse("underwaterOverlay", "Underwater Overlay", 0);
    public static PropertyDefaultTrueFalse shaderPackSun = new PropertyDefaultTrueFalse("sun", "Sun", 0);
    public static PropertyDefaultTrueFalse shaderPackMoon = new PropertyDefaultTrueFalse("moon", "Moon", 0);
    public static PropertyDefaultTrueFalse shaderPackVignette = new PropertyDefaultTrueFalse("vignette", "Vignette", 0);
    public static PropertyDefaultTrueFalse shaderPackBackFaceSolid = new PropertyDefaultTrueFalse("backFace.solid", "Back-face Solid", 0);
    public static PropertyDefaultTrueFalse shaderPackBackFaceCutout = new PropertyDefaultTrueFalse("backFace.cutout", "Back-face Cutout", 0);
    public static PropertyDefaultTrueFalse shaderPackBackFaceCutoutMipped = new PropertyDefaultTrueFalse("backFace.cutoutMipped", "Back-face Cutout Mipped", 0);
    public static PropertyDefaultTrueFalse shaderPackBackFaceTranslucent = new PropertyDefaultTrueFalse("backFace.translucent", "Back-face Translucent", 0);
    public static PropertyDefaultTrueFalse shaderPackRainDepth = new PropertyDefaultTrueFalse("rain.depth", "Rain Depth", 0);
    public static PropertyDefaultTrueFalse shaderPackBeaconBeamDepth = new PropertyDefaultTrueFalse("beacon.beam.depth", "Rain Depth", 0);
    public static PropertyDefaultTrueFalse shaderPackSeparateAo = new PropertyDefaultTrueFalse("separateAo", "Separate AO", 0);
    public static PropertyDefaultTrueFalse shaderPackFrustumCulling = new PropertyDefaultTrueFalse("frustum.culling", "Frustum Culling", 0);
    private static Map<String, String> shaderPackResources = new HashMap();
    private static World currentWorld = null;
    private static final List<Integer> shaderPackDimensions = new ArrayList();
    private static ICustomTexture[] customTexturesGbuffers = null;
    private static ICustomTexture[] customTexturesComposite = null;
    private static ICustomTexture[] customTexturesDeferred = null;
    private static String noiseTexturePath = null;
    private static CustomUniforms customUniforms = null;
    private static final int STAGE_GBUFFERS = 0;
    private static final int STAGE_COMPOSITE = 1;
    private static final int STAGE_DEFERRED = 2;
    private static final String[] STAGE_NAMES = new String[] {"gbuffers", "composite", "deferred"};
    public static final boolean enableShadersOption = true;
    private static final boolean enableShadersDebug = true;
    public static final boolean saveFinalShaders = System.getProperty("shaders.debug.save", "false").equals("true");
    public static float blockLightLevel05 = 0.5F;
    public static float blockLightLevel06 = 0.6F;
    public static float blockLightLevel08 = 0.8F;
    public static float aoLevel = -1.0F;
    public static float sunPathRotation = 0.0F;
    public static float shadowAngleInterval = 0.0F;
    public static int fogMode = 0;
    public static float fogDensity = 0.0F;
    public static float fogColorR;
    public static float fogColorG;
    public static float fogColorB;
    public static float shadowIntervalSize = 2.0F;
    public static int terrainIconSize = 16;
    public static int[] terrainTextureSize = new int[2];
    private static ICustomTexture noiseTexture;
    private static boolean noiseTextureEnabled = false;
    private static int noiseTextureResolution = 256;
    static final int[] colorTextureImageUnit = new int[] {0, 1, 2, 3, 7, 8, 9, 10};
    private static final int bigBufferSize = (285 + 8 * ProgramCount) * 4;
    private static final ByteBuffer bigBuffer = BufferUtils.createByteBuffer(bigBufferSize).limit(0);
    static final float[] faProjection = new float[16];
    static final float[] faProjectionInverse = new float[16];
    static final float[] faModelView = new float[16];
    static final float[] faModelViewInverse = new float[16];
    static final float[] faShadowProjection = new float[16];
    static final float[] faShadowProjectionInverse = new float[16];
    static final float[] faShadowModelView = new float[16];
    static final float[] faShadowModelViewInverse = new float[16];
    static final FloatBuffer projection = nextFloatBuffer(16);
    static final FloatBuffer projectionInverse = nextFloatBuffer(16);
    static final FloatBuffer modelView = nextFloatBuffer(16);
    static final FloatBuffer modelViewInverse = nextFloatBuffer(16);
    static final FloatBuffer shadowProjection = nextFloatBuffer(16);
    static final FloatBuffer shadowProjectionInverse = nextFloatBuffer(16);
    static final FloatBuffer shadowModelView = nextFloatBuffer(16);
    static final FloatBuffer shadowModelViewInverse = nextFloatBuffer(16);
    static final FloatBuffer previousProjection = nextFloatBuffer(16);
    static final FloatBuffer previousModelView = nextFloatBuffer(16);
    static final FloatBuffer tempMatrixDirectBuffer = nextFloatBuffer(16);
    static final FloatBuffer tempDirectFloatBuffer = nextFloatBuffer(16);
    static final IntBuffer dfbColorTextures = nextIntBuffer(16);
    static final IntBuffer dfbDepthTextures = nextIntBuffer(3);
    static final IntBuffer sfbColorTextures = nextIntBuffer(8);
    static final IntBuffer sfbDepthTextures = nextIntBuffer(2);
    static final IntBuffer dfbDrawBuffers = nextIntBuffer(8);
    static final IntBuffer sfbDrawBuffers = nextIntBuffer(8);
    static final IntBuffer drawBuffersNone = nextIntBuffer(8).limit(0);
    static final IntBuffer drawBuffersColorAtt0 = nextIntBuffer(8).put(36064).position(0).limit(1);
    static final FlipTextures dfbColorTexturesFlip = new FlipTextures(dfbColorTextures, 8);
    static Map<Block, Integer> mapBlockToEntityData;
    private static final String[] formatNames = new String[] {"R8", "RG8", "RGB8", "RGBA8", "R8_SNORM", "RG8_SNORM", "RGB8_SNORM", "RGBA8_SNORM", "R16", "RG16", "RGB16", "RGBA16", "R16_SNORM", "RG16_SNORM", "RGB16_SNORM", "RGBA16_SNORM", "R16F", "RG16F", "RGB16F", "RGBA16F", "R32F", "RG32F", "RGB32F", "RGBA32F", "R32I", "RG32I", "RGB32I", "RGBA32I", "R32UI", "RG32UI", "RGB32UI", "RGBA32UI", "R3_G3_B2", "RGB5_A1", "RGB10_A2", "R11F_G11F_B10F", "RGB9_E5"};
    private static final int[] formatIds = new int[] {33321, 33323, 32849, 32856, 36756, 36757, 36758, 36759, 33322, 33324, 32852, 32859, 36760, 36761, 36762, 36763, 33325, 33327, 34843, 34842, 33326, 33328, 34837, 34836, 33333, 33339, 36227, 36226, 33334, 33340, 36209, 36208, 10768, 32855, 32857, 35898, 35901};
    private static final Pattern patternLoadEntityDataMap = Pattern.compile("\\s*([\\w:]+)\\s*=\\s*([-]?\\d+)\\s*");
    public static int[] entityData = new int[32];
    public static int entityDataIndex = 0;

    private static ByteBuffer nextByteBuffer(int size)
    {
        ByteBuffer bytebuffer = bigBuffer;
        int i = bytebuffer.limit();
        bytebuffer.position(i).limit(i + size);
        return bytebuffer.slice();
    }

    public static IntBuffer nextIntBuffer(int size)
    {
        ByteBuffer bytebuffer = bigBuffer;
        int i = bytebuffer.limit();
        bytebuffer.position(i).limit(i + size * 4);
        return bytebuffer.asIntBuffer();
    }

    private static FloatBuffer nextFloatBuffer(int size)
    {
        ByteBuffer bytebuffer = bigBuffer;
        int i = bytebuffer.limit();
        bytebuffer.position(i).limit(i + size * 4);
        return bytebuffer.asFloatBuffer();
    }

    private static IntBuffer[] nextIntBufferArray(int count, int size)
    {
        IntBuffer[] aintbuffer = new IntBuffer[count];

        for (int i = 0; i < count; ++i)
        {
            aintbuffer[i] = nextIntBuffer(size);
        }

        return aintbuffer;
    }

    public static void loadConfig()
    {
        SMCLog.info("Load shaders configuration.");

        try
        {
            if (!shaderPacksDir.exists())
            {
                shaderPacksDir.mkdir();
            }
        }
        catch (Exception var8)
        {
            SMCLog.severe("Failed to open the shaderpacks directory: " + shaderPacksDir);
        }

        shadersConfig = new PropertiesOrdered();
        shadersConfig.setProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), "");

        if (configFile.exists())
        {
            try
            {
                FileReader filereader = new FileReader(configFile);
                shadersConfig.load(filereader);
                filereader.close();
            }
            catch (Exception var7)
            {
            }
        }

        if (!configFile.exists())
        {
            try
            {
                storeConfig();
            }
            catch (Exception var6)
            {
            }
        }

        EnumShaderOption[] aenumshaderoption = EnumShaderOption.values();

        for (int i = 0; i < aenumshaderoption.length; ++i)
        {
            EnumShaderOption enumshaderoption = aenumshaderoption[i];
            String s = enumshaderoption.getPropertyKey();
            String s1 = enumshaderoption.getValueDefault();
            String s2 = shadersConfig.getProperty(s, s1);
            setEnumShaderOption(enumshaderoption, s2);
        }

        loadShaderPack();
    }

    private static void setEnumShaderOption(EnumShaderOption eso, String str)
    {
        if (str == null)
        {
            str = eso.getValueDefault();
        }

        switch (eso)
        {
            case ANTIALIASING:
                configAntialiasingLevel = Config.parseInt(str, 0);
                break;

            case NORMAL_MAP:
                configNormalMap = Config.parseBoolean(str, true);
                break;

            case SPECULAR_MAP:
                configSpecularMap = Config.parseBoolean(str, true);
                break;

            case RENDER_RES_MUL:
                configRenderResMul = Config.parseFloat(str, 1.0F);
                break;

            case SHADOW_RES_MUL:
                configShadowResMul = Config.parseFloat(str, 1.0F);
                break;

            case HAND_DEPTH_MUL:
                configHandDepthMul = Config.parseFloat(str, 0.125F);
                break;

            case CLOUD_SHADOW:
                configCloudShadow = Config.parseBoolean(str, true);
                break;

            case OLD_HAND_LIGHT:
                configOldHandLight.setPropertyValue(str);
                break;

            case OLD_LIGHTING:
                configOldLighting.setPropertyValue(str);
                break;

            case SHADER_PACK:
                currentShaderName = str;
                break;

            case TWEAK_BLOCK_DAMAGE:
                configTweakBlockDamage = Config.parseBoolean(str, true);
                break;

            case SHADOW_CLIP_FRUSTRUM:
                configShadowClipFrustrum = Config.parseBoolean(str, true);
                break;

            case TEX_MIN_FIL_B:
                configTexMinFilB = Config.parseInt(str, 0);
                break;

            case TEX_MIN_FIL_N:
                configTexMinFilN = Config.parseInt(str, 0);
                break;

            case TEX_MIN_FIL_S:
                configTexMinFilS = Config.parseInt(str, 0);
                break;

            case TEX_MAG_FIL_B:
                configTexMagFilB = Config.parseInt(str, 0);
                break;

            case TEX_MAG_FIL_N:
                configTexMagFilB = Config.parseInt(str, 0);
                break;

            case TEX_MAG_FIL_S:
                configTexMagFilB = Config.parseInt(str, 0);
                break;

            default:
                throw new IllegalArgumentException("Unknown option: " + eso);
        }
    }

    public static void storeConfig()
    {
        SMCLog.info("Save shaders configuration.");

        if (shadersConfig == null)
        {
            shadersConfig = new PropertiesOrdered();
        }

        EnumShaderOption[] aenumshaderoption = EnumShaderOption.values();

        for (int i = 0; i < aenumshaderoption.length; ++i)
        {
            EnumShaderOption enumshaderoption = aenumshaderoption[i];
            String s = enumshaderoption.getPropertyKey();
            String s1 = getEnumShaderOption(enumshaderoption);
            shadersConfig.setProperty(s, s1);
        }

        try
        {
            FileWriter filewriter = new FileWriter(configFile);
            shadersConfig.store(filewriter, null);
            filewriter.close();
        }
        catch (Exception exception)
        {
            SMCLog.severe("Error saving configuration: " + exception.getClass().getName() + ": " + exception.getMessage());
        }
    }

    public static String getEnumShaderOption(EnumShaderOption eso)
    {
        switch (eso)
        {
            case ANTIALIASING:
                return Integer.toString(configAntialiasingLevel);

            case NORMAL_MAP:
                return Boolean.toString(configNormalMap);

            case SPECULAR_MAP:
                return Boolean.toString(configSpecularMap);

            case RENDER_RES_MUL:
                return Float.toString(configRenderResMul);

            case SHADOW_RES_MUL:
                return Float.toString(configShadowResMul);

            case HAND_DEPTH_MUL:
                return Float.toString(configHandDepthMul);

            case CLOUD_SHADOW:
                return Boolean.toString(configCloudShadow);

            case OLD_HAND_LIGHT:
                return configOldHandLight.getPropertyValue();

            case OLD_LIGHTING:
                return configOldLighting.getPropertyValue();

            case SHADER_PACK:
                return currentShaderName;

            case TWEAK_BLOCK_DAMAGE:
                return Boolean.toString(configTweakBlockDamage);

            case SHADOW_CLIP_FRUSTRUM:
                return Boolean.toString(configShadowClipFrustrum);

            case TEX_MIN_FIL_B:
                return Integer.toString(configTexMinFilB);

            case TEX_MIN_FIL_N:
                return Integer.toString(configTexMinFilN);

            case TEX_MIN_FIL_S:
                return Integer.toString(configTexMinFilS);

            case TEX_MAG_FIL_B:
                return Integer.toString(configTexMagFilB);

            case TEX_MAG_FIL_N:
                return Integer.toString(configTexMagFilB);

            case TEX_MAG_FIL_S:
                return Integer.toString(configTexMagFilB);

            default:
                throw new IllegalArgumentException("Unknown option: " + eso);
        }
    }

    public static void setShaderPack(String par1name)
    {
        currentShaderName = par1name;
        shadersConfig.setProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), par1name);
        loadShaderPack();
    }

    public static void loadShaderPack()
    {
        boolean flag = shaderPackLoaded;
        boolean flag1 = isOldLighting();

        if (mc.renderGlobal != null)
        {
            mc.renderGlobal.pauseChunkUpdates();
        }

        shaderPackLoaded = false;

        if (shaderPack != null)
        {
            shaderPack.close();
            shaderPack = null;
            shaderPackResources.clear();
            shaderPackDimensions.clear();
            shaderPackOptions = null;
            shaderPackOptionSliders = null;
            shaderPackProfiles = null;
            shaderPackGuiScreens = null;
            shaderPackProgramConditions.clear();
            shaderPackClouds.resetValue();
            shaderPackOldHandLight.resetValue();
            shaderPackDynamicHandLight.resetValue();
            shaderPackOldLighting.resetValue();
            resetCustomTextures();
            noiseTexturePath = null;
        }

        boolean flag2 = false;

        if (Config.isAntialiasing())
        {
            SMCLog.info("Shaders can not be loaded, Antialiasing is enabled: " + Config.getAntialiasingLevel() + "x");
            flag2 = true;
        }

        if (Config.isAnisotropicFiltering())
        {
            SMCLog.info("Shaders can not be loaded, Anisotropic Filtering is enabled: " + Config.getAnisotropicFilterLevel() + "x");
            flag2 = true;
        }

        if (Config.isFastRender())
        {
            SMCLog.info("Shaders can not be loaded, Fast Render is enabled.");
            flag2 = true;
        }

        String s = shadersConfig.getProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), "(internal)");

        if (!flag2)
        {
            shaderPack = getShaderPack(s);
            shaderPackLoaded = shaderPack != null;
        }

        if (shaderPackLoaded)
        {
            SMCLog.info("Loaded shaderpack: " + getShaderPackName());
        }
        else
        {
            SMCLog.info("No shaderpack loaded.");
            shaderPack = new ShaderPackNone();
        }

        if (saveFinalShaders)
        {
            clearDirectory(new File(shaderPacksDir, "debug"));
        }

        loadShaderPackResources();
        loadShaderPackDimensions();
        shaderPackOptions = loadShaderPackOptions();
        loadShaderPackProperties();
        boolean flag3 = shaderPackLoaded != flag;
        boolean flag4 = isOldLighting() != flag1;

        if (flag3 || flag4)
        {
            DefaultVertexFormats.updateVertexFormats();

            if (Reflector.LightUtil.exists())
            {
                Reflector.LightUtil_itemConsumer.setValue(null);
                Reflector.LightUtil_tessellator.setValue(null);
            }

            updateBlockLightLevel();
        }

        if (mc.getResourcePackRepository() != null)
        {
            CustomBlockLayers.update();
        }

        if (mc.renderGlobal != null)
        {
            mc.renderGlobal.resumeChunkUpdates();
        }

        if ((flag3 || flag4) && mc.getResourceManager() != null)
        {
            mc.scheduleResourcesRefresh();
        }
    }

    public static IShaderPack getShaderPack(String name)
    {
        if (name == null)
        {
            return null;
        }
        else
        {
            name = name.trim();

            if (!name.isEmpty() && !name.equals("OFF"))
            {
                if (name.equals("(internal)"))
                {
                    return new ShaderPackDefault();
                }
                else
                {
                    try
                    {
                        File file1 = new File(shaderPacksDir, name);
                        return file1.isDirectory() ? new ShaderPackFolder(name, file1) : (file1.isFile() && name.toLowerCase().endsWith(".zip") ? new ShaderPackZip(name, file1) : null);
                    }
                    catch (Exception exception)
                    {
                        exception.printStackTrace();
                        return null;
                    }
                }
            }
            else
            {
                return null;
            }
        }
    }

    public static IShaderPack getShaderPack()
    {
        return shaderPack;
    }

    private static void loadShaderPackDimensions()
    {
        shaderPackDimensions.clear();

        for (int i = -128; i <= 128; ++i)
        {
            String s = "/shaders/world" + i;

            if (shaderPack.hasDirectory(s))
            {
                shaderPackDimensions.add(Integer.valueOf(i));
            }
        }

        if (shaderPackDimensions.size() > 0)
        {
            Integer[] ainteger = shaderPackDimensions.toArray(new Integer[shaderPackDimensions.size()]);
            Config.dbg("[Shaders] Worlds: " + Config.arrayToString(ainteger));
        }
    }

    private static void loadShaderPackProperties()
    {
        shaderPackClouds.resetValue();
        shaderPackOldHandLight.resetValue();
        shaderPackDynamicHandLight.resetValue();
        shaderPackOldLighting.resetValue();
        shaderPackShadowTranslucent.resetValue();
        shaderPackUnderwaterOverlay.resetValue();
        shaderPackSun.resetValue();
        shaderPackMoon.resetValue();
        shaderPackVignette.resetValue();
        shaderPackBackFaceSolid.resetValue();
        shaderPackBackFaceCutout.resetValue();
        shaderPackBackFaceCutoutMipped.resetValue();
        shaderPackBackFaceTranslucent.resetValue();
        shaderPackRainDepth.resetValue();
        shaderPackBeaconBeamDepth.resetValue();
        shaderPackSeparateAo.resetValue();
        shaderPackFrustumCulling.resetValue();
        BlockAliases.reset();
        ItemAliases.reset();
        EntityAliases.reset();
        customUniforms = null;

        for (int i = 0; i < ProgramsAll.length; ++i)
        {
            Program program = ProgramsAll[i];
            program.resetProperties();
        }

        if (shaderPack != null)
        {
            BlockAliases.update(shaderPack);
            ItemAliases.update(shaderPack);
            EntityAliases.update(shaderPack);
            String s = "/shaders/shaders.properties";

            try
            {
                InputStream inputstream = shaderPack.getResourceAsStream(s);

                if (inputstream == null)
                {
                    return;
                }

                inputstream = MacroProcessor.process(inputstream, s);
                Properties properties = new PropertiesOrdered();
                properties.load(inputstream);
                inputstream.close();
                shaderPackClouds.loadFrom(properties);
                shaderPackOldHandLight.loadFrom(properties);
                shaderPackDynamicHandLight.loadFrom(properties);
                shaderPackOldLighting.loadFrom(properties);
                shaderPackShadowTranslucent.loadFrom(properties);
                shaderPackUnderwaterOverlay.loadFrom(properties);
                shaderPackSun.loadFrom(properties);
                shaderPackVignette.loadFrom(properties);
                shaderPackMoon.loadFrom(properties);
                shaderPackBackFaceSolid.loadFrom(properties);
                shaderPackBackFaceCutout.loadFrom(properties);
                shaderPackBackFaceCutoutMipped.loadFrom(properties);
                shaderPackBackFaceTranslucent.loadFrom(properties);
                shaderPackRainDepth.loadFrom(properties);
                shaderPackBeaconBeamDepth.loadFrom(properties);
                shaderPackSeparateAo.loadFrom(properties);
                shaderPackFrustumCulling.loadFrom(properties);
                shaderPackOptionSliders = ShaderPackParser.parseOptionSliders(properties, shaderPackOptions);
                shaderPackProfiles = ShaderPackParser.parseProfiles(properties, shaderPackOptions);
                shaderPackGuiScreens = ShaderPackParser.parseGuiScreens(properties, shaderPackProfiles, shaderPackOptions);
                shaderPackProgramConditions = ShaderPackParser.parseProgramConditions(properties, shaderPackOptions);
                customTexturesGbuffers = loadCustomTextures(properties, 0);
                customTexturesComposite = loadCustomTextures(properties, 1);
                customTexturesDeferred = loadCustomTextures(properties, 2);
                noiseTexturePath = properties.getProperty("texture.noise");

                if (noiseTexturePath != null)
                {
                    noiseTextureEnabled = true;
                }

                customUniforms = ShaderPackParser.parseCustomUniforms(properties);
                ShaderPackParser.parseAlphaStates(properties);
                ShaderPackParser.parseBlendStates(properties);
                ShaderPackParser.parseRenderScales(properties);
                ShaderPackParser.parseBuffersFlip(properties);
            }
            catch (IOException var3)
            {
                Config.warn("[Shaders] Error reading: " + s);
            }
        }
    }

    private static ICustomTexture[] loadCustomTextures(Properties props, int stage)
    {
        String s = "texture." + STAGE_NAMES[stage] + ".";
        Set set = props.keySet();
        List<ICustomTexture> list = new ArrayList();

        for (Object o : set)
        {
            String s1 = (String) o;
            if (s1.startsWith(s))
            {
                String s2 = StrUtils.removePrefix(s1, s);
                s2 = StrUtils.removeSuffix(s2, new String[] {".0", ".1", ".2", ".3", ".4", ".5", ".6", ".7", ".8", ".9"});
                String s3 = props.getProperty(s1).trim();
                int i = getTextureIndex(stage, s2);

                if (i < 0)
                {
                    SMCLog.warning("Invalid texture name: " + s1);
                }
                else
                {
                    ICustomTexture icustomtexture = loadCustomTexture(i, s3);

                    if (icustomtexture != null)
                    {
                        SMCLog.info("Custom texture: " + s1 + " = " + s3);
                        list.add(icustomtexture);
                    }
                }
            }
        }

        if (list.size() <= 0)
        {
            return null;
        }
        else
        {
            ICustomTexture[] aicustomtexture = list.toArray(new ICustomTexture[list.size()]);
            return aicustomtexture;
        }
    }

    private static ICustomTexture loadCustomTexture(int textureUnit, String path)
    {
        if (path == null)
        {
            return null;
        }
        else
        {
            path = path.trim();
            return path.indexOf(58) >= 0 ? loadCustomTextureLocation(textureUnit, path) : (path.indexOf(32) >= 0 ? loadCustomTextureRaw(textureUnit, path) : loadCustomTextureShaders(textureUnit, path));
        }
    }

    private static ICustomTexture loadCustomTextureLocation(int textureUnit, String path)
    {
        String s = path.trim();
        int i = 0;

        if (s.startsWith("minecraft:textures/"))
        {
            s = StrUtils.addSuffixCheck(s, ".png");

            if (s.endsWith("_n.png"))
            {
                s = StrUtils.replaceSuffix(s, "_n.png", ".png");
                i = 1;
            }
            else if (s.endsWith("_s.png"))
            {
                s = StrUtils.replaceSuffix(s, "_s.png", ".png");
                i = 2;
            }
        }

        ResourceLocation resourcelocation = new ResourceLocation(s);
        CustomTextureLocation customtexturelocation = new CustomTextureLocation(textureUnit, resourcelocation, i);
        return customtexturelocation;
    }

    private static ICustomTexture loadCustomTextureRaw(int textureUnit, String line)
    {
        ConnectedParser connectedparser = new ConnectedParser("Shaders");
        String[] astring = Config.tokenize(line, " ");
        Deque<String> deque = new ArrayDeque(Arrays.asList(astring));
        String s = deque.poll();
        TextureType texturetype = (TextureType)connectedparser.parseEnum(deque.poll(), TextureType.values(), "texture type");

        if (texturetype == null)
        {
            SMCLog.warning("Invalid raw texture type: " + line);
            return null;
        }
        else
        {
            InternalFormat internalformat = (InternalFormat)connectedparser.parseEnum(deque.poll(), InternalFormat.values(), "internal format");

            if (internalformat == null)
            {
                SMCLog.warning("Invalid raw texture internal format: " + line);
                return null;
            }
            else
            {
                int i = 0;
                int j = 0;
                int k = 0;

                switch (texturetype)
                {
                    case TEXTURE_1D:
                        i = connectedparser.parseInt(deque.poll(), -1);
                        break;

                    case TEXTURE_2D:
                        i = connectedparser.parseInt(deque.poll(), -1);
                        j = connectedparser.parseInt(deque.poll(), -1);
                        break;

                    case TEXTURE_3D:
                        i = connectedparser.parseInt(deque.poll(), -1);
                        j = connectedparser.parseInt(deque.poll(), -1);
                        k = connectedparser.parseInt(deque.poll(), -1);
                        break;

                    case TEXTURE_RECTANGLE:
                        i = connectedparser.parseInt(deque.poll(), -1);
                        j = connectedparser.parseInt(deque.poll(), -1);
                        break;

                    default:
                        SMCLog.warning("Invalid raw texture type: " + texturetype);
                        return null;
                }

                if (i >= 0 && j >= 0 && k >= 0)
                {
                    PixelFormat pixelformat = (PixelFormat)connectedparser.parseEnum(deque.poll(), PixelFormat.values(), "pixel format");

                    if (pixelformat == null)
                    {
                        SMCLog.warning("Invalid raw texture pixel format: " + line);
                        return null;
                    }
                    else
                    {
                        PixelType pixeltype = (PixelType)connectedparser.parseEnum(deque.poll(), PixelType.values(), "pixel type");

                        if (pixeltype == null)
                        {
                            SMCLog.warning("Invalid raw texture pixel type: " + line);
                            return null;
                        }
                        else if (!deque.isEmpty())
                        {
                            SMCLog.warning("Invalid raw texture, too many parameters: " + line);
                            return null;
                        }
                        else
                        {
                            return loadCustomTextureRaw(textureUnit, line, s, texturetype, internalformat, i, j, k, pixelformat, pixeltype);
                        }
                    }
                }
                else
                {
                    SMCLog.warning("Invalid raw texture size: " + line);
                    return null;
                }
            }
        }
    }

    private static ICustomTexture loadCustomTextureRaw(int textureUnit, String line, String path, TextureType type, InternalFormat internalFormat, int width, int height, int depth, PixelFormat pixelFormat, PixelType pixelType)
    {
        try
        {
            String s = "shaders/" + StrUtils.removePrefix(path, "/");
            InputStream inputstream = shaderPack.getResourceAsStream(s);

            if (inputstream == null)
            {
                SMCLog.warning("Raw texture not found: " + path);
                return null;
            }
            else
            {
                byte[] abyte = Config.readAll(inputstream);
                IOUtils.closeQuietly(inputstream);
                ByteBuffer bytebuffer = GLAllocation.createDirectByteBuffer(abyte.length);
                bytebuffer.put(abyte);
                bytebuffer.flip();
                TextureMetadataSection texturemetadatasection = SimpleShaderTexture.loadTextureMetadataSection(s, new TextureMetadataSection(true, true, new ArrayList()));
                CustomTextureRaw customtextureraw = new CustomTextureRaw(type, internalFormat, width, height, depth, pixelFormat, pixelType, bytebuffer, textureUnit, texturemetadatasection.getTextureBlur(), texturemetadatasection.getTextureClamp());
                return customtextureraw;
            }
        }
        catch (IOException ioexception)
        {
            SMCLog.warning("Error loading raw texture: " + path);
            SMCLog.warning(ioexception.getClass().getName() + ": " + ioexception.getMessage());
            return null;
        }
    }

    private static ICustomTexture loadCustomTextureShaders(int textureUnit, String path)
    {
        path = path.trim();

        if (path.indexOf(46) < 0)
        {
            path = path + ".png";
        }

        try
        {
            String s = "shaders/" + StrUtils.removePrefix(path, "/");
            InputStream inputstream = shaderPack.getResourceAsStream(s);

            if (inputstream == null)
            {
                SMCLog.warning("Texture not found: " + path);
                return null;
            }
            else
            {
                IOUtils.closeQuietly(inputstream);
                SimpleShaderTexture simpleshadertexture = new SimpleShaderTexture(s);
                simpleshadertexture.loadTexture(mc.getResourceManager());
                CustomTexture customtexture = new CustomTexture(textureUnit, s, simpleshadertexture);
                return customtexture;
            }
        }
        catch (IOException ioexception)
        {
            SMCLog.warning("Error loading texture: " + path);
            SMCLog.warning(ioexception.getClass().getName() + ": " + ioexception.getMessage());
            return null;
        }
    }

    private static int getTextureIndex(int stage, String name)
    {
        if (stage == 0)
        {
            if (name.equals("texture"))
            {
                return 0;
            }

            if (name.equals("lightmap"))
            {
                return 1;
            }

            if (name.equals("normals"))
            {
                return 2;
            }

            if (name.equals("specular"))
            {
                return 3;
            }

            if (name.equals("shadowtex0") || name.equals("watershadow"))
            {
                return 4;
            }

            if (name.equals("shadow"))
            {
                return waterShadowEnabled ? 5 : 4;
            }

            if (name.equals("shadowtex1"))
            {
                return 5;
            }

            if (name.equals("depthtex0"))
            {
                return 6;
            }

            if (name.equals("gaux1"))
            {
                return 7;
            }

            if (name.equals("gaux2"))
            {
                return 8;
            }

            if (name.equals("gaux3"))
            {
                return 9;
            }

            if (name.equals("gaux4"))
            {
                return 10;
            }

            if (name.equals("depthtex1"))
            {
                return 12;
            }

            if (name.equals("shadowcolor0") || name.equals("shadowcolor"))
            {
                return 13;
            }

            if (name.equals("shadowcolor1"))
            {
                return 14;
            }

            if (name.equals("noisetex"))
            {
                return 15;
            }
        }

        if (stage == 1 || stage == 2)
        {
            if (name.equals("colortex0") || name.equals("colortex0"))
            {
                return 0;
            }

            if (name.equals("colortex1") || name.equals("gdepth"))
            {
                return 1;
            }

            if (name.equals("colortex2") || name.equals("gnormal"))
            {
                return 2;
            }

            if (name.equals("colortex3") || name.equals("composite"))
            {
                return 3;
            }

            if (name.equals("shadowtex0") || name.equals("watershadow"))
            {
                return 4;
            }

            if (name.equals("shadow"))
            {
                return waterShadowEnabled ? 5 : 4;
            }

            if (name.equals("shadowtex1"))
            {
                return 5;
            }

            if (name.equals("depthtex0") || name.equals("gdepthtex"))
            {
                return 6;
            }

            if (name.equals("colortex4") || name.equals("gaux1"))
            {
                return 7;
            }

            if (name.equals("colortex5") || name.equals("gaux2"))
            {
                return 8;
            }

            if (name.equals("colortex6") || name.equals("gaux3"))
            {
                return 9;
            }

            if (name.equals("colortex7") || name.equals("gaux4"))
            {
                return 10;
            }

            if (name.equals("depthtex1"))
            {
                return 11;
            }

            if (name.equals("depthtex2"))
            {
                return 12;
            }

            if (name.equals("shadowcolor0") || name.equals("shadowcolor"))
            {
                return 13;
            }

            if (name.equals("shadowcolor1"))
            {
                return 14;
            }

            if (name.equals("noisetex"))
            {
                return 15;
            }
        }

        return -1;
    }

    private static void bindCustomTextures(ICustomTexture[] cts)
    {
        if (cts != null)
        {
            for (int i = 0; i < cts.length; ++i)
            {
                ICustomTexture icustomtexture = cts[i];
                GlStateManager.setActiveTexture(33984 + icustomtexture.getTextureUnit());
                int j = icustomtexture.getTextureId();
                int k = icustomtexture.getTarget();

                if (k == 3553)
                {
                    GlStateManager.bindTexture(j);
                }
                else
                {
                    GL11.glBindTexture(k, j);
                }
            }
        }
    }

    private static void resetCustomTextures()
    {
        deleteCustomTextures(customTexturesGbuffers);
        deleteCustomTextures(customTexturesComposite);
        deleteCustomTextures(customTexturesDeferred);
        customTexturesGbuffers = null;
        customTexturesComposite = null;
        customTexturesDeferred = null;
    }

    private static void deleteCustomTextures(ICustomTexture[] cts)
    {
        if (cts != null)
        {
            for (int i = 0; i < cts.length; ++i)
            {
                ICustomTexture icustomtexture = cts[i];
                icustomtexture.deleteTexture();
            }
        }
    }

    public static ShaderOption[] getShaderPackOptions(String screenName)
    {
        ShaderOption[] ashaderoption = shaderPackOptions.clone();

        if (shaderPackGuiScreens == null)
        {
            if (shaderPackProfiles != null)
            {
                ShaderOptionProfile shaderoptionprofile = new ShaderOptionProfile(shaderPackProfiles, ashaderoption);
                ashaderoption = (ShaderOption[]) Config.addObjectToArray(ashaderoption, shaderoptionprofile, 0);
            }

            ashaderoption = getVisibleOptions(ashaderoption);
            return ashaderoption;
        }
        else
        {
            String s = screenName != null ? "screen." + screenName : "screen";
            ScreenShaderOptions screenshaderoptions = shaderPackGuiScreens.get(s);

            if (screenshaderoptions == null)
            {
                return new ShaderOption[0];
            }
            else
            {
                ShaderOption[] ashaderoption1 = screenshaderoptions.getShaderOptions();
                List<ShaderOption> list = new ArrayList();

                for (int i = 0; i < ashaderoption1.length; ++i)
                {
                    ShaderOption shaderoption = ashaderoption1[i];

                    if (shaderoption == null)
                    {
                        list.add(null);
                    }
                    else if (shaderoption instanceof ShaderOptionRest)
                    {
                        ShaderOption[] ashaderoption2 = getShaderOptionsRest(shaderPackGuiScreens, ashaderoption);
                        list.addAll(Arrays.asList(ashaderoption2));
                    }
                    else
                    {
                        list.add(shaderoption);
                    }
                }

                ShaderOption[] ashaderoption3 = list.toArray(new ShaderOption[list.size()]);
                return ashaderoption3;
            }
        }
    }

    public static int getShaderPackColumns(String screenName, int def)
    {
        String s = screenName != null ? "screen." + screenName : "screen";

        if (shaderPackGuiScreens == null)
        {
            return def;
        }
        else
        {
            ScreenShaderOptions screenshaderoptions = shaderPackGuiScreens.get(s);
            return screenshaderoptions == null ? def : screenshaderoptions.getColumns();
        }
    }

    private static ShaderOption[] getShaderOptionsRest(Map<String, ScreenShaderOptions> mapScreens, ShaderOption[] ops)
    {
        Set<String> set = new HashSet();

        for (String s : mapScreens.keySet())
        {
            ScreenShaderOptions screenshaderoptions = mapScreens.get(s);
            ShaderOption[] ashaderoption = screenshaderoptions.getShaderOptions();

            for (int i = 0; i < ashaderoption.length; ++i)
            {
                ShaderOption shaderoption = ashaderoption[i];

                if (shaderoption != null)
                {
                    set.add(shaderoption.getName());
                }
            }
        }

        List<ShaderOption> list = new ArrayList();

        for (int j = 0; j < ops.length; ++j)
        {
            ShaderOption shaderoption1 = ops[j];

            if (shaderoption1.isVisible())
            {
                String s1 = shaderoption1.getName();

                if (!set.contains(s1))
                {
                    list.add(shaderoption1);
                }
            }
        }

        ShaderOption[] ashaderoption1 = list.toArray(new ShaderOption[list.size()]);
        return ashaderoption1;
    }

    public static ShaderOption getShaderOption(String name)
    {
        return ShaderUtils.getShaderOption(name, shaderPackOptions);
    }

    public static ShaderOption[] getShaderPackOptions()
    {
        return shaderPackOptions;
    }

    public static boolean isShaderPackOptionSlider(String name)
    {
        return shaderPackOptionSliders != null && shaderPackOptionSliders.contains(name);
    }

    private static ShaderOption[] getVisibleOptions(ShaderOption[] ops)
    {
        List<ShaderOption> list = new ArrayList();

        for (int i = 0; i < ops.length; ++i)
        {
            ShaderOption shaderoption = ops[i];

            if (shaderoption.isVisible())
            {
                list.add(shaderoption);
            }
        }

        ShaderOption[] ashaderoption = list.toArray(new ShaderOption[list.size()]);
        return ashaderoption;
    }

    public static void saveShaderPackOptions()
    {
        saveShaderPackOptions(shaderPackOptions, shaderPack);
    }

    private static void saveShaderPackOptions(ShaderOption[] sos, IShaderPack sp)
    {
        Properties properties = new PropertiesOrdered();

        if (shaderPackOptions != null)
        {
            for (int i = 0; i < sos.length; ++i)
            {
                ShaderOption shaderoption = sos[i];

                if (shaderoption.isChanged() && shaderoption.isEnabled())
                {
                    properties.setProperty(shaderoption.getName(), shaderoption.getValue());
                }
            }
        }

        try
        {
            saveOptionProperties(sp, properties);
        }
        catch (IOException ioexception)
        {
            Config.warn("[Shaders] Error saving configuration for " + shaderPack.getName());
            ioexception.printStackTrace();
        }
    }

    private static void saveOptionProperties(IShaderPack sp, Properties props) throws IOException
    {
        String s = "shaderpacks/" + sp.getName() + ".txt";
        File file1 = new File(Minecraft.getMinecraft().mcDataDir, s);

        if (props.isEmpty())
        {
            file1.delete();
        }
        else
        {
            FileOutputStream fileoutputstream = new FileOutputStream(file1);
            props.store(fileoutputstream, null);
            fileoutputstream.flush();
            fileoutputstream.close();
        }
    }

    private static ShaderOption[] loadShaderPackOptions()
    {
        try
        {
            String[] astring = programs.getProgramNames();
            ShaderOption[] ashaderoption = ShaderPackParser.parseShaderPackOptions(shaderPack, astring, shaderPackDimensions);
            Properties properties = loadOptionProperties(shaderPack);

            for (int i = 0; i < ashaderoption.length; ++i)
            {
                ShaderOption shaderoption = ashaderoption[i];
                String s = properties.getProperty(shaderoption.getName());

                if (s != null)
                {
                    shaderoption.resetValue();

                    if (!shaderoption.setValue(s))
                    {
                        Config.warn("[Shaders] Invalid value, option: " + shaderoption.getName() + ", value: " + s);
                    }
                }
            }

            return ashaderoption;
        }
        catch (IOException ioexception)
        {
            Config.warn("[Shaders] Error reading configuration for " + shaderPack.getName());
            ioexception.printStackTrace();
            return null;
        }
    }

    private static Properties loadOptionProperties(IShaderPack sp) throws IOException
    {
        Properties properties = new PropertiesOrdered();
        String s = "shaderpacks/" + sp.getName() + ".txt";
        File file1 = new File(Minecraft.getMinecraft().mcDataDir, s);

        if (file1.exists() && file1.isFile() && file1.canRead())
        {
            FileInputStream fileinputstream = new FileInputStream(file1);
            properties.load(fileinputstream);
            fileinputstream.close();
            return properties;
        }
        else
        {
            return properties;
        }
    }

    public static ShaderOption[] getChangedOptions(ShaderOption[] ops)
    {
        List<ShaderOption> list = new ArrayList();

        for (int i = 0; i < ops.length; ++i)
        {
            ShaderOption shaderoption = ops[i];

            if (shaderoption.isEnabled() && shaderoption.isChanged())
            {
                list.add(shaderoption);
            }
        }

        ShaderOption[] ashaderoption = list.toArray(new ShaderOption[list.size()]);
        return ashaderoption;
    }

    private static String applyOptions(String line, ShaderOption[] ops)
    {
        if (ops != null && ops.length > 0)
        {
            for (int i = 0; i < ops.length; ++i)
            {
                ShaderOption shaderoption = ops[i];

                if (shaderoption.matchesLine(line))
                {
                    line = shaderoption.getSourceLine();
                    break;
                }
            }

            return line;
        }
        else
        {
            return line;
        }
    }

    public static ArrayList listOfShaders()
    {
        ArrayList<String> arraylist = new ArrayList();
        arraylist.add("OFF");
        arraylist.add("(internal)");
        int i = arraylist.size();

        try
        {
            if (!shaderPacksDir.exists())
            {
                shaderPacksDir.mkdir();
            }

            File[] afile = shaderPacksDir.listFiles();

            for (int j = 0; j < afile.length; ++j)
            {
                File file1 = afile[j];
                String s = file1.getName();

                if (file1.isDirectory())
                {
                    if (!s.equals("debug"))
                    {
                        File file2 = new File(file1, "shaders");

                        if (file2.exists() && file2.isDirectory())
                        {
                            arraylist.add(s);
                        }
                    }
                }
                else if (file1.isFile() && s.toLowerCase().endsWith(".zip"))
                {
                    arraylist.add(s);
                }
            }
        }
        catch (Exception var7)
        {
        }

        List<String> list = arraylist.subList(i, arraylist.size());
        Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
        return arraylist;
    }

    public static int checkFramebufferStatus(String location)
    {
        int i = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);

        if (i != 36053)
        {
            System.err.format("FramebufferStatus 0x%04X at %s\n", Integer.valueOf(i), location);
        }

        return i;
    }

    public static int checkGLError(String location)
    {
        int i = GlStateManager.glGetError();

        if (i != 0 && GlErrors.isEnabled(i))
        {
            String s = Config.getGlErrorString(i);
            String s1 = getErrorInfo(i, location);
            String s2 = String.format("OpenGL error: %s (%s)%s, at: %s", Integer.valueOf(i), s, s1, location);
            SMCLog.severe(s2);

            if (Config.isShowGlErrors() && TimedEvent.isActive("ShowGlErrorShaders", 10000L))
            {
                String s3 = I18n.format("of.message.openglError", Integer.valueOf(i), s);
                printChat(s3);
            }
        }

        return i;
    }

    private static String getErrorInfo(int errorCode, String location)
    {
        StringBuilder stringbuilder = new StringBuilder();

        if (errorCode == 1286)
        {
            int i = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
            String s = getFramebufferStatusText(i);
            String s1 = ", fbStatus: " + i + " (" + s + ")";
            stringbuilder.append(s1);
        }

        String s2 = activeProgram.getName();

        if (s2.isEmpty())
        {
            s2 = "none";
        }

        stringbuilder.append(", program: " + s2);
        Program program = getProgramById(activeProgramID);

        if (program != activeProgram)
        {
            String s3 = program.getName();

            if (s3.isEmpty())
            {
                s3 = "none";
            }

            stringbuilder.append(" (" + s3 + ")");
        }

        if (location.equals("setDrawBuffers"))
        {
            stringbuilder.append(", drawBuffers: " + activeProgram.getDrawBufSettings());
        }

        return stringbuilder.toString();
    }

    private static Program getProgramById(int programID)
    {
        for (int i = 0; i < ProgramsAll.length; ++i)
        {
            Program program = ProgramsAll[i];

            if (program.getId() == programID)
            {
                return program;
            }
        }

        return ProgramNone;
    }

    private static String getFramebufferStatusText(int fbStatusCode)
    {
        switch (fbStatusCode)
        {
            case 33305:
                return "Undefined";

            case 36053:
                return "Complete";

            case 36054:
                return "Incomplete attachment";

            case 36055:
                return "Incomplete missing attachment";

            case 36059:
                return "Incomplete draw buffer";

            case 36060:
                return "Incomplete read buffer";

            case 36061:
                return "Unsupported";

            case 36182:
                return "Incomplete multisample";

            case 36264:
                return "Incomplete layer targets";

            default:
                return "Unknown";
        }
    }

    private static void printChat(String str)
    {
        mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(str));
    }

    private static void printChatAndLogError(String str)
    {
        SMCLog.severe(str);
        mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(str));
    }

    public static void printIntBuffer(String title, IntBuffer buf)
    {
        StringBuilder stringbuilder = new StringBuilder(128);
        stringbuilder.append(title).append(" [pos ").append(buf.position()).append(" lim ").append(buf.limit()).append(" cap ").append(buf.capacity()).append(" :");
        int i = buf.limit();

        for (int j = 0; j < i; ++j)
        {
            stringbuilder.append(" ").append(buf.get(j));
        }

        stringbuilder.append("]");
        SMCLog.info(stringbuilder.toString());
    }

    public static void startup(Minecraft mc)
    {
        checkShadersModInstalled();
        mc = Minecraft.getMinecraft();
        capabilities = GLContext.getCapabilities();
        glVersionString = GL11.glGetString(GL11.GL_VERSION);
        glVendorString = GL11.glGetString(GL11.GL_VENDOR);
        glRendererString = GL11.glGetString(GL11.GL_RENDERER);
        SMCLog.info("OpenGL Version: " + glVersionString);
        SMCLog.info("Vendor:  " + glVendorString);
        SMCLog.info("Renderer: " + glRendererString);
        SMCLog.info("Capabilities: " + (capabilities.OpenGL20 ? " 2.0 " : " - ") + (capabilities.OpenGL21 ? " 2.1 " : " - ") + (capabilities.OpenGL30 ? " 3.0 " : " - ") + (capabilities.OpenGL32 ? " 3.2 " : " - ") + (capabilities.OpenGL40 ? " 4.0 " : " - "));
        SMCLog.info("GL_MAX_DRAW_BUFFERS: " + GL11.glGetInteger(GL20.GL_MAX_DRAW_BUFFERS));
        SMCLog.info("GL_MAX_COLOR_ATTACHMENTS_EXT: " + GL11.glGetInteger(36063));
        SMCLog.info("GL_MAX_TEXTURE_IMAGE_UNITS: " + GL11.glGetInteger(GL20.GL_MAX_TEXTURE_IMAGE_UNITS));
        hasGlGenMipmap = capabilities.OpenGL30;
        loadConfig();
    }

    public static void updateBlockLightLevel()
    {
        if (isOldLighting())
        {
            blockLightLevel05 = 0.5F;
            blockLightLevel06 = 0.6F;
            blockLightLevel08 = 0.8F;
        }
        else
        {
            blockLightLevel05 = 1.0F;
            blockLightLevel06 = 1.0F;
            blockLightLevel08 = 1.0F;
        }
    }

    public static boolean isOldHandLight()
    {
        return !configOldHandLight.isDefault() ? configOldHandLight.isTrue() : (shaderPackOldHandLight.isDefault() || shaderPackOldHandLight.isTrue());
    }

    public static boolean isDynamicHandLight()
    {
        return shaderPackDynamicHandLight.isDefault() || shaderPackDynamicHandLight.isTrue();
    }

    public static boolean isOldLighting()
    {
        return !configOldLighting.isDefault() ? configOldLighting.isTrue() : (shaderPackOldLighting.isDefault() || shaderPackOldLighting.isTrue());
    }

    public static boolean isRenderShadowTranslucent()
    {
        return !shaderPackShadowTranslucent.isFalse();
    }

    public static boolean isUnderwaterOverlay()
    {
        return !shaderPackUnderwaterOverlay.isFalse();
    }

    public static boolean isSun()
    {
        return !shaderPackSun.isFalse();
    }

    public static boolean isMoon()
    {
        return !shaderPackMoon.isFalse();
    }

    public static boolean isVignette()
    {
        return !shaderPackVignette.isFalse();
    }

    public static boolean isRenderBackFace(EnumWorldBlockLayer blockLayerIn)
    {
        switch (blockLayerIn)
        {
            case SOLID:
                return shaderPackBackFaceSolid.isTrue();

            case CUTOUT:
                return shaderPackBackFaceCutout.isTrue();

            case CUTOUT_MIPPED:
                return shaderPackBackFaceCutoutMipped.isTrue();

            case TRANSLUCENT:
                return shaderPackBackFaceTranslucent.isTrue();

            default:
                return false;
        }
    }

    public static boolean isRainDepth()
    {
        return shaderPackRainDepth.isTrue();
    }

    public static boolean isBeaconBeamDepth()
    {
        return shaderPackBeaconBeamDepth.isTrue();
    }

    public static boolean isSeparateAo()
    {
        return shaderPackSeparateAo.isTrue();
    }

    public static boolean isFrustumCulling()
    {
        return !shaderPackFrustumCulling.isFalse();
    }

    public static void init()
    {
        boolean flag;

        if (!isInitializedOnce)
        {
            isInitializedOnce = true;
            flag = true;
        }
        else
        {
            flag = false;
        }

        if (!isShaderPackInitialized)
        {
            checkGLError("Shaders.init pre");

            if (getShaderPackName() != null)
            {
            }

            if (!capabilities.OpenGL20)
            {
                printChatAndLogError("No OpenGL 2.0");
            }

            if (!capabilities.GL_EXT_framebuffer_object)
            {
                printChatAndLogError("No EXT_framebuffer_object");
            }

            dfbDrawBuffers.position(0).limit(8);
            dfbColorTextures.position(0).limit(16);
            dfbDepthTextures.position(0).limit(3);
            sfbDrawBuffers.position(0).limit(8);
            sfbDepthTextures.position(0).limit(2);
            sfbColorTextures.position(0).limit(8);
            usedColorBuffers = 4;
            usedDepthBuffers = 1;
            usedShadowColorBuffers = 0;
            usedShadowDepthBuffers = 0;
            usedColorAttachs = 1;
            usedDrawBuffers = 1;
            Arrays.fill(gbuffersFormat, 6408);
            Arrays.fill(gbuffersClear, true);
            Arrays.fill(gbuffersClearColor, null);
            Arrays.fill(shadowHardwareFilteringEnabled, false);
            Arrays.fill(shadowMipmapEnabled, false);
            Arrays.fill(shadowFilterNearest, false);
            Arrays.fill(shadowColorMipmapEnabled, false);
            Arrays.fill(shadowColorFilterNearest, false);
            centerDepthSmoothEnabled = false;
            noiseTextureEnabled = false;
            sunPathRotation = 0.0F;
            shadowIntervalSize = 2.0F;
            shadowMapWidth = 1024;
            shadowMapHeight = 1024;
            spShadowMapWidth = 1024;
            spShadowMapHeight = 1024;
            shadowMapFOV = 90.0F;
            shadowMapHalfPlane = 160.0F;
            shadowMapIsOrtho = true;
            shadowDistanceRenderMul = -1.0F;
            aoLevel = -1.0F;
            useEntityAttrib = false;
            useMidTexCoordAttrib = false;
            useTangentAttrib = false;
            waterShadowEnabled = false;
            hasGeometryShaders = false;
            updateBlockLightLevel();
            Smoother.resetValues();
            shaderUniforms.reset();

            if (customUniforms != null)
            {
                customUniforms.reset();
            }

            ShaderProfile shaderprofile = ShaderUtils.detectProfile(shaderPackProfiles, shaderPackOptions, false);
            String s = "";

            if (currentWorld != null)
            {
                int i = currentWorld.provider.getDimensionId();

                if (shaderPackDimensions.contains(Integer.valueOf(i)))
                {
                    s = "world" + i + "/";
                }
            }

            for (int k = 0; k < ProgramsAll.length; ++k)
            {
                Program program = ProgramsAll[k];
                program.resetId();
                program.resetConfiguration();

                if (program.getProgramStage() != ProgramStage.NONE)
                {
                    String s1 = program.getName();
                    String s2 = s + s1;
                    boolean flag1 = true;

                    if (shaderPackProgramConditions.containsKey(s2))
                    {
                        flag1 = flag1 && shaderPackProgramConditions.get(s2).eval();
                    }

                    if (shaderprofile != null)
                    {
                        flag1 = flag1 && !shaderprofile.isProgramDisabled(s2);
                    }

                    if (!flag1)
                    {
                        SMCLog.info("Program disabled: " + s2);
                        s1 = "<disabled>";
                        s2 = s + s1;
                    }

                    String s3 = "/shaders/" + s2;
                    String s4 = s3 + ".vsh";
                    String s5 = s3 + ".gsh";
                    String s6 = s3 + ".fsh";
                    setupProgram(program, s4, s5, s6);
                    int j = program.getId();

                    if (j > 0)
                    {
                        SMCLog.info("Program loaded: " + s2);
                    }

                    initDrawBuffers(program);
                    updateToggleBuffers(program);
                }
            }

            hasDeferredPrograms = false;

            for (int l = 0; l < ProgramsDeferred.length; ++l)
            {
                if (ProgramsDeferred[l].getId() != 0)
                {
                    hasDeferredPrograms = true;
                    break;
                }
            }

            usedColorAttachs = usedColorBuffers;
            shadowPassInterval = usedShadowDepthBuffers > 0 ? 1 : 0;
            shouldSkipDefaultShadow = usedShadowDepthBuffers > 0;
            SMCLog.info("usedColorBuffers: " + usedColorBuffers);
            SMCLog.info("usedDepthBuffers: " + usedDepthBuffers);
            SMCLog.info("usedShadowColorBuffers: " + usedShadowColorBuffers);
            SMCLog.info("usedShadowDepthBuffers: " + usedShadowDepthBuffers);
            SMCLog.info("usedColorAttachs: " + usedColorAttachs);
            SMCLog.info("usedDrawBuffers: " + usedDrawBuffers);
            dfbDrawBuffers.position(0).limit(usedDrawBuffers);
            dfbColorTextures.position(0).limit(usedColorBuffers * 2);
            dfbColorTexturesFlip.reset();

            for (int i1 = 0; i1 < usedDrawBuffers; ++i1)
            {
                dfbDrawBuffers.put(i1, 36064 + i1);
            }

            int j1 = GL11.glGetInteger(GL20.GL_MAX_DRAW_BUFFERS);

            if (usedDrawBuffers > j1)
            {
                printChatAndLogError("[Shaders] Error: Not enough draw buffers, needed: " + usedDrawBuffers + ", available: " + j1);
            }

            sfbDrawBuffers.position(0).limit(usedShadowColorBuffers);

            for (int k1 = 0; k1 < usedShadowColorBuffers; ++k1)
            {
                sfbDrawBuffers.put(k1, 36064 + k1);
            }

            for (int l1 = 0; l1 < ProgramsAll.length; ++l1)
            {
                Program program1 = ProgramsAll[l1];
                Program program2;

                for (program2 = program1; program2.getId() == 0 && program2.getProgramBackup() != program2; program2 = program2.getProgramBackup())
                {
                }

                if (program2 != program1 && program1 != ProgramShadow)
                {
                    program1.copyFrom(program2);
                }
            }

            resize();
            resizeShadow();

            if (noiseTextureEnabled)
            {
                setupNoiseTexture();
            }

            if (defaultTexture == null)
            {
                defaultTexture = ShadersTex.createDefaultTexture();
            }

            GlStateManager.pushMatrix();
            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            preCelestialRotate();
            postCelestialRotate();
            GlStateManager.popMatrix();
            isShaderPackInitialized = true;
            loadEntityDataMap();
            resetDisplayLists();

            if (!flag)
            {
            }

            checkGLError("Shaders.init");
        }
    }

    private static void initDrawBuffers(Program p)
    {
        int i = GL11.glGetInteger(GL20.GL_MAX_DRAW_BUFFERS);
        Arrays.fill(p.getToggleColorTextures(), false);

        if (p == ProgramFinal)
        {
            p.setDrawBuffers(null);
        }
        else if (p.getId() == 0)
        {
            if (p == ProgramShadow)
            {
                p.setDrawBuffers(drawBuffersNone);
            }
            else
            {
                p.setDrawBuffers(drawBuffersColorAtt0);
            }
        }
        else
        {
            String s = p.getDrawBufSettings();

            if (s == null)
            {
                if (p != ProgramShadow && p != ProgramShadowSolid && p != ProgramShadowCutout)
                {
                    p.setDrawBuffers(dfbDrawBuffers);
                    usedDrawBuffers = usedColorBuffers;
                    Arrays.fill(p.getToggleColorTextures(), 0, usedColorBuffers, true);
                }
                else
                {
                    p.setDrawBuffers(sfbDrawBuffers);
                }
            }
            else
            {
                IntBuffer intbuffer = p.getDrawBuffersBuffer();
                int j = s.length();
                usedDrawBuffers = Math.max(usedDrawBuffers, j);
                j = Math.min(j, i);
                p.setDrawBuffers(intbuffer);
                intbuffer.limit(j);

                for (int k = 0; k < j; ++k)
                {
                    int l = getDrawBuffer(p, s, k);
                    intbuffer.put(k, l);
                }
            }
        }
    }

    private static int getDrawBuffer(Program p, String str, int ic)
    {
        int i = 0;

        if (ic >= str.length())
        {
            return i;
        }
        else
        {
            int j = str.charAt(ic) - 48;

            if (p == ProgramShadow)
            {
                if (j >= 0 && j <= 1)
                {
                    i = j + 36064;
                    usedShadowColorBuffers = Math.max(usedShadowColorBuffers, j);
                }

                return i;
            }
            else
            {
                if (j >= 0 && j <= 7)
                {
                    p.getToggleColorTextures()[j] = true;
                    i = j + 36064;
                    usedColorAttachs = Math.max(usedColorAttachs, j);
                    usedColorBuffers = Math.max(usedColorBuffers, j);
                }

                return i;
            }
        }
    }

    private static void updateToggleBuffers(Program p)
    {
        boolean[] aboolean = p.getToggleColorTextures();
        Boolean[] aboolean1 = p.getBuffersFlip();

        for (int i = 0; i < aboolean1.length; ++i)
        {
            Boolean obool = aboolean1[i];

            if (obool != null)
            {
                aboolean[i] = obool.booleanValue();
            }
        }
    }

    public static void resetDisplayLists()
    {
        SMCLog.info("Reset model renderers");
        ++countResetDisplayLists;
        SMCLog.info("Reset world renderers");
        mc.renderGlobal.loadRenderers();
    }

    private static void setupProgram(Program program, String vShaderPath, String gShaderPath, String fShaderPath)
    {
        checkGLError("pre setupProgram");
        int i = ARBShaderObjects.glCreateProgramObjectARB();
        checkGLError("create");

        if (i != 0)
        {
            progUseEntityAttrib = false;
            progUseMidTexCoordAttrib = false;
            progUseTangentAttrib = false;
            int j = createVertShader(program, vShaderPath);
            int k = createGeomShader(program, gShaderPath);
            int l = createFragShader(program, fShaderPath);
            checkGLError("create");

            if (j == 0 && k == 0 && l == 0)
            {
                ARBShaderObjects.glDeleteObjectARB(i);
                i = 0;
                program.resetId();
            }
            else
            {
                if (j != 0)
                {
                    ARBShaderObjects.glAttachObjectARB(i, j);
                    checkGLError("attach");
                }

                if (k != 0)
                {
                    ARBShaderObjects.glAttachObjectARB(i, k);
                    checkGLError("attach");

                    if (progArbGeometryShader4)
                    {
                        ARBGeometryShader4.glProgramParameteriARB(i, 36315, 4);
                        ARBGeometryShader4.glProgramParameteriARB(i, 36316, 5);
                        ARBGeometryShader4.glProgramParameteriARB(i, 36314, progMaxVerticesOut);
                        checkGLError("arbGeometryShader4");
                    }

                    hasGeometryShaders = true;
                }

                if (l != 0)
                {
                    ARBShaderObjects.glAttachObjectARB(i, l);
                    checkGLError("attach");
                }

                if (progUseEntityAttrib)
                {
                    ARBVertexShader.glBindAttribLocationARB(i, entityAttrib, "mc_Entity");
                    checkGLError("mc_Entity");
                }

                if (progUseMidTexCoordAttrib)
                {
                    ARBVertexShader.glBindAttribLocationARB(i, midTexCoordAttrib, "mc_midTexCoord");
                    checkGLError("mc_midTexCoord");
                }

                if (progUseTangentAttrib)
                {
                    ARBVertexShader.glBindAttribLocationARB(i, tangentAttrib, "at_tangent");
                    checkGLError("at_tangent");
                }

                ARBShaderObjects.glLinkProgramARB(i);

                if (GL20.glGetProgrami(i, 35714) != 1)
                {
                    SMCLog.severe("Error linking program: " + i + " (" + program.getName() + ")");
                }

                printLogInfo(i, program.getName());

                if (j != 0)
                {
                    ARBShaderObjects.glDetachObjectARB(i, j);
                    ARBShaderObjects.glDeleteObjectARB(j);
                }

                if (k != 0)
                {
                    ARBShaderObjects.glDetachObjectARB(i, k);
                    ARBShaderObjects.glDeleteObjectARB(k);
                }

                if (l != 0)
                {
                    ARBShaderObjects.glDetachObjectARB(i, l);
                    ARBShaderObjects.glDeleteObjectARB(l);
                }

                program.setId(i);
                program.setRef(i);
                useProgram(program);
                ARBShaderObjects.glValidateProgramARB(i);
                useProgram(ProgramNone);
                printLogInfo(i, program.getName());
                int i1 = GL20.glGetProgrami(i, 35715);

                if (i1 != 1)
                {
                    String s = "\"";
                    printChatAndLogError("[Shaders] Error: Invalid program " + s + program.getName() + s);
                    ARBShaderObjects.glDeleteObjectARB(i);
                    i = 0;
                    program.resetId();
                }
            }
        }
    }

    private static int createVertShader(Program program, String filename)
    {
        int i = ARBShaderObjects.glCreateShaderObjectARB(ARBVertexShader.GL_VERTEX_SHADER_ARB);

        if (i == 0)
        {
            return 0;
        }
        else
        {
            StringBuilder stringbuilder = new StringBuilder(131072);
            BufferedReader bufferedreader = null;

            try
            {
                bufferedreader = new BufferedReader(getShaderReader(filename));
            }
            catch (Exception var10)
            {
                ARBShaderObjects.glDeleteObjectARB(i);
                return 0;
            }

            ShaderOption[] ashaderoption = getChangedOptions(shaderPackOptions);
            List<String> list = new ArrayList();

            if (bufferedreader != null)
            {
                try
                {
                    bufferedreader = ShaderPackParser.resolveIncludes(bufferedreader, filename, shaderPack, 0, list, 0);
                    MacroState macrostate = new MacroState();

                    while (true)
                    {
                        String s = bufferedreader.readLine();

                        if (s == null)
                        {
                            bufferedreader.close();
                            break;
                        }

                        s = applyOptions(s, ashaderoption);
                        stringbuilder.append(s).append('\n');

                        if (macrostate.processLine(s))
                        {
                            ShaderLine shaderline = ShaderParser.parseLine(s);

                            if (shaderline != null)
                            {
                                if (shaderline.isAttribute("mc_Entity"))
                                {
                                    useEntityAttrib = true;
                                    progUseEntityAttrib = true;
                                }
                                else if (shaderline.isAttribute("mc_midTexCoord"))
                                {
                                    useMidTexCoordAttrib = true;
                                    progUseMidTexCoordAttrib = true;
                                }
                                else if (shaderline.isAttribute("at_tangent"))
                                {
                                    useTangentAttrib = true;
                                    progUseTangentAttrib = true;
                                }

                                if (shaderline.isConstInt("countInstances"))
                                {
                                    program.setCountInstances(shaderline.getValueInt());
                                    SMCLog.info("countInstances: " + program.getCountInstances());
                                }
                            }
                        }
                    }
                }
                catch (Exception exception)
                {
                    SMCLog.severe("Couldn't read " + filename + "!");
                    exception.printStackTrace();
                    ARBShaderObjects.glDeleteObjectARB(i);
                    return 0;
                }
            }

            if (saveFinalShaders)
            {
                saveShader(filename, stringbuilder.toString());
            }

            ARBShaderObjects.glShaderSourceARB(i, stringbuilder);
            ARBShaderObjects.glCompileShaderARB(i);

            if (GL20.glGetShaderi(i, 35713) != 1)
            {
                SMCLog.severe("Error compiling vertex shader: " + filename);
            }

            printShaderLogInfo(i, filename, list);
            return i;
        }
    }

    private static int createGeomShader(Program program, String filename)
    {
        int i = ARBShaderObjects.glCreateShaderObjectARB(36313);

        if (i == 0)
        {
            return 0;
        }
        else
        {
            StringBuilder stringbuilder = new StringBuilder(131072);
            BufferedReader bufferedreader = null;

            try
            {
                bufferedreader = new BufferedReader(getShaderReader(filename));
            }
            catch (Exception var11)
            {
                ARBShaderObjects.glDeleteObjectARB(i);
                return 0;
            }

            ShaderOption[] ashaderoption = getChangedOptions(shaderPackOptions);
            List<String> list = new ArrayList();
            progArbGeometryShader4 = false;
            progMaxVerticesOut = 3;

            if (bufferedreader != null)
            {
                try
                {
                    bufferedreader = ShaderPackParser.resolveIncludes(bufferedreader, filename, shaderPack, 0, list, 0);
                    MacroState macrostate = new MacroState();

                    while (true)
                    {
                        String s = bufferedreader.readLine();

                        if (s == null)
                        {
                            bufferedreader.close();
                            break;
                        }

                        s = applyOptions(s, ashaderoption);
                        stringbuilder.append(s).append('\n');

                        if (macrostate.processLine(s))
                        {
                            ShaderLine shaderline = ShaderParser.parseLine(s);

                            if (shaderline != null)
                            {
                                if (shaderline.isExtension("GL_ARB_geometry_shader4"))
                                {
                                    String s1 = Config.normalize(shaderline.getValue());

                                    if (s1.equals("enable") || s1.equals("require") || s1.equals("warn"))
                                    {
                                        progArbGeometryShader4 = true;
                                    }
                                }

                                if (shaderline.isConstInt("maxVerticesOut"))
                                {
                                    progMaxVerticesOut = shaderline.getValueInt();
                                }
                            }
                        }
                    }
                }
                catch (Exception exception)
                {
                    SMCLog.severe("Couldn't read " + filename + "!");
                    exception.printStackTrace();
                    ARBShaderObjects.glDeleteObjectARB(i);
                    return 0;
                }
            }

            if (saveFinalShaders)
            {
                saveShader(filename, stringbuilder.toString());
            }

            ARBShaderObjects.glShaderSourceARB(i, stringbuilder);
            ARBShaderObjects.glCompileShaderARB(i);

            if (GL20.glGetShaderi(i, 35713) != 1)
            {
                SMCLog.severe("Error compiling geometry shader: " + filename);
            }

            printShaderLogInfo(i, filename, list);
            return i;
        }
    }

    private static int createFragShader(Program program, String filename)
    {
        int i = ARBShaderObjects.glCreateShaderObjectARB(ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);

        if (i == 0)
        {
            return 0;
        }
        else
        {
            StringBuilder stringbuilder = new StringBuilder(131072);
            BufferedReader bufferedreader = null;

            try
            {
                bufferedreader = new BufferedReader(getShaderReader(filename));
            }
            catch (Exception var14)
            {
                ARBShaderObjects.glDeleteObjectARB(i);
                return 0;
            }

            ShaderOption[] ashaderoption = getChangedOptions(shaderPackOptions);
            List<String> list = new ArrayList();

            if (bufferedreader != null)
            {
                try
                {
                    bufferedreader = ShaderPackParser.resolveIncludes(bufferedreader, filename, shaderPack, 0, list, 0);
                    MacroState macrostate = new MacroState();

                    while (true)
                    {
                        String s = bufferedreader.readLine();

                        if (s == null)
                        {
                            bufferedreader.close();
                            break;
                        }

                        s = applyOptions(s, ashaderoption);
                        stringbuilder.append(s).append('\n');

                        if (macrostate.processLine(s))
                        {
                            ShaderLine shaderline = ShaderParser.parseLine(s);

                            if (shaderline != null)
                            {
                                if (shaderline.isUniform())
                                {
                                    String s6 = shaderline.getName();
                                    int l1;

                                    if ((l1 = ShaderParser.getShadowDepthIndex(s6)) >= 0)
                                    {
                                        usedShadowDepthBuffers = Math.max(usedShadowDepthBuffers, l1 + 1);
                                    }
                                    else if ((l1 = ShaderParser.getShadowColorIndex(s6)) >= 0)
                                    {
                                        usedShadowColorBuffers = Math.max(usedShadowColorBuffers, l1 + 1);
                                    }
                                    else if ((l1 = ShaderParser.getDepthIndex(s6)) >= 0)
                                    {
                                        usedDepthBuffers = Math.max(usedDepthBuffers, l1 + 1);
                                    }
                                    else if (s6.equals("gdepth") && gbuffersFormat[1] == 6408)
                                    {
                                        gbuffersFormat[1] = 34836;
                                    }
                                    else if ((l1 = ShaderParser.getColorIndex(s6)) >= 0)
                                    {
                                        usedColorBuffers = Math.max(usedColorBuffers, l1 + 1);
                                    }
                                    else if (s6.equals("centerDepthSmooth"))
                                    {
                                        centerDepthSmoothEnabled = true;
                                    }
                                }
                                else if (!shaderline.isConstInt("shadowMapResolution") && !shaderline.isProperty("SHADOWRES"))
                                {
                                    if (!shaderline.isConstFloat("shadowMapFov") && !shaderline.isProperty("SHADOWFOV"))
                                    {
                                        if (!shaderline.isConstFloat("shadowDistance") && !shaderline.isProperty("SHADOWHPL"))
                                        {
                                            if (shaderline.isConstFloat("shadowDistanceRenderMul"))
                                            {
                                                shadowDistanceRenderMul = shaderline.getValueFloat();
                                                SMCLog.info("Shadow distance render mul: " + shadowDistanceRenderMul);
                                            }
                                            else if (shaderline.isConstFloat("shadowIntervalSize"))
                                            {
                                                shadowIntervalSize = shaderline.getValueFloat();
                                                SMCLog.info("Shadow map interval size: " + shadowIntervalSize);
                                            }
                                            else if (shaderline.isConstBool("generateShadowMipmap", true))
                                            {
                                                Arrays.fill(shadowMipmapEnabled, true);
                                                SMCLog.info("Generate shadow mipmap");
                                            }
                                            else if (shaderline.isConstBool("generateShadowColorMipmap", true))
                                            {
                                                Arrays.fill(shadowColorMipmapEnabled, true);
                                                SMCLog.info("Generate shadow color mipmap");
                                            }
                                            else if (shaderline.isConstBool("shadowHardwareFiltering", true))
                                            {
                                                Arrays.fill(shadowHardwareFilteringEnabled, true);
                                                SMCLog.info("Hardware shadow filtering enabled.");
                                            }
                                            else if (shaderline.isConstBool("shadowHardwareFiltering0", true))
                                            {
                                                shadowHardwareFilteringEnabled[0] = true;
                                                SMCLog.info("shadowHardwareFiltering0");
                                            }
                                            else if (shaderline.isConstBool("shadowHardwareFiltering1", true))
                                            {
                                                shadowHardwareFilteringEnabled[1] = true;
                                                SMCLog.info("shadowHardwareFiltering1");
                                            }
                                            else if (shaderline.isConstBool("shadowtex0Mipmap", "shadowtexMipmap", true))
                                            {
                                                shadowMipmapEnabled[0] = true;
                                                SMCLog.info("shadowtex0Mipmap");
                                            }
                                            else if (shaderline.isConstBool("shadowtex1Mipmap", true))
                                            {
                                                shadowMipmapEnabled[1] = true;
                                                SMCLog.info("shadowtex1Mipmap");
                                            }
                                            else if (shaderline.isConstBool("shadowcolor0Mipmap", "shadowColor0Mipmap", true))
                                            {
                                                shadowColorMipmapEnabled[0] = true;
                                                SMCLog.info("shadowcolor0Mipmap");
                                            }
                                            else if (shaderline.isConstBool("shadowcolor1Mipmap", "shadowColor1Mipmap", true))
                                            {
                                                shadowColorMipmapEnabled[1] = true;
                                                SMCLog.info("shadowcolor1Mipmap");
                                            }
                                            else if (shaderline.isConstBool("shadowtex0Nearest", "shadowtexNearest", "shadow0MinMagNearest", true))
                                            {
                                                shadowFilterNearest[0] = true;
                                                SMCLog.info("shadowtex0Nearest");
                                            }
                                            else if (shaderline.isConstBool("shadowtex1Nearest", "shadow1MinMagNearest", true))
                                            {
                                                shadowFilterNearest[1] = true;
                                                SMCLog.info("shadowtex1Nearest");
                                            }
                                            else if (shaderline.isConstBool("shadowcolor0Nearest", "shadowColor0Nearest", "shadowColor0MinMagNearest", true))
                                            {
                                                shadowColorFilterNearest[0] = true;
                                                SMCLog.info("shadowcolor0Nearest");
                                            }
                                            else if (shaderline.isConstBool("shadowcolor1Nearest", "shadowColor1Nearest", "shadowColor1MinMagNearest", true))
                                            {
                                                shadowColorFilterNearest[1] = true;
                                                SMCLog.info("shadowcolor1Nearest");
                                            }
                                            else if (!shaderline.isConstFloat("wetnessHalflife") && !shaderline.isProperty("WETNESSHL"))
                                            {
                                                if (!shaderline.isConstFloat("drynessHalflife") && !shaderline.isProperty("DRYNESSHL"))
                                                {
                                                    if (shaderline.isConstFloat("eyeBrightnessHalflife"))
                                                    {
                                                        eyeBrightnessHalflife = shaderline.getValueFloat();
                                                        SMCLog.info("Eye brightness halflife: " + eyeBrightnessHalflife);
                                                    }
                                                    else if (shaderline.isConstFloat("centerDepthHalflife"))
                                                    {
                                                        centerDepthSmoothHalflife = shaderline.getValueFloat();
                                                        SMCLog.info("Center depth halflife: " + centerDepthSmoothHalflife);
                                                    }
                                                    else if (shaderline.isConstFloat("sunPathRotation"))
                                                    {
                                                        sunPathRotation = shaderline.getValueFloat();
                                                        SMCLog.info("Sun path rotation: " + sunPathRotation);
                                                    }
                                                    else if (shaderline.isConstFloat("ambientOcclusionLevel"))
                                                    {
                                                        aoLevel = Config.limit(shaderline.getValueFloat(), 0.0F, 1.0F);
                                                        SMCLog.info("AO Level: " + aoLevel);
                                                    }
                                                    else if (shaderline.isConstInt("superSamplingLevel"))
                                                    {
                                                        int i1 = shaderline.getValueInt();

                                                        if (i1 > 1)
                                                        {
                                                            SMCLog.info("Super sampling level: " + i1 + "x");
                                                            superSamplingLevel = i1;
                                                        }
                                                        else
                                                        {
                                                            superSamplingLevel = 1;
                                                        }
                                                    }
                                                    else if (shaderline.isConstInt("noiseTextureResolution"))
                                                    {
                                                        noiseTextureResolution = shaderline.getValueInt();
                                                        noiseTextureEnabled = true;
                                                        SMCLog.info("Noise texture enabled");
                                                        SMCLog.info("Noise texture resolution: " + noiseTextureResolution);
                                                    }
                                                    else if (shaderline.isConstIntSuffix("Format"))
                                                    {
                                                        String s5 = StrUtils.removeSuffix(shaderline.getName(), "Format");
                                                        String s7 = shaderline.getValue();
                                                        int i2 = getBufferIndexFromString(s5);
                                                        int l = getTextureFormatFromString(s7);

                                                        if (i2 >= 0 && l != 0)
                                                        {
                                                            gbuffersFormat[i2] = l;
                                                            SMCLog.info("%s format: %s", s5, s7);
                                                        }
                                                    }
                                                    else if (shaderline.isConstBoolSuffix("Clear", false))
                                                    {
                                                        if (ShaderParser.isComposite(filename) || ShaderParser.isDeferred(filename))
                                                        {
                                                            String s4 = StrUtils.removeSuffix(shaderline.getName(), "Clear");
                                                            int k1 = getBufferIndexFromString(s4);

                                                            if (k1 >= 0)
                                                            {
                                                                gbuffersClear[k1] = false;
                                                                SMCLog.info("%s clear disabled", s4);
                                                            }
                                                        }
                                                    }
                                                    else if (shaderline.isConstVec4Suffix("ClearColor"))
                                                    {
                                                        if (ShaderParser.isComposite(filename) || ShaderParser.isDeferred(filename))
                                                        {
                                                            String s3 = StrUtils.removeSuffix(shaderline.getName(), "ClearColor");
                                                            int j1 = getBufferIndexFromString(s3);

                                                            if (j1 >= 0)
                                                            {
                                                                Vector4f vector4f = shaderline.getValueVec4();

                                                                if (vector4f != null)
                                                                {
                                                                    gbuffersClearColor[j1] = vector4f;
                                                                    SMCLog.info("%s clear color: %s %s %s %s", s3, Float.valueOf(vector4f.getX()), Float.valueOf(vector4f.getY()), Float.valueOf(vector4f.getZ()), Float.valueOf(vector4f.getW()));
                                                                }
                                                                else
                                                                {
                                                                    SMCLog.warning("Invalid color value: " + shaderline.getValue());
                                                                }
                                                            }
                                                        }
                                                    }
                                                    else if (shaderline.isProperty("GAUX4FORMAT", "RGBA32F"))
                                                    {
                                                        gbuffersFormat[7] = 34836;
                                                        SMCLog.info("gaux4 format : RGB32AF");
                                                    }
                                                    else if (shaderline.isProperty("GAUX4FORMAT", "RGB32F"))
                                                    {
                                                        gbuffersFormat[7] = 34837;
                                                        SMCLog.info("gaux4 format : RGB32F");
                                                    }
                                                    else if (shaderline.isProperty("GAUX4FORMAT", "RGB16"))
                                                    {
                                                        gbuffersFormat[7] = 32852;
                                                        SMCLog.info("gaux4 format : RGB16");
                                                    }
                                                    else if (shaderline.isConstBoolSuffix("MipmapEnabled", true))
                                                    {
                                                        if (ShaderParser.isComposite(filename) || ShaderParser.isDeferred(filename) || ShaderParser.isFinal(filename))
                                                        {
                                                            String s2 = StrUtils.removeSuffix(shaderline.getName(), "MipmapEnabled");
                                                            int j = getBufferIndexFromString(s2);

                                                            if (j >= 0)
                                                            {
                                                                int k = program.getCompositeMipmapSetting();
                                                                k = k | 1 << j;
                                                                program.setCompositeMipmapSetting(k);
                                                                SMCLog.info("%s mipmap enabled", s2);
                                                            }
                                                        }
                                                    }
                                                    else if (shaderline.isProperty("DRAWBUFFERS"))
                                                    {
                                                        String s1 = shaderline.getValue();

                                                        if (ShaderParser.isValidDrawBuffers(s1))
                                                        {
                                                            program.setDrawBufSettings(s1);
                                                        }
                                                        else
                                                        {
                                                            SMCLog.warning("Invalid draw buffers: " + s1);
                                                        }
                                                    }
                                                }
                                                else
                                                {
                                                    drynessHalfLife = shaderline.getValueFloat();
                                                    SMCLog.info("Dryness halflife: " + drynessHalfLife);
                                                }
                                            }
                                            else
                                            {
                                                wetnessHalfLife = shaderline.getValueFloat();
                                                SMCLog.info("Wetness halflife: " + wetnessHalfLife);
                                            }
                                        }
                                        else
                                        {
                                            shadowMapHalfPlane = shaderline.getValueFloat();
                                            shadowMapIsOrtho = true;
                                            SMCLog.info("Shadow map distance: " + shadowMapHalfPlane);
                                        }
                                    }
                                    else
                                    {
                                        shadowMapFOV = shaderline.getValueFloat();
                                        shadowMapIsOrtho = false;
                                        SMCLog.info("Shadow map field of view: " + shadowMapFOV);
                                    }
                                }
                                else
                                {
                                    spShadowMapWidth = spShadowMapHeight = shaderline.getValueInt();
                                    shadowMapWidth = shadowMapHeight = Math.round((float)spShadowMapWidth * configShadowResMul);
                                    SMCLog.info("Shadow map resolution: " + spShadowMapWidth);
                                }
                            }
                        }
                    }
                }
                catch (Exception exception)
                {
                    SMCLog.severe("Couldn't read " + filename + "!");
                    exception.printStackTrace();
                    ARBShaderObjects.glDeleteObjectARB(i);
                    return 0;
                }
            }

            if (saveFinalShaders)
            {
                saveShader(filename, stringbuilder.toString());
            }

            ARBShaderObjects.glShaderSourceARB(i, stringbuilder);
            ARBShaderObjects.glCompileShaderARB(i);

            if (GL20.glGetShaderi(i, 35713) != 1)
            {
                SMCLog.severe("Error compiling fragment shader: " + filename);
            }

            printShaderLogInfo(i, filename, list);
            return i;
        }
    }

    private static Reader getShaderReader(String filename)
    {
        return new InputStreamReader(shaderPack.getResourceAsStream(filename));
    }

    public static void saveShader(String filename, String code)
    {
        try
        {
            File file1 = new File(shaderPacksDir, "debug/" + filename);
            file1.getParentFile().mkdirs();
            Config.writeFile(file1, code);
        }
        catch (IOException ioexception)
        {
            Config.warn("Error saving: " + filename);
            ioexception.printStackTrace();
        }
    }

    private static void clearDirectory(File dir)
    {
        if (dir.exists())
        {
            if (dir.isDirectory())
            {
                File[] afile = dir.listFiles();

                if (afile != null)
                {
                    for (int i = 0; i < afile.length; ++i)
                    {
                        File file1 = afile[i];

                        if (file1.isDirectory())
                        {
                            clearDirectory(file1);
                        }

                        file1.delete();
                    }
                }
            }
        }
    }

    private static boolean printLogInfo(int obj, String name)
    {
        IntBuffer intbuffer = BufferUtils.createIntBuffer(1);
        ARBShaderObjects.glGetObjectParameterivARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB, intbuffer);
        int i = intbuffer.get();

        if (i > 1)
        {
            ByteBuffer bytebuffer = BufferUtils.createByteBuffer(i);
            intbuffer.flip();
            ARBShaderObjects.glGetInfoLogARB(obj, intbuffer, bytebuffer);
            byte[] abyte = new byte[i];
            bytebuffer.get(abyte);

            if (abyte[i - 1] == 0)
            {
                abyte[i - 1] = 10;
            }

            String s = new String(abyte, Charsets.US_ASCII);
            s = StrUtils.trim(s, " \n\r\t");
            SMCLog.info("Info log: " + name + "\n" + s);
            return false;
        }
        else
        {
            return true;
        }
    }

    private static boolean printShaderLogInfo(int shader, String name, List<String> listFiles)
    {
        IntBuffer intbuffer = BufferUtils.createIntBuffer(1);
        int i = GL20.glGetShaderi(shader, 35716);

        if (i <= 1)
        {
            return true;
        }
        else
        {
            for (int j = 0; j < listFiles.size(); ++j)
            {
                String s = listFiles.get(j);
                SMCLog.info("File: " + (j + 1) + " = " + s);
            }

            String s1 = GL20.glGetShaderInfoLog(shader, i);
            s1 = StrUtils.trim(s1, " \n\r\t");
            SMCLog.info("Shader info log: " + name + "\n" + s1);
            return false;
        }
    }

    public static void setDrawBuffers(IntBuffer drawBuffers)
    {
        if (drawBuffers == null)
        {
            drawBuffers = drawBuffersNone;
        }

        if (activeDrawBuffers != drawBuffers)
        {
            activeDrawBuffers = drawBuffers;
            GL20.glDrawBuffers(drawBuffers);
            checkGLError("setDrawBuffers");
        }
    }

    public static void useProgram(Program program)
    {
        checkGLError("pre-useProgram");

        if (isShadowPass)
        {
            program = ProgramShadow;
        }
        else if (isEntitiesGlowing)
        {
            program = ProgramEntitiesGlowing;
        }

        if (activeProgram != program)
        {
            updateAlphaBlend(activeProgram, program);
            activeProgram = program;
            int i = program.getId();
            activeProgramID = i;
            ARBShaderObjects.glUseProgramObjectARB(i);

            if (checkGLError("useProgram") != 0)
            {
                program.setId(0);
                i = program.getId();
                activeProgramID = i;
                ARBShaderObjects.glUseProgramObjectARB(i);
            }

            shaderUniforms.setProgram(i);

            if (customUniforms != null)
            {
                customUniforms.setProgram(i);
            }

            if (i != 0)
            {
                IntBuffer intbuffer = program.getDrawBuffers();

                if (isRenderingDfb)
                {
                    setDrawBuffers(intbuffer);
                }

                activeCompositeMipmapSetting = program.getCompositeMipmapSetting();

                switch (program.getProgramStage())
                {
                    case GBUFFERS:
                        setProgramUniform1i(uniform_texture, 0);
                        setProgramUniform1i(uniform_lightmap, 1);
                        setProgramUniform1i(uniform_normals, 2);
                        setProgramUniform1i(uniform_specular, 3);
                        setProgramUniform1i(uniform_shadow, waterShadowEnabled ? 5 : 4);
                        setProgramUniform1i(uniform_watershadow, 4);
                        setProgramUniform1i(uniform_shadowtex0, 4);
                        setProgramUniform1i(uniform_shadowtex1, 5);
                        setProgramUniform1i(uniform_depthtex0, 6);

                        if (customTexturesGbuffers != null || hasDeferredPrograms)
                        {
                            setProgramUniform1i(uniform_gaux1, 7);
                            setProgramUniform1i(uniform_gaux2, 8);
                            setProgramUniform1i(uniform_gaux3, 9);
                            setProgramUniform1i(uniform_gaux4, 10);
                        }

                        setProgramUniform1i(uniform_depthtex1, 11);
                        setProgramUniform1i(uniform_shadowcolor, 13);
                        setProgramUniform1i(uniform_shadowcolor0, 13);
                        setProgramUniform1i(uniform_shadowcolor1, 14);
                        setProgramUniform1i(uniform_noisetex, 15);
                        break;

                    case DEFERRED:
                    case COMPOSITE:
                        setProgramUniform1i(uniform_gcolor, 0);
                        setProgramUniform1i(uniform_gdepth, 1);
                        setProgramUniform1i(uniform_gnormal, 2);
                        setProgramUniform1i(uniform_composite, 3);
                        setProgramUniform1i(uniform_gaux1, 7);
                        setProgramUniform1i(uniform_gaux2, 8);
                        setProgramUniform1i(uniform_gaux3, 9);
                        setProgramUniform1i(uniform_gaux4, 10);
                        setProgramUniform1i(uniform_colortex0, 0);
                        setProgramUniform1i(uniform_colortex1, 1);
                        setProgramUniform1i(uniform_colortex2, 2);
                        setProgramUniform1i(uniform_colortex3, 3);
                        setProgramUniform1i(uniform_colortex4, 7);
                        setProgramUniform1i(uniform_colortex5, 8);
                        setProgramUniform1i(uniform_colortex6, 9);
                        setProgramUniform1i(uniform_colortex7, 10);
                        setProgramUniform1i(uniform_shadow, waterShadowEnabled ? 5 : 4);
                        setProgramUniform1i(uniform_watershadow, 4);
                        setProgramUniform1i(uniform_shadowtex0, 4);
                        setProgramUniform1i(uniform_shadowtex1, 5);
                        setProgramUniform1i(uniform_gdepthtex, 6);
                        setProgramUniform1i(uniform_depthtex0, 6);
                        setProgramUniform1i(uniform_depthtex1, 11);
                        setProgramUniform1i(uniform_depthtex2, 12);
                        setProgramUniform1i(uniform_shadowcolor, 13);
                        setProgramUniform1i(uniform_shadowcolor0, 13);
                        setProgramUniform1i(uniform_shadowcolor1, 14);
                        setProgramUniform1i(uniform_noisetex, 15);
                        break;

                    case SHADOW:
                        setProgramUniform1i(uniform_tex, 0);
                        setProgramUniform1i(uniform_texture, 0);
                        setProgramUniform1i(uniform_lightmap, 1);
                        setProgramUniform1i(uniform_normals, 2);
                        setProgramUniform1i(uniform_specular, 3);
                        setProgramUniform1i(uniform_shadow, waterShadowEnabled ? 5 : 4);
                        setProgramUniform1i(uniform_watershadow, 4);
                        setProgramUniform1i(uniform_shadowtex0, 4);
                        setProgramUniform1i(uniform_shadowtex1, 5);

                        if (customTexturesGbuffers != null)
                        {
                            setProgramUniform1i(uniform_gaux1, 7);
                            setProgramUniform1i(uniform_gaux2, 8);
                            setProgramUniform1i(uniform_gaux3, 9);
                            setProgramUniform1i(uniform_gaux4, 10);
                        }

                        setProgramUniform1i(uniform_shadowcolor, 13);
                        setProgramUniform1i(uniform_shadowcolor0, 13);
                        setProgramUniform1i(uniform_shadowcolor1, 14);
                        setProgramUniform1i(uniform_noisetex, 15);
                }

                ItemStack itemstack = mc.thePlayer != null ? mc.thePlayer.getHeldItem() : null;
                Item item = itemstack != null ? itemstack.getItem() : null;
                int j = -1;
                Block block = null;

                if (item != null)
                {
                    j = Item.itemRegistry.getIDForObject(item);
                    block = Block.blockRegistry.getObjectById(j);
                    j = ItemAliases.getItemAliasId(j);
                }

                int k = block != null ? block.getLightValue() : 0;
                setProgramUniform1i(uniform_heldItemId, j);
                setProgramUniform1i(uniform_heldBlockLightValue, k);
                setProgramUniform1i(uniform_fogMode, fogEnabled ? fogMode : 0);
                setProgramUniform1f(uniform_fogDensity, fogEnabled ? fogDensity : 0.0F);
                setProgramUniform3f(uniform_fogColor, fogColorR, fogColorG, fogColorB);
                setProgramUniform3f(uniform_skyColor, skyColorR, skyColorG, skyColorB);
                setProgramUniform1i(uniform_worldTime, (int)(worldTime % 24000L));
                setProgramUniform1i(uniform_worldDay, (int)(worldTime / 24000L));
                setProgramUniform1i(uniform_moonPhase, moonPhase);
                setProgramUniform1i(uniform_frameCounter, frameCounter);
                setProgramUniform1f(uniform_frameTime, frameTime);
                setProgramUniform1f(uniform_frameTimeCounter, frameTimeCounter);
                setProgramUniform1f(uniform_sunAngle, sunAngle);
                setProgramUniform1f(uniform_shadowAngle, shadowAngle);
                setProgramUniform1f(uniform_rainStrength, rainStrength);
                setProgramUniform1f(uniform_aspectRatio, (float)renderWidth / (float)renderHeight);
                setProgramUniform1f(uniform_viewWidth, (float)renderWidth);
                setProgramUniform1f(uniform_viewHeight, (float)renderHeight);
                setProgramUniform1f(uniform_near, 0.05F);
                setProgramUniform1f(uniform_far, (float)(mc.gameSettings.renderDistanceChunks * 16));
                setProgramUniform3f(uniform_sunPosition, sunPosition[0], sunPosition[1], sunPosition[2]);
                setProgramUniform3f(uniform_moonPosition, moonPosition[0], moonPosition[1], moonPosition[2]);
                setProgramUniform3f(uniform_shadowLightPosition, shadowLightPosition[0], shadowLightPosition[1], shadowLightPosition[2]);
                setProgramUniform3f(uniform_upPosition, upPosition[0], upPosition[1], upPosition[2]);
                setProgramUniform3f(uniform_previousCameraPosition, (float)previousCameraPositionX, (float)previousCameraPositionY, (float)previousCameraPositionZ);
                setProgramUniform3f(uniform_cameraPosition, (float)cameraPositionX, (float)cameraPositionY, (float)cameraPositionZ);
                setProgramUniformMatrix4ARB(uniform_gbufferModelView, false, modelView);
                setProgramUniformMatrix4ARB(uniform_gbufferModelViewInverse, false, modelViewInverse);
                setProgramUniformMatrix4ARB(uniform_gbufferPreviousProjection, false, previousProjection);
                setProgramUniformMatrix4ARB(uniform_gbufferProjection, false, projection);
                setProgramUniformMatrix4ARB(uniform_gbufferProjectionInverse, false, projectionInverse);
                setProgramUniformMatrix4ARB(uniform_gbufferPreviousModelView, false, previousModelView);

                if (usedShadowDepthBuffers > 0)
                {
                    setProgramUniformMatrix4ARB(uniform_shadowProjection, false, shadowProjection);
                    setProgramUniformMatrix4ARB(uniform_shadowProjectionInverse, false, shadowProjectionInverse);
                    setProgramUniformMatrix4ARB(uniform_shadowModelView, false, shadowModelView);
                    setProgramUniformMatrix4ARB(uniform_shadowModelViewInverse, false, shadowModelViewInverse);
                }

                setProgramUniform1f(uniform_wetness, wetness);
                setProgramUniform1f(uniform_eyeAltitude, eyePosY);
                setProgramUniform2i(uniform_eyeBrightness, eyeBrightness & 65535, eyeBrightness >> 16);
                setProgramUniform2i(uniform_eyeBrightnessSmooth, Math.round(eyeBrightnessFadeX), Math.round(eyeBrightnessFadeY));
                setProgramUniform2i(uniform_terrainTextureSize, terrainTextureSize[0], terrainTextureSize[1]);
                setProgramUniform1i(uniform_terrainIconSize, terrainIconSize);
                setProgramUniform1i(uniform_isEyeInWater, isEyeInWater);
                setProgramUniform1f(uniform_nightVision, nightVision);
                setProgramUniform1f(uniform_blindness, blindness);
                setProgramUniform1f(uniform_screenBrightness, mc.gameSettings.gammaSetting);
                setProgramUniform1i(uniform_hideGUI, mc.gameSettings.hideGUI ? 1 : 0);
                setProgramUniform1f(uniform_centerDepthSmooth, centerDepthSmooth);
                setProgramUniform2i(uniform_atlasSize, atlasSizeX, atlasSizeY);

                if (customUniforms != null)
                {
                    customUniforms.update();
                }

                checkGLError("end useProgram");
            }
        }
    }

    private static void updateAlphaBlend(Program programOld, Program programNew)
    {
        if (programOld.getAlphaState() != null)
        {
            GlStateManager.unlockAlpha();
        }

        if (programOld.getBlendState() != null)
        {
            GlStateManager.unlockBlend();
        }

        GlAlphaState glalphastate = programNew.getAlphaState();

        if (glalphastate != null)
        {
            GlStateManager.lockAlpha(glalphastate);
        }

        GlBlendState glblendstate = programNew.getBlendState();

        if (glblendstate != null)
        {
            GlStateManager.lockBlend(glblendstate);
        }
    }

    private static void setProgramUniform1i(ShaderUniform1i su, int value)
    {
        su.setValue(value);
    }

    private static void setProgramUniform2i(ShaderUniform2i su, int i0, int i1)
    {
        su.setValue(i0, i1);
    }

    private static void setProgramUniform1f(ShaderUniform1f su, float value)
    {
        su.setValue(value);
    }

    private static void setProgramUniform3f(ShaderUniform3f su, float f0, float f1, float f2)
    {
        su.setValue(f0, f1, f2);
    }

    private static void setProgramUniformMatrix4ARB(ShaderUniformM4 su, boolean transpose, FloatBuffer matrix)
    {
        su.setValue(transpose, matrix);
    }

    public static int getBufferIndexFromString(String name)
    {
        return !name.equals("colortex0") && !name.equals("gcolor") ? (!name.equals("colortex1") && !name.equals("gdepth") ? (!name.equals("colortex2") && !name.equals("gnormal") ? (!name.equals("colortex3") && !name.equals("composite") ? (!name.equals("colortex4") && !name.equals("gaux1") ? (!name.equals("colortex5") && !name.equals("gaux2") ? (!name.equals("colortex6") && !name.equals("gaux3") ? (!name.equals("colortex7") && !name.equals("gaux4") ? -1 : 7) : 6) : 5) : 4) : 3) : 2) : 1) : 0;
    }

    private static int getTextureFormatFromString(String par)
    {
        par = par.trim();

        for (int i = 0; i < formatNames.length; ++i)
        {
            String s = formatNames[i];

            if (par.equals(s))
            {
                return formatIds[i];
            }
        }

        return 0;
    }

    private static void setupNoiseTexture()
    {
        if (noiseTexture == null && noiseTexturePath != null)
        {
            noiseTexture = loadCustomTexture(15, noiseTexturePath);
        }

        if (noiseTexture == null)
        {
            noiseTexture = new HFNoiseTexture(noiseTextureResolution, noiseTextureResolution);
        }
    }

    private static void loadEntityDataMap()
    {
        mapBlockToEntityData = new IdentityHashMap(300);

        if (mapBlockToEntityData.isEmpty())
        {
            for (ResourceLocation resourcelocation : Block.blockRegistry.getKeys())
            {
                Block block = Block.blockRegistry.getObject(resourcelocation);
                int i = Block.blockRegistry.getIDForObject(block);
                mapBlockToEntityData.put(block, Integer.valueOf(i));
            }
        }

        BufferedReader bufferedreader = null;

        try
        {
            bufferedreader = new BufferedReader(new InputStreamReader(shaderPack.getResourceAsStream("/mc_Entity_x.txt")));
        }
        catch (Exception var8)
        {
        }

        if (bufferedreader != null)
        {
            String s1;

            try
            {
                while ((s1 = bufferedreader.readLine()) != null)
                {
                    Matcher matcher = patternLoadEntityDataMap.matcher(s1);

                    if (matcher.matches())
                    {
                        String s2 = matcher.group(1);
                        String s = matcher.group(2);
                        int j = Integer.parseInt(s);
                        Block block1 = Block.getBlockFromName(s2);

                        if (block1 != null)
                        {
                            mapBlockToEntityData.put(block1, Integer.valueOf(j));
                        }
                        else
                        {
                            SMCLog.warning("Unknown block name %s", s2);
                        }
                    }
                    else
                    {
                        SMCLog.warning("unmatched %s\n", s1);
                    }
                }
            }
            catch (Exception var9)
            {
                SMCLog.warning("Error parsing mc_Entity_x.txt");
            }
        }

        if (bufferedreader != null)
        {
            try
            {
                bufferedreader.close();
            }
            catch (Exception var7)
            {
            }
        }
    }

    private static IntBuffer fillIntBufferZero(IntBuffer buf)
    {
        int i = buf.limit();

        for (int j = buf.position(); j < i; ++j)
        {
            buf.put(j, 0);
        }

        return buf;
    }

    public static void uninit()
    {
        if (isShaderPackInitialized)
        {
            checkGLError("Shaders.uninit pre");

            for (int i = 0; i < ProgramsAll.length; ++i)
            {
                Program program = ProgramsAll[i];

                if (program.getRef() != 0)
                {
                    ARBShaderObjects.glDeleteObjectARB(program.getRef());
                    checkGLError("del programRef");
                }

                program.setRef(0);
                program.setId(0);
                program.setDrawBufSettings(null);
                program.setDrawBuffers(null);
                program.setCompositeMipmapSetting(0);
            }

            hasDeferredPrograms = false;

            if (dfb != 0)
            {
                EXTFramebufferObject.glDeleteFramebuffersEXT(dfb);
                dfb = 0;
                checkGLError("del dfb");
            }

            if (sfb != 0)
            {
                EXTFramebufferObject.glDeleteFramebuffersEXT(sfb);
                sfb = 0;
                checkGLError("del sfb");
            }

            if (dfbDepthTextures != null)
            {
                GlStateManager.deleteTextures(dfbDepthTextures);
                fillIntBufferZero(dfbDepthTextures);
                checkGLError("del dfbDepthTextures");
            }

            if (dfbColorTextures != null)
            {
                GlStateManager.deleteTextures(dfbColorTextures);
                fillIntBufferZero(dfbColorTextures);
                checkGLError("del dfbTextures");
            }

            if (sfbDepthTextures != null)
            {
                GlStateManager.deleteTextures(sfbDepthTextures);
                fillIntBufferZero(sfbDepthTextures);
                checkGLError("del shadow depth");
            }

            if (sfbColorTextures != null)
            {
                GlStateManager.deleteTextures(sfbColorTextures);
                fillIntBufferZero(sfbColorTextures);
                checkGLError("del shadow color");
            }

            if (dfbDrawBuffers != null)
            {
                fillIntBufferZero(dfbDrawBuffers);
            }

            if (noiseTexture != null)
            {
                noiseTexture.deleteTexture();
                noiseTexture = null;
            }

            SMCLog.info("Uninit");
            shadowPassInterval = 0;
            shouldSkipDefaultShadow = false;
            isShaderPackInitialized = false;
            checkGLError("Shaders.uninit");
        }
    }

    public static void scheduleResize()
    {
        renderDisplayHeight = 0;
    }

    public static void scheduleResizeShadow()
    {
        needResizeShadow = true;
    }

    private static void resize()
    {
        renderDisplayWidth = mc.displayWidth;
        renderDisplayHeight = mc.displayHeight;
        renderWidth = Math.round((float)renderDisplayWidth * configRenderResMul);
        renderHeight = Math.round((float)renderDisplayHeight * configRenderResMul);
        setupFrameBuffer();
    }

    private static void resizeShadow()
    {
        needResizeShadow = false;
        shadowMapWidth = Math.round((float)spShadowMapWidth * configShadowResMul);
        shadowMapHeight = Math.round((float)spShadowMapHeight * configShadowResMul);
        setupShadowFrameBuffer();
    }

    private static void setupFrameBuffer()
    {
        if (dfb != 0)
        {
            EXTFramebufferObject.glDeleteFramebuffersEXT(dfb);
            GlStateManager.deleteTextures(dfbDepthTextures);
            GlStateManager.deleteTextures(dfbColorTextures);
        }

        dfb = EXTFramebufferObject.glGenFramebuffersEXT();
        GL11.glGenTextures(dfbDepthTextures.clear().limit(usedDepthBuffers));
        GL11.glGenTextures(dfbColorTextures.clear().limit(16));
        dfbDepthTextures.position(0);
        dfbColorTextures.position(0);
        EXTFramebufferObject.glBindFramebufferEXT(36160, dfb);
        GL20.glDrawBuffers(0);
        GL11.glReadBuffer(0);

        for (int i = 0; i < usedDepthBuffers; ++i)
        {
            GlStateManager.bindTexture(dfbDepthTextures.get(i));
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_DEPTH_TEXTURE_MODE, GL11.GL_LUMINANCE);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, renderWidth, renderHeight, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer)(ByteBuffer)null);
        }

        EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, dfbDepthTextures.get(0), 0);
        GL20.glDrawBuffers(dfbDrawBuffers);
        GL11.glReadBuffer(0);
        checkGLError("FT d");

        for (int k = 0; k < usedColorBuffers; ++k)
        {
            GlStateManager.bindTexture(dfbColorTexturesFlip.getA(k));
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, gbuffersFormat[k], renderWidth, renderHeight, 0, getPixelFormat(gbuffersFormat[k]), GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (ByteBuffer)(ByteBuffer)null);
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + k, 3553, dfbColorTexturesFlip.getA(k), 0);
            checkGLError("FT c");
        }

        for (int l = 0; l < usedColorBuffers; ++l)
        {
            GlStateManager.bindTexture(dfbColorTexturesFlip.getB(l));
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, gbuffersFormat[l], renderWidth, renderHeight, 0, getPixelFormat(gbuffersFormat[l]), GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (ByteBuffer)(ByteBuffer)null);
            checkGLError("FT ca");
        }

        int i1 = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);

        if (i1 == 36058)
        {
            printChatAndLogError("[Shaders] Error: Failed framebuffer incomplete formats");

            for (int j = 0; j < usedColorBuffers; ++j)
            {
                GlStateManager.bindTexture(dfbColorTexturesFlip.getA(j));
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, renderWidth, renderHeight, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (ByteBuffer)(ByteBuffer)null);
                EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + j, 3553, dfbColorTexturesFlip.getA(j), 0);
                checkGLError("FT c");
            }

            i1 = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);

            if (i1 == 36053)
            {
                SMCLog.info("complete");
            }
        }

        GlStateManager.bindTexture(0);

        if (i1 != 36053)
        {
            printChatAndLogError("[Shaders] Error: Failed creating framebuffer! (Status " + i1 + ")");
        }
        else
        {
            SMCLog.info("Framebuffer created.");
        }
    }

    private static int getPixelFormat(int internalFormat)
    {
        switch (internalFormat)
        {
            case 33333:
            case 33334:
            case 33339:
            case 33340:
            case 36208:
            case 36209:
            case 36226:
            case 36227:
                return 36251;

            default:
                return 32993;
        }
    }

    private static void setupShadowFrameBuffer()
    {
        if (usedShadowDepthBuffers != 0)
        {
            if (sfb != 0)
            {
                EXTFramebufferObject.glDeleteFramebuffersEXT(sfb);
                GlStateManager.deleteTextures(sfbDepthTextures);
                GlStateManager.deleteTextures(sfbColorTextures);
            }

            sfb = EXTFramebufferObject.glGenFramebuffersEXT();
            EXTFramebufferObject.glBindFramebufferEXT(36160, sfb);
            GL11.glDrawBuffer(0);
            GL11.glReadBuffer(0);
            GL11.glGenTextures(sfbDepthTextures.clear().limit(usedShadowDepthBuffers));
            GL11.glGenTextures(sfbColorTextures.clear().limit(usedShadowColorBuffers));
            sfbDepthTextures.position(0);
            sfbColorTextures.position(0);

            for (int i = 0; i < usedShadowDepthBuffers; ++i)
            {
                GlStateManager.bindTexture(sfbDepthTextures.get(i));
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 33071.0F);
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 33071.0F);
                int j = shadowFilterNearest[i] ? 9728 : 9729;
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, j);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, j);

                if (shadowHardwareFilteringEnabled[i])
                {
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_MODE, GL14.GL_COMPARE_R_TO_TEXTURE);
                }

                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, shadowMapWidth, shadowMapHeight, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT,(ByteBuffer)(ByteBuffer) null);
            }

            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, sfbDepthTextures.get(0), 0);
            checkGLError("FT sd");

            for (int k = 0; k < usedShadowColorBuffers; ++k)
            {
                GlStateManager.bindTexture(sfbColorTextures.get(k));
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 33071.0F);
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 33071.0F);
                int i1 = shadowColorFilterNearest[k] ? 9728 : 9729;
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, i1);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, i1);
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, shadowMapWidth, shadowMapHeight, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV,(ByteBuffer)(ByteBuffer) null);
                EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + k, 3553, sfbColorTextures.get(k), 0);
                checkGLError("FT sc");
            }

            GlStateManager.bindTexture(0);

            if (usedShadowColorBuffers > 0)
            {
                GL20.glDrawBuffers(sfbDrawBuffers);
            }

            int l = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);

            if (l != 36053)
            {
                printChatAndLogError("[Shaders] Error: Failed creating shadow framebuffer! (Status " + l + ")");
            }
            else
            {
                SMCLog.info("Shadow framebuffer created.");
            }
        }
    }

    public static void beginRender(Minecraft minecraft, float partialTicks, long finishTimeNano)
    {
        checkGLError("pre beginRender");
        checkWorldChanged(mc.theWorld);
        mc = minecraft;
        mc.mcProfiler.startSection("init");
        entityRenderer = mc.entityRenderer;

        if (!isShaderPackInitialized)
        {
            try
            {
                init();
            }
            catch (IllegalStateException illegalstateexception)
            {
                if (Config.normalize(illegalstateexception.getMessage()).equals("Function is not supported"))
                {
                    printChatAndLogError("[Shaders] Error: " + illegalstateexception.getMessage());
                    illegalstateexception.printStackTrace();
                    setShaderPack("OFF");
                    return;
                }
            }
        }

        if (mc.displayWidth != renderDisplayWidth || mc.displayHeight != renderDisplayHeight)
        {
            resize();
        }

        if (needResizeShadow)
        {
            resizeShadow();
        }

        worldTime = mc.theWorld.getWorldTime();
        diffWorldTime = (worldTime - lastWorldTime) % 24000L;

        if (diffWorldTime < 0L)
        {
            diffWorldTime += 24000L;
        }

        lastWorldTime = worldTime;
        moonPhase = mc.theWorld.getMoonPhase();
        ++frameCounter;

        if (frameCounter >= 720720)
        {
            frameCounter = 0;
        }

        systemTime = System.currentTimeMillis();

        if (lastSystemTime == 0L)
        {
            lastSystemTime = systemTime;
        }

        diffSystemTime = systemTime - lastSystemTime;
        lastSystemTime = systemTime;
        frameTime = (float)diffSystemTime / 1000.0F;
        frameTimeCounter += frameTime;
        frameTimeCounter %= 3600.0F;
        rainStrength = minecraft.theWorld.getRainStrength(partialTicks);
        float f = (float)diffSystemTime * 0.01F;
        float f1 = (float)Math.exp(Math.log(0.5D) * (double)f / (double)(wetness < rainStrength ? drynessHalfLife : wetnessHalfLife));
        wetness = wetness * f1 + rainStrength * (1.0F - f1);
        Entity entity = mc.getRenderViewEntity();

        if (entity != null)
        {
            isSleeping = entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPlayerSleeping();
            eyePosY = (float)entity.posY * partialTicks + (float)entity.lastTickPosY * (1.0F - partialTicks);
            eyeBrightness = entity.getBrightnessForRender(partialTicks);
            f1 = (float)diffSystemTime * 0.01F;
            float f2 = (float)Math.exp(Math.log(0.5D) * (double)f1 / (double)eyeBrightnessHalflife);
            eyeBrightnessFadeX = eyeBrightnessFadeX * f2 + (float)(eyeBrightness & 65535) * (1.0F - f2);
            eyeBrightnessFadeY = eyeBrightnessFadeY * f2 + (float)(eyeBrightness >> 16) * (1.0F - f2);
            Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(mc.theWorld, entity, partialTicks);
            Material material = block.getMaterial();

            if (material == Material.water)
            {
                isEyeInWater = 1;
            }
            else if (material == Material.lava)
            {
                isEyeInWater = 2;
            }
            else
            {
                isEyeInWater = 0;
            }

            if (mc.thePlayer != null)
            {
                nightVision = 0.0F;

                if (mc.thePlayer.isPotionActive(Potion.nightVision))
                {
                    nightVision = Config.getMinecraft().entityRenderer.getNightVisionBrightness(mc.thePlayer, partialTicks);
                }

                blindness = 0.0F;

                if (mc.thePlayer.isPotionActive(Potion.blindness))
                {
                    int i = mc.thePlayer.getActivePotionEffect(Potion.blindness).getDuration();
                    blindness = Config.limit((float)i / 20.0F, 0.0F, 1.0F);
                }
            }

            Vec3 vec3 = mc.theWorld.getSkyColor(entity, partialTicks);
            vec3 = CustomColors.getWorldSkyColor(vec3, currentWorld, entity, partialTicks);
            skyColorR = (float)vec3.xCoord;
            skyColorG = (float)vec3.yCoord;
            skyColorB = (float)vec3.zCoord;
        }

        isRenderingWorld = true;
        isCompositeRendered = false;
        isShadowPass = false;
        isHandRenderedMain = false;
        isHandRenderedOff = false;
        skipRenderHandMain = false;
        skipRenderHandOff = false;
        bindGbuffersTextures();
        previousCameraPositionX = cameraPositionX;
        previousCameraPositionY = cameraPositionY;
        previousCameraPositionZ = cameraPositionZ;
        previousProjection.position(0);
        projection.position(0);
        previousProjection.put(projection);
        previousProjection.position(0);
        projection.position(0);
        previousModelView.position(0);
        modelView.position(0);
        previousModelView.put(modelView);
        previousModelView.position(0);
        modelView.position(0);
        checkGLError("beginRender");
        ShadersRender.renderShadowMap(entityRenderer, 0, partialTicks, finishTimeNano);
        mc.mcProfiler.endSection();
        EXTFramebufferObject.glBindFramebufferEXT(36160, dfb);

        for (int j = 0; j < usedColorBuffers; ++j)
        {
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + j, 3553, dfbColorTexturesFlip.getA(j), 0);
        }

        checkGLError("end beginRender");
    }

    private static void bindGbuffersTextures()
    {
        if (usedShadowDepthBuffers >= 1)
        {
            GlStateManager.setActiveTexture(33988);
            GlStateManager.bindTexture(sfbDepthTextures.get(0));

            if (usedShadowDepthBuffers >= 2)
            {
                GlStateManager.setActiveTexture(33989);
                GlStateManager.bindTexture(sfbDepthTextures.get(1));
            }
        }

        GlStateManager.setActiveTexture(33984);

        for (int i = 0; i < usedColorBuffers; ++i)
        {
            GlStateManager.bindTexture(dfbColorTexturesFlip.getA(i));
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GlStateManager.bindTexture(dfbColorTexturesFlip.getB(i));
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        }

        GlStateManager.bindTexture(0);

        for (int j = 0; j < 4 && 4 + j < usedColorBuffers; ++j)
        {
            GlStateManager.setActiveTexture(33991 + j);
            GlStateManager.bindTexture(dfbColorTexturesFlip.getA(4 + j));
        }

        GlStateManager.setActiveTexture(33990);
        GlStateManager.bindTexture(dfbDepthTextures.get(0));

        if (usedDepthBuffers >= 2)
        {
            GlStateManager.setActiveTexture(33995);
            GlStateManager.bindTexture(dfbDepthTextures.get(1));

            if (usedDepthBuffers >= 3)
            {
                GlStateManager.setActiveTexture(33996);
                GlStateManager.bindTexture(dfbDepthTextures.get(2));
            }
        }

        for (int k = 0; k < usedShadowColorBuffers; ++k)
        {
            GlStateManager.setActiveTexture(33997 + k);
            GlStateManager.bindTexture(sfbColorTextures.get(k));
        }

        if (noiseTextureEnabled)
        {
            GlStateManager.setActiveTexture(33984 + noiseTexture.getTextureUnit());
            GlStateManager.bindTexture(noiseTexture.getTextureId());
        }

        bindCustomTextures(customTexturesGbuffers);
        GlStateManager.setActiveTexture(33984);
    }

    public static void checkWorldChanged(World world)
    {
        if (currentWorld != world)
        {
            World oldworld = currentWorld;
            currentWorld = world;
            setCameraOffset(mc.getRenderViewEntity());
            int i = getDimensionId(oldworld);
            int j = getDimensionId(world);

            if (j != i)
            {
                boolean flag = shaderPackDimensions.contains(Integer.valueOf(i));
                boolean flag1 = shaderPackDimensions.contains(Integer.valueOf(j));

                if (flag || flag1)
                {
                    uninit();
                }
            }

            Smoother.resetValues();
        }
    }

    private static int getDimensionId(World world)
    {
        return world == null ? Integer.MIN_VALUE : world.provider.getDimensionId();
    }

    public static void beginRenderPass(int pass, float partialTicks, long finishTimeNano)
    {
        if (!isShadowPass)
        {
            EXTFramebufferObject.glBindFramebufferEXT(36160, dfb);
            GL11.glViewport(0, 0, renderWidth, renderHeight);
            activeDrawBuffers = null;
            ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
            useProgram(ProgramTextured);
            checkGLError("end beginRenderPass");
        }
    }

    public static void setViewport(int vx, int vy, int vw, int vh)
    {
        GlStateManager.colorMask(true, true, true, true);

        if (isShadowPass)
        {
            GL11.glViewport(0, 0, shadowMapWidth, shadowMapHeight);
        }
        else
        {
            GL11.glViewport(0, 0, renderWidth, renderHeight);
            EXTFramebufferObject.glBindFramebufferEXT(36160, dfb);
            isRenderingDfb = true;
            GlStateManager.enableCull();
            GlStateManager.enableDepth();
            setDrawBuffers(drawBuffersNone);
            useProgram(ProgramTextured);
            checkGLError("beginRenderPass");
        }
    }

    public static void setFogMode(int value)
    {
        fogMode = value;

        if (fogEnabled)
        {
            setProgramUniform1i(uniform_fogMode, value);
        }
    }

    public static void setFogColor(float r, float g, float b)
    {
        fogColorR = r;
        fogColorG = g;
        fogColorB = b;
        setProgramUniform3f(uniform_fogColor, fogColorR, fogColorG, fogColorB);
    }

    public static void setClearColor(float red, float green, float blue, float alpha)
    {
        GlStateManager.clearColor(red, green, blue, alpha);
        clearColorR = red;
        clearColorG = green;
        clearColorB = blue;
    }

    public static void clearRenderBuffer()
    {
        if (isShadowPass)
        {
            checkGLError("shadow clear pre");
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, sfbDepthTextures.get(0), 0);
            GL11.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
            GL20.glDrawBuffers(ProgramShadow.getDrawBuffers());
            checkFramebufferStatus("shadow clear");
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            checkGLError("shadow clear");
        }
        else
        {
            checkGLError("clear pre");

            if (gbuffersClear[0])
            {
                Vector4f vector4f = gbuffersClearColor[0];

                if (vector4f != null)
                {
                    GL11.glClearColor(vector4f.getX(), vector4f.getY(), vector4f.getZ(), vector4f.getW());
                }

                if (dfbColorTexturesFlip.isChanged(0))
                {
                    EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064, 3553, dfbColorTexturesFlip.getB(0), 0);
                    GL20.glDrawBuffers(36064);
                    GL11.glClear(16384);
                    EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064, 3553, dfbColorTexturesFlip.getA(0), 0);
                }

                GL20.glDrawBuffers(36064);
                GL11.glClear(16384);
            }

            if (gbuffersClear[1])
            {
                GL11.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
                Vector4f vector4f2 = gbuffersClearColor[1];

                if (vector4f2 != null)
                {
                    GL11.glClearColor(vector4f2.getX(), vector4f2.getY(), vector4f2.getZ(), vector4f2.getW());
                }

                if (dfbColorTexturesFlip.isChanged(1))
                {
                    EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36065, 3553, dfbColorTexturesFlip.getB(1), 0);
                    GL20.glDrawBuffers(36065);
                    GL11.glClear(16384);
                    EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36065, 3553, dfbColorTexturesFlip.getA(1), 0);
                }

                GL20.glDrawBuffers(36065);
                GL11.glClear(16384);
            }

            for (int i = 2; i < usedColorBuffers; ++i)
            {
                if (gbuffersClear[i])
                {
                    GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
                    Vector4f vector4f1 = gbuffersClearColor[i];

                    if (vector4f1 != null)
                    {
                        GL11.glClearColor(vector4f1.getX(), vector4f1.getY(), vector4f1.getZ(), vector4f1.getW());
                    }

                    if (dfbColorTexturesFlip.isChanged(i))
                    {
                        EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, dfbColorTexturesFlip.getB(i), 0);
                        GL20.glDrawBuffers(36064 + i);
                        GL11.glClear(16384);
                        EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, dfbColorTexturesFlip.getA(i), 0);
                    }

                    GL20.glDrawBuffers(36064 + i);
                    GL11.glClear(16384);
                }
            }

            setDrawBuffers(dfbDrawBuffers);
            checkFramebufferStatus("clear");
            checkGLError("clear");
        }
    }

    public static void setCamera(float partialTicks)
    {
        Entity entity = mc.getRenderViewEntity();
        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
        updateCameraOffset(entity);
        cameraPositionX = d0 - (double)cameraOffsetX;
        cameraPositionY = d1;
        cameraPositionZ = d2 - (double)cameraOffsetZ;
        GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, projection.position(0));
        SMath.invertMat4FBFA(projectionInverse.position(0), projection.position(0), faProjectionInverse, faProjection);
        projection.position(0);
        projectionInverse.position(0);
        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, modelView.position(0));
        SMath.invertMat4FBFA(modelViewInverse.position(0), modelView.position(0), faModelViewInverse, faModelView);
        modelView.position(0);
        modelViewInverse.position(0);
        checkGLError("setCamera");
    }

    private static void updateCameraOffset(Entity viewEntity)
    {
        double d0 = Math.abs(cameraPositionX - previousCameraPositionX);
        double d1 = Math.abs(cameraPositionZ - previousCameraPositionZ);
        double d2 = Math.abs(cameraPositionX);
        double d3 = Math.abs(cameraPositionZ);

        if (d0 > 1000.0D || d1 > 1000.0D || d2 > 1000000.0D || d3 > 1000000.0D)
        {
            setCameraOffset(viewEntity);
        }
    }

    private static void setCameraOffset(Entity viewEntity)
    {
        if (viewEntity == null)
        {
            cameraOffsetX = 0;
            cameraOffsetZ = 0;
        }
        else
        {
            cameraOffsetX = (int)viewEntity.posX / 1000 * 1000;
            cameraOffsetZ = (int)viewEntity.posZ / 1000 * 1000;
        }
    }

    public static void setCameraShadow(float partialTicks)
    {
        Entity entity = mc.getRenderViewEntity();
        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
        updateCameraOffset(entity);
        cameraPositionX = d0 - (double)cameraOffsetX;
        cameraPositionY = d1;
        cameraPositionZ = d2 - (double)cameraOffsetZ;
        GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, projection.position(0));
        SMath.invertMat4FBFA(projectionInverse.position(0), projection.position(0), faProjectionInverse, faProjection);
        projection.position(0);
        projectionInverse.position(0);
        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, modelView.position(0));
        SMath.invertMat4FBFA(modelViewInverse.position(0), modelView.position(0), faModelViewInverse, faModelView);
        modelView.position(0);
        modelViewInverse.position(0);
        GL11.glViewport(0, 0, shadowMapWidth, shadowMapHeight);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();

        if (shadowMapIsOrtho)
        {
            GL11.glOrtho(-shadowMapHalfPlane, shadowMapHalfPlane, -shadowMapHalfPlane, shadowMapHalfPlane, 0.05000000074505806D, 256.0D);
        }
        else
        {
            GLU.gluPerspective(shadowMapFOV, (float)shadowMapWidth / (float)shadowMapHeight, 0.05F, 256.0F);
        }

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -100.0F);
        GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
        celestialAngle = mc.theWorld.getCelestialAngle(partialTicks);
        sunAngle = celestialAngle < 0.75F ? celestialAngle + 0.25F : celestialAngle - 0.75F;
        float f = celestialAngle * -360.0F;
        float f1 = shadowAngleInterval > 0.0F ? f % shadowAngleInterval - shadowAngleInterval * 0.5F : 0.0F;

        if ((double)sunAngle <= 0.5D)
        {
            GL11.glRotatef(f - f1, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(sunPathRotation, 1.0F, 0.0F, 0.0F);
            shadowAngle = sunAngle;
        }
        else
        {
            GL11.glRotatef(f + 180.0F - f1, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(sunPathRotation, 1.0F, 0.0F, 0.0F);
            shadowAngle = sunAngle - 0.5F;
        }

        if (shadowMapIsOrtho)
        {
            float f2 = shadowIntervalSize;
            float f3 = f2 / 2.0F;
            GL11.glTranslatef((float)d0 % f2 - f3, (float)d1 % f2 - f3, (float)d2 % f2 - f3);
        }

        float f9 = sunAngle * ((float)Math.PI * 2F);
        float f10 = (float)Math.cos(f9);
        float f4 = (float)Math.sin(f9);
        float f5 = sunPathRotation * ((float)Math.PI * 2F);
        float f6 = f10;
        float f7 = f4 * (float)Math.cos(f5);
        float f8 = f4 * (float)Math.sin(f5);

        if ((double)sunAngle > 0.5D)
        {
            f6 = -f10;
            f7 = -f7;
            f8 = -f8;
        }

        shadowLightPositionVector[0] = f6;
        shadowLightPositionVector[1] = f7;
        shadowLightPositionVector[2] = f8;
        shadowLightPositionVector[3] = 0.0F;
        GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, shadowProjection.position(0));
        SMath.invertMat4FBFA(shadowProjectionInverse.position(0), shadowProjection.position(0), faShadowProjectionInverse, faShadowProjection);
        shadowProjection.position(0);
        shadowProjectionInverse.position(0);
        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, shadowModelView.position(0));
        SMath.invertMat4FBFA(shadowModelViewInverse.position(0), shadowModelView.position(0), faShadowModelViewInverse, faShadowModelView);
        shadowModelView.position(0);
        shadowModelViewInverse.position(0);
        setProgramUniformMatrix4ARB(uniform_gbufferProjection, false, projection);
        setProgramUniformMatrix4ARB(uniform_gbufferProjectionInverse, false, projectionInverse);
        setProgramUniformMatrix4ARB(uniform_gbufferPreviousProjection, false, previousProjection);
        setProgramUniformMatrix4ARB(uniform_gbufferModelView, false, modelView);
        setProgramUniformMatrix4ARB(uniform_gbufferModelViewInverse, false, modelViewInverse);
        setProgramUniformMatrix4ARB(uniform_gbufferPreviousModelView, false, previousModelView);
        setProgramUniformMatrix4ARB(uniform_shadowProjection, false, shadowProjection);
        setProgramUniformMatrix4ARB(uniform_shadowProjectionInverse, false, shadowProjectionInverse);
        setProgramUniformMatrix4ARB(uniform_shadowModelView, false, shadowModelView);
        setProgramUniformMatrix4ARB(uniform_shadowModelViewInverse, false, shadowModelViewInverse);
        mc.gameSettings.thirdPersonView = 1;
        checkGLError("setCamera");
    }

    public static void preCelestialRotate()
    {
        GL11.glRotatef(sunPathRotation, 0.0F, 0.0F, 1.0F);
        checkGLError("preCelestialRotate");
    }

    public static void postCelestialRotate()
    {
        FloatBuffer floatbuffer = tempMatrixDirectBuffer;
        floatbuffer.clear();
        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, floatbuffer);
        floatbuffer.get(tempMat, 0, 16);
        SMath.multiplyMat4xVec4(sunPosition, tempMat, sunPosModelView);
        SMath.multiplyMat4xVec4(moonPosition, tempMat, moonPosModelView);
        System.arraycopy(shadowAngle == sunAngle ? sunPosition : moonPosition, 0, shadowLightPosition, 0, 3);
        setProgramUniform3f(uniform_sunPosition, sunPosition[0], sunPosition[1], sunPosition[2]);
        setProgramUniform3f(uniform_moonPosition, moonPosition[0], moonPosition[1], moonPosition[2]);
        setProgramUniform3f(uniform_shadowLightPosition, shadowLightPosition[0], shadowLightPosition[1], shadowLightPosition[2]);

        if (customUniforms != null)
        {
            customUniforms.update();
        }

        checkGLError("postCelestialRotate");
    }

    public static void setUpPosition()
    {
        FloatBuffer floatbuffer = tempMatrixDirectBuffer;
        floatbuffer.clear();
        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, floatbuffer);
        floatbuffer.get(tempMat, 0, 16);
        SMath.multiplyMat4xVec4(upPosition, tempMat, upPosModelView);
        setProgramUniform3f(uniform_upPosition, upPosition[0], upPosition[1], upPosition[2]);

        if (customUniforms != null)
        {
            customUniforms.update();
        }
    }

    public static void genCompositeMipmap()
    {
        if (hasGlGenMipmap)
        {
            for (int i = 0; i < usedColorBuffers; ++i)
            {
                if ((activeCompositeMipmapSetting & 1 << i) != 0)
                {
                    GlStateManager.setActiveTexture(33984 + colorTextureImageUnit[i]);
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
                    GL30.glGenerateMipmap(3553);
                }
            }

            GlStateManager.setActiveTexture(33984);
        }
    }

    public static void drawComposite()
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        drawCompositeQuad();
        int i = activeProgram.getCountInstances();

        if (i > 1)
        {
            for (int j = 1; j < i; ++j)
            {
                uniform_instanceId.setValue(j);
                drawCompositeQuad();
            }

            uniform_instanceId.setValue(0);
        }
    }

    private static void drawCompositeQuad()
    {
        if (!canRenderQuads())
        {
            GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
            GL11.glTexCoord2f(0.0F, 0.0F);
            GL11.glVertex3f(0.0F, 0.0F, 0.0F);
            GL11.glTexCoord2f(1.0F, 0.0F);
            GL11.glVertex3f(1.0F, 0.0F, 0.0F);
            GL11.glTexCoord2f(0.0F, 1.0F);
            GL11.glVertex3f(0.0F, 1.0F, 0.0F);
            GL11.glTexCoord2f(1.0F, 1.0F);
            GL11.glVertex3f(1.0F, 1.0F, 0.0F);
            GL11.glEnd();
        }
        else
        {
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(0.0F, 0.0F);
            GL11.glVertex3f(0.0F, 0.0F, 0.0F);
            GL11.glTexCoord2f(1.0F, 0.0F);
            GL11.glVertex3f(1.0F, 0.0F, 0.0F);
            GL11.glTexCoord2f(1.0F, 1.0F);
            GL11.glVertex3f(1.0F, 1.0F, 0.0F);
            GL11.glTexCoord2f(0.0F, 1.0F);
            GL11.glVertex3f(0.0F, 1.0F, 0.0F);
            GL11.glEnd();
        }
    }

    public static void renderDeferred()
    {
        if (!isShadowPass)
        {
            boolean flag = checkBufferFlip(ProgramDeferredPre);

            if (hasDeferredPrograms)
            {
                checkGLError("pre-render Deferred");
                renderComposites(ProgramsDeferred, false);
                flag = true;
            }

            if (flag)
            {
                bindGbuffersTextures();

                for (int i = 0; i < usedColorBuffers; ++i)
                {
                    EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, dfbColorTexturesFlip.getA(i), 0);
                }

                if (ProgramWater.getDrawBuffers() != null)
                {
                    setDrawBuffers(ProgramWater.getDrawBuffers());
                }
                else
                {
                    setDrawBuffers(dfbDrawBuffers);
                }

                GlStateManager.setActiveTexture(33984);
                mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            }
        }
    }

    public static void renderCompositeFinal()
    {
        if (!isShadowPass)
        {
            checkBufferFlip(ProgramCompositePre);
            checkGLError("pre-render CompositeFinal");
            renderComposites(ProgramsComposite, true);
        }
    }

    private static boolean checkBufferFlip(Program program)
    {
        boolean flag = false;
        Boolean[] aboolean = program.getBuffersFlip();

        for (int i = 0; i < usedColorBuffers; ++i)
        {
            if (Config.isTrue(aboolean[i]))
            {
                dfbColorTexturesFlip.flip(i);
                flag = true;
            }
        }

        return flag;
    }

    private static void renderComposites(Program[] ps, boolean renderFinal)
    {
        if (!isShadowPass)
        {
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0D, 1.0D, 0.0D, 1.0D, 0.0D, 1.0D);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableTexture2D();
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.enableDepth();
            GlStateManager.depthFunc(519);
            GlStateManager.depthMask(false);
            GlStateManager.disableLighting();

            if (usedShadowDepthBuffers >= 1)
            {
                GlStateManager.setActiveTexture(33988);
                GlStateManager.bindTexture(sfbDepthTextures.get(0));

                if (usedShadowDepthBuffers >= 2)
                {
                    GlStateManager.setActiveTexture(33989);
                    GlStateManager.bindTexture(sfbDepthTextures.get(1));
                }
            }

            for (int i = 0; i < usedColorBuffers; ++i)
            {
                GlStateManager.setActiveTexture(33984 + colorTextureImageUnit[i]);
                GlStateManager.bindTexture(dfbColorTexturesFlip.getA(i));
            }

            GlStateManager.setActiveTexture(33990);
            GlStateManager.bindTexture(dfbDepthTextures.get(0));

            if (usedDepthBuffers >= 2)
            {
                GlStateManager.setActiveTexture(33995);
                GlStateManager.bindTexture(dfbDepthTextures.get(1));

                if (usedDepthBuffers >= 3)
                {
                    GlStateManager.setActiveTexture(33996);
                    GlStateManager.bindTexture(dfbDepthTextures.get(2));
                }
            }

            for (int k = 0; k < usedShadowColorBuffers; ++k)
            {
                GlStateManager.setActiveTexture(33997 + k);
                GlStateManager.bindTexture(sfbColorTextures.get(k));
            }

            if (noiseTextureEnabled)
            {
                GlStateManager.setActiveTexture(33984 + noiseTexture.getTextureUnit());
                GlStateManager.bindTexture(noiseTexture.getTextureId());
            }

            if (renderFinal)
            {
                bindCustomTextures(customTexturesComposite);
            }
            else
            {
                bindCustomTextures(customTexturesDeferred);
            }

            GlStateManager.setActiveTexture(33984);

            for (int l = 0; l < usedColorBuffers; ++l)
            {
                EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + l, 3553, dfbColorTexturesFlip.getB(l), 0);
            }

            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, dfbDepthTextures.get(0), 0);
            GL20.glDrawBuffers(dfbDrawBuffers);
            checkGLError("pre-composite");

            for (int i1 = 0; i1 < ps.length; ++i1)
            {
                Program program = ps[i1];

                if (program.getId() != 0)
                {
                    useProgram(program);
                    checkGLError(program.getName());

                    if (activeCompositeMipmapSetting != 0)
                    {
                        genCompositeMipmap();
                    }

                    preDrawComposite();
                    drawComposite();
                    postDrawComposite();

                    for (int j = 0; j < usedColorBuffers; ++j)
                    {
                        if (program.getToggleColorTextures()[j])
                        {
                            dfbColorTexturesFlip.flip(j);
                            GlStateManager.setActiveTexture(33984 + colorTextureImageUnit[j]);
                            GlStateManager.bindTexture(dfbColorTexturesFlip.getA(j));
                            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + j, 3553, dfbColorTexturesFlip.getB(j), 0);
                        }
                    }

                    GlStateManager.setActiveTexture(33984);
                }
            }

            checkGLError("composite");

            if (renderFinal)
            {
                renderFinal();
                isCompositeRendered = true;
            }

            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.depthFunc(515);
            GlStateManager.depthMask(true);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPopMatrix();
            useProgram(ProgramNone);
        }
    }

    private static void preDrawComposite()
    {
        RenderScale renderscale = activeProgram.getRenderScale();

        if (renderscale != null)
        {
            int i = (int)((float)renderWidth * renderscale.getOffsetX());
            int j = (int)((float)renderHeight * renderscale.getOffsetY());
            int k = (int)((float)renderWidth * renderscale.getScale());
            int l = (int)((float)renderHeight * renderscale.getScale());
            GL11.glViewport(i, j, k, l);
        }
    }

    private static void postDrawComposite()
    {
        RenderScale renderscale = activeProgram.getRenderScale();

        if (renderscale != null)
        {
            GL11.glViewport(0, 0, renderWidth, renderHeight);
        }
    }

    private static void renderFinal()
    {
        isRenderingDfb = false;
        mc.getFramebuffer().bindFramebuffer(true);
        OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, 3553, mc.getFramebuffer().framebufferTexture, 0);
        GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);

        if (EntityRenderer.anaglyphEnable)
        {
            boolean flag = EntityRenderer.anaglyphField != 0;
            GlStateManager.colorMask(flag, !flag, !flag, true);
        }

        GlStateManager.depthMask(true);
        GL11.glClearColor(clearColorR, clearColorG, clearColorB, 1.0F);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(519);
        GlStateManager.depthMask(false);
        checkGLError("pre-final");
        useProgram(ProgramFinal);
        checkGLError("final");

        if (activeCompositeMipmapSetting != 0)
        {
            genCompositeMipmap();
        }

        drawComposite();
        checkGLError("renderCompositeFinal");
    }

    public static void endRender()
    {
        if (isShadowPass)
        {
            checkGLError("shadow endRender");
        }
        else
        {
            if (!isCompositeRendered)
            {
                renderCompositeFinal();
            }

            isRenderingWorld = false;
            GlStateManager.colorMask(true, true, true, true);
            useProgram(ProgramNone);
            RenderHelper.disableStandardItemLighting();
            checkGLError("endRender end");
        }
    }

    public static void beginSky()
    {
        isRenderingSky = true;
        fogEnabled = true;
        setDrawBuffers(dfbDrawBuffers);
        useProgram(ProgramSkyTextured);
        pushEntity(-2, 0);
    }

    public static void setSkyColor(Vec3 v3color)
    {
        skyColorR = (float)v3color.xCoord;
        skyColorG = (float)v3color.yCoord;
        skyColorB = (float)v3color.zCoord;
        setProgramUniform3f(uniform_skyColor, skyColorR, skyColorG, skyColorB);
    }

    public static void drawHorizon()
    {
        WorldRenderer worldrenderer = Tessellator.getInstance().getWorldRenderer();
        float f = (float)(mc.gameSettings.renderDistanceChunks * 16);
        double d0 = (double)f * 0.9238D;
        double d1 = (double)f * 0.3826D;
        double d2 = -d1;
        double d3 = -d0;
        double d4 = 16.0D;
        double d5 = -cameraPositionY;
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(d2, d5, d3).endVertex();
        worldrenderer.pos(d2, d4, d3).endVertex();
        worldrenderer.pos(d3, d4, d2).endVertex();
        worldrenderer.pos(d3, d5, d2).endVertex();
        worldrenderer.pos(d3, d5, d2).endVertex();
        worldrenderer.pos(d3, d4, d2).endVertex();
        worldrenderer.pos(d3, d4, d1).endVertex();
        worldrenderer.pos(d3, d5, d1).endVertex();
        worldrenderer.pos(d3, d5, d1).endVertex();
        worldrenderer.pos(d3, d4, d1).endVertex();
        worldrenderer.pos(d2, d4, d0).endVertex();
        worldrenderer.pos(d2, d5, d0).endVertex();
        worldrenderer.pos(d2, d5, d0).endVertex();
        worldrenderer.pos(d2, d4, d0).endVertex();
        worldrenderer.pos(d1, d4, d0).endVertex();
        worldrenderer.pos(d1, d5, d0).endVertex();
        worldrenderer.pos(d1, d5, d0).endVertex();
        worldrenderer.pos(d1, d4, d0).endVertex();
        worldrenderer.pos(d0, d4, d1).endVertex();
        worldrenderer.pos(d0, d5, d1).endVertex();
        worldrenderer.pos(d0, d5, d1).endVertex();
        worldrenderer.pos(d0, d4, d1).endVertex();
        worldrenderer.pos(d0, d4, d2).endVertex();
        worldrenderer.pos(d0, d5, d2).endVertex();
        worldrenderer.pos(d0, d5, d2).endVertex();
        worldrenderer.pos(d0, d4, d2).endVertex();
        worldrenderer.pos(d1, d4, d3).endVertex();
        worldrenderer.pos(d1, d5, d3).endVertex();
        worldrenderer.pos(d1, d5, d3).endVertex();
        worldrenderer.pos(d1, d4, d3).endVertex();
        worldrenderer.pos(d2, d4, d3).endVertex();
        worldrenderer.pos(d2, d5, d3).endVertex();
        worldrenderer.pos(d3, d5, d3).endVertex();
        worldrenderer.pos(d3, d5, d0).endVertex();
        worldrenderer.pos(d0, d5, d0).endVertex();
        worldrenderer.pos(d0, d5, d3).endVertex();
        Tessellator.getInstance().draw();
    }

    public static void preSkyList()
    {
        setUpPosition();
        GL11.glColor3f(fogColorR, fogColorG, fogColorB);
        drawHorizon();
        GL11.glColor3f(skyColorR, skyColorG, skyColorB);
    }

    public static void endSky()
    {
        isRenderingSky = false;
        setDrawBuffers(dfbDrawBuffers);
        useProgram(lightmapEnabled ? ProgramTexturedLit : ProgramTextured);
        popEntity();
    }

    public static void beginUpdateChunks()
    {
        checkGLError("beginUpdateChunks1");
        checkFramebufferStatus("beginUpdateChunks1");

        if (!isShadowPass)
        {
            useProgram(ProgramTerrain);
        }

        checkGLError("beginUpdateChunks2");
        checkFramebufferStatus("beginUpdateChunks2");
    }

    public static void endUpdateChunks()
    {
        checkGLError("endUpdateChunks1");
        checkFramebufferStatus("endUpdateChunks1");

        if (!isShadowPass)
        {
            useProgram(ProgramTerrain);
        }

        checkGLError("endUpdateChunks2");
        checkFramebufferStatus("endUpdateChunks2");
    }

    public static boolean shouldRenderClouds(GameSettings gs)
    {
        if (!shaderPackLoaded)
        {
            return true;
        }
        else
        {
            checkGLError("shouldRenderClouds");
            return isShadowPass ? configCloudShadow : gs.clouds > 0;
        }
    }

    public static void beginClouds()
    {
        fogEnabled = true;
        pushEntity(-3, 0);
        useProgram(ProgramClouds);
    }

    public static void endClouds()
    {
        disableFog();
        popEntity();
        useProgram(lightmapEnabled ? ProgramTexturedLit : ProgramTextured);
    }

    public static void beginEntities()
    {
        if (isRenderingWorld)
        {
            useProgram(ProgramEntities);
        }
    }

    public static void nextEntity(Entity entity)
    {
        if (isRenderingWorld)
        {
            useProgram(ProgramEntities);
            setEntityId(entity);
        }
    }

    public static void setEntityId(Entity entity)
    {
        if (uniform_entityId.isDefined())
        {
            int i = EntityUtils.getEntityIdByClass(entity);
            int j = EntityAliases.getEntityAliasId(i);

            if (j >= 0)
            {
                i = j;
            }

            uniform_entityId.setValue(i);
        }
    }

    public static void beginSpiderEyes()
    {
        if (isRenderingWorld && ProgramSpiderEyes.getId() != ProgramNone.getId())
        {
            useProgram(ProgramSpiderEyes);
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc(516, 0.0F);
            GlStateManager.blendFunc(770, 771);
        }
    }

    public static void endSpiderEyes()
    {
        if (isRenderingWorld && ProgramSpiderEyes.getId() != ProgramNone.getId())
        {
            useProgram(ProgramEntities);
            GlStateManager.disableAlpha();
        }
    }

    public static void endEntities()
    {
        if (isRenderingWorld)
        {
            setEntityId(null);
            useProgram(lightmapEnabled ? ProgramTexturedLit : ProgramTextured);
        }
    }

    public static void beginEntitiesGlowing()
    {
        if (isRenderingWorld)
        {
            isEntitiesGlowing = true;
        }
    }

    public static void endEntitiesGlowing()
    {
        if (isRenderingWorld)
        {
            isEntitiesGlowing = false;
        }
    }

    public static void setEntityColor(float r, float g, float b, float a)
    {
        if (isRenderingWorld && !isShadowPass)
        {
            uniform_entityColor.setValue(r, g, b, a);
        }
    }

    public static void beginLivingDamage()
    {
        if (isRenderingWorld)
        {
            ShadersTex.bindTexture(defaultTexture);

            if (!isShadowPass)
            {
                setDrawBuffers(drawBuffersColorAtt0);
            }
        }
    }

    public static void endLivingDamage()
    {
        if (isRenderingWorld && !isShadowPass)
        {
            setDrawBuffers(ProgramEntities.getDrawBuffers());
        }
    }

    public static void beginBlockEntities()
    {
        if (isRenderingWorld)
        {
            checkGLError("beginBlockEntities");
            useProgram(ProgramBlock);
        }
    }

    public static void nextBlockEntity(TileEntity tileEntity)
    {
        if (isRenderingWorld)
        {
            checkGLError("nextBlockEntity");
            useProgram(ProgramBlock);
            setBlockEntityId(tileEntity);
        }
    }

    public static void setBlockEntityId(TileEntity tileEntity)
    {
        if (uniform_blockEntityId.isDefined())
        {
            int i = getBlockEntityId(tileEntity);
            uniform_blockEntityId.setValue(i);
        }
    }

    private static int getBlockEntityId(TileEntity tileEntity)
    {
        if (tileEntity == null)
        {
            return -1;
        }
        else
        {
            Block block = tileEntity.getBlockType();

            if (block == null)
            {
                return 0;
            }
            else
            {
                int i = Block.getIdFromBlock(block);
                int j = tileEntity.getBlockMetadata();
                int k = BlockAliases.getBlockAliasId(i, j);

                if (k >= 0)
                {
                    i = k;
                }

                return i;
            }
        }
    }

    public static void endBlockEntities()
    {
        if (isRenderingWorld)
        {
            checkGLError("endBlockEntities");
            setBlockEntityId(null);
            useProgram(lightmapEnabled ? ProgramTexturedLit : ProgramTextured);
            ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
        }
    }

    public static void beginLitParticles()
    {
        useProgram(ProgramTexturedLit);
    }

    public static void beginParticles()
    {
        useProgram(ProgramTextured);
    }

    public static void endParticles()
    {
        useProgram(ProgramTexturedLit);
    }

    public static void readCenterDepth()
    {
        if (!isShadowPass && centerDepthSmoothEnabled)
        {
            tempDirectFloatBuffer.clear();
            GL11.glReadPixels(renderWidth / 2, renderHeight / 2, 1, 1, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, tempDirectFloatBuffer);
            centerDepth = tempDirectFloatBuffer.get(0);
            float f = (float)diffSystemTime * 0.01F;
            float f1 = (float)Math.exp(Math.log(0.5D) * (double)f / (double)centerDepthSmoothHalflife);
            centerDepthSmooth = centerDepthSmooth * f1 + centerDepth * (1.0F - f1);
        }
    }

    public static void beginWeather()
    {
        if (!isShadowPass)
        {
            if (usedDepthBuffers >= 3)
            {
                GlStateManager.setActiveTexture(33996);
                GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, renderWidth, renderHeight);
                GlStateManager.setActiveTexture(33984);
            }

            GlStateManager.enableDepth();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.enableAlpha();
            useProgram(ProgramWeather);
        }
    }

    public static void endWeather()
    {
        GlStateManager.disableBlend();
        useProgram(ProgramTexturedLit);
    }

    public static void preWater()
    {
        if (usedDepthBuffers >= 2)
        {
            GlStateManager.setActiveTexture(33995);
            checkGLError("pre copy depth");
            GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, renderWidth, renderHeight);
            checkGLError("copy depth");
            GlStateManager.setActiveTexture(33984);
        }

        ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
    }

    public static void beginWater()
    {
        if (isRenderingWorld)
        {
            if (!isShadowPass)
            {
                renderDeferred();
                useProgram(ProgramWater);
                GlStateManager.enableBlend();
                GlStateManager.depthMask(true);
            }
            else
            {
                GlStateManager.depthMask(true);
            }
        }
    }

    public static void endWater()
    {
        if (isRenderingWorld)
        {
            if (isShadowPass)
            {
            }

            useProgram(lightmapEnabled ? ProgramTexturedLit : ProgramTextured);
        }
    }

    public static void applyHandDepth()
    {
        if ((double)configHandDepthMul != 1.0D)
        {
            GL11.glScaled(1.0D, 1.0D, configHandDepthMul);
        }
    }

    public static void beginHand(boolean translucent)
    {
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        if (translucent)
        {
            useProgram(ProgramHandWater);
        }
        else
        {
            useProgram(ProgramHand);
        }

        checkGLError("beginHand");
        checkFramebufferStatus("beginHand");
    }

    public static void endHand()
    {
        checkGLError("pre endHand");
        checkFramebufferStatus("pre endHand");
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
        GlStateManager.blendFunc(770, 771);
        checkGLError("endHand");
    }

    public static void beginFPOverlay()
    {
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
    }

    public static void endFPOverlay()
    {
    }

    public static void glEnableWrapper(int cap)
    {
        GL11.glEnable(cap);

        if (cap == 3553)
        {
            enableTexture2D();
        }
        else if (cap == 2912)
        {
            enableFog();
        }
    }

    public static void glDisableWrapper(int cap)
    {
        GL11.glDisable(cap);

        if (cap == 3553)
        {
            disableTexture2D();
        }
        else if (cap == 2912)
        {
            disableFog();
        }
    }

    public static void sglEnableT2D(int cap)
    {
        GL11.glEnable(cap);
        enableTexture2D();
    }

    public static void sglDisableT2D(int cap)
    {
        GL11.glDisable(cap);
        disableTexture2D();
    }

    public static void sglEnableFog(int cap)
    {
        GL11.glEnable(cap);
        enableFog();
    }

    public static void sglDisableFog(int cap)
    {
        GL11.glDisable(cap);
        disableFog();
    }

    public static void enableTexture2D()
    {
        if (isRenderingSky)
        {
            useProgram(ProgramSkyTextured);
        }
        else if (activeProgram == ProgramBasic)
        {
            useProgram(lightmapEnabled ? ProgramTexturedLit : ProgramTextured);
        }
    }

    public static void disableTexture2D()
    {
        if (isRenderingSky)
        {
            useProgram(ProgramSkyBasic);
        }
        else if (activeProgram == ProgramTextured || activeProgram == ProgramTexturedLit)
        {
            useProgram(ProgramBasic);
        }
    }

    public static void pushProgram()
    {
        programStack.push(activeProgram);
    }

    public static void popProgram()
    {
        Program program = programStack.pop();
        useProgram(program);
    }

    public static void beginLeash()
    {
        pushProgram();
        useProgram(ProgramBasic);
    }

    public static void endLeash()
    {
        popProgram();
    }

    public static void enableFog()
    {
        fogEnabled = true;
        setProgramUniform1i(uniform_fogMode, fogMode);
        setProgramUniform1f(uniform_fogDensity, fogDensity);
    }

    public static void disableFog()
    {
        fogEnabled = false;
        setProgramUniform1i(uniform_fogMode, 0);
    }

    public static void setFogDensity(float value)
    {
        fogDensity = value;

        if (fogEnabled)
        {
            setProgramUniform1f(uniform_fogDensity, value);
        }
    }

    public static void sglFogi(int pname, int param)
    {
        GL11.glFogi(pname, param);

        if (pname == 2917)
        {
            fogMode = param;

            if (fogEnabled)
            {
                setProgramUniform1i(uniform_fogMode, fogMode);
            }
        }
    }

    public static void enableLightmap()
    {
        lightmapEnabled = true;

        if (activeProgram == ProgramTextured)
        {
            useProgram(ProgramTexturedLit);
        }
    }

    public static void disableLightmap()
    {
        lightmapEnabled = false;

        if (activeProgram == ProgramTexturedLit)
        {
            useProgram(ProgramTextured);
        }
    }

    public static int getEntityData()
    {
        return entityData[entityDataIndex * 2];
    }

    public static int getEntityData2()
    {
        return entityData[entityDataIndex * 2 + 1];
    }

    public static int setEntityData1(int data1)
    {
        entityData[entityDataIndex * 2] = entityData[entityDataIndex * 2] & 65535 | data1 << 16;
        return data1;
    }

    public static int setEntityData2(int data2)
    {
        entityData[entityDataIndex * 2 + 1] = entityData[entityDataIndex * 2 + 1] & -65536 | data2 & 65535;
        return data2;
    }

    public static void pushEntity(int data0, int data1)
    {
        ++entityDataIndex;
        entityData[entityDataIndex * 2] = data0 & 65535 | data1 << 16;
        entityData[entityDataIndex * 2 + 1] = 0;
    }

    public static void pushEntity(int data0)
    {
        ++entityDataIndex;
        entityData[entityDataIndex * 2] = data0 & 65535;
        entityData[entityDataIndex * 2 + 1] = 0;
    }

    public static void pushEntity(Block block)
    {
        ++entityDataIndex;
        int i = block.getRenderType();
        entityData[entityDataIndex * 2] = Block.blockRegistry.getIDForObject(block) & 65535 | i << 16;
        entityData[entityDataIndex * 2 + 1] = 0;
    }

    public static void popEntity()
    {
        entityData[entityDataIndex * 2] = 0;
        entityData[entityDataIndex * 2 + 1] = 0;
        --entityDataIndex;
    }

    public static void mcProfilerEndSection()
    {
        mc.mcProfiler.endSection();
    }

    public static String getShaderPackName()
    {
        return shaderPack == null ? null : (shaderPack instanceof ShaderPackNone ? null : shaderPack.getName());
    }

    public static InputStream getShaderPackResourceStream(String path)
    {
        return shaderPack == null ? null : shaderPack.getResourceAsStream(path);
    }

    public static void nextAntialiasingLevel(boolean forward)
    {
        if (forward)
        {
            configAntialiasingLevel += 2;

            if (configAntialiasingLevel > 4)
            {
                configAntialiasingLevel = 0;
            }
        }
        else
        {
            configAntialiasingLevel -= 2;

            if (configAntialiasingLevel < 0)
            {
                configAntialiasingLevel = 4;
            }
        }

        configAntialiasingLevel = configAntialiasingLevel / 2 * 2;
        configAntialiasingLevel = Config.limit(configAntialiasingLevel, 0, 4);
    }

    public static void checkShadersModInstalled()
    {
        try
        {
            Class oclass = Class.forName("shadersmod.transform.SMCClassTransformer");
        }
        catch (Throwable var1)
        {
            return;
        }

        throw new RuntimeException("Shaders Mod detected. Please remove it, OptiFine has built-in support for shaders.");
    }

    public static void resourcesReloaded()
    {
        loadShaderPackResources();

        if (shaderPackLoaded)
        {
            BlockAliases.resourcesReloaded();
            ItemAliases.resourcesReloaded();
            EntityAliases.resourcesReloaded();
        }
    }

    private static void loadShaderPackResources()
    {
        shaderPackResources = new HashMap();

        if (shaderPackLoaded)
        {
            List<String> list = new ArrayList();
            String s = "/shaders/lang/";
            String s1 = "en_US";
            String s2 = ".lang";
            list.add(s + s1 + s2);

            if (!Config.getGameSettings().language.equals(s1))
            {
                list.add(s + Config.getGameSettings().language + s2);
            }

            try
            {
                for (String s3 : list)
                {
                    InputStream inputstream = shaderPack.getResourceAsStream(s3);

                    if (inputstream != null)
                    {
                        Properties properties = new PropertiesOrdered();
                        Lang.loadLocaleData(inputstream, properties);
                        inputstream.close();

                        for (Object o: properties.keySet())
                        {
                            String s4 = (String) o;
                            String s5 = properties.getProperty(s4);
                            shaderPackResources.put(s4, s5);
                        }
                    }
                }
            }
            catch (IOException ioexception)
            {
                ioexception.printStackTrace();
            }
        }
    }

    public static String translate(String key, String def)
    {
        String s = shaderPackResources.get(key);
        return s == null ? def : s;
    }

    public static boolean isProgramPath(String path)
    {
        if (path == null)
        {
            return false;
        }
        else if (path.length() <= 0)
        {
            return false;
        }
        else
        {
            int i = path.lastIndexOf("/");

            if (i >= 0)
            {
                path = path.substring(i + 1);
            }

            Program program = getProgram(path);
            return program != null;
        }
    }

    public static Program getProgram(String name)
    {
        return programs.getProgram(name);
    }

    public static void setItemToRenderMain(ItemStack itemToRenderMain)
    {
        itemToRenderMainTranslucent = isTranslucentBlock(itemToRenderMain);
    }

    public static void setItemToRenderOff(ItemStack itemToRenderOff)
    {
        itemToRenderOffTranslucent = isTranslucentBlock(itemToRenderOff);
    }

    public static boolean isItemToRenderMainTranslucent()
    {
        return itemToRenderMainTranslucent;
    }

    public static boolean isItemToRenderOffTranslucent()
    {
        return itemToRenderOffTranslucent;
    }

    public static boolean isBothHandsRendered()
    {
        return isHandRenderedMain && isHandRenderedOff;
    }

    private static boolean isTranslucentBlock(ItemStack stack)
    {
        if (stack == null)
        {
            return false;
        }
        else
        {
            Item item = stack.getItem();

            if (item == null)
            {
                return false;
            }
            else if (!(item instanceof ItemBlock itemblock))
            {
                return false;
            }
            else
            {
                Block block = itemblock.getBlock();

                if (block == null)
                {
                    return false;
                }
                else
                {
                    EnumWorldBlockLayer enumworldblocklayer = block.getBlockLayer();
                    return enumworldblocklayer == EnumWorldBlockLayer.TRANSLUCENT;
                }
            }
        }
    }

    public static boolean isSkipRenderHand()
    {
        return skipRenderHandMain;
    }

    public static boolean isRenderBothHands()
    {
        return !skipRenderHandMain && !skipRenderHandOff;
    }

    public static void setSkipRenderHands(boolean skipMain, boolean skipOff)
    {
        skipRenderHandMain = skipMain;
        skipRenderHandOff = skipOff;
    }

    public static void setHandsRendered(boolean handMain, boolean handOff)
    {
        isHandRenderedMain = handMain;
        isHandRenderedOff = handOff;
    }

    public static boolean isHandRenderedMain()
    {
        return isHandRenderedMain;
    }

    public static boolean isHandRenderedOff()
    {
        return isHandRenderedOff;
    }

    public static float getShadowRenderDistance()
    {
        return shadowDistanceRenderMul < 0.0F ? -1.0F : shadowMapHalfPlane * shadowDistanceRenderMul;
    }

    public static void setRenderingFirstPersonHand(boolean flag)
    {
        isRenderingFirstPersonHand = flag;
    }

    public static boolean isRenderingFirstPersonHand()
    {
        return isRenderingFirstPersonHand;
    }

    public static void beginBeacon()
    {
        if (isRenderingWorld)
        {
            useProgram(ProgramBeaconBeam);
        }
    }

    public static void endBeacon()
    {
        if (isRenderingWorld)
        {
            useProgram(ProgramBlock);
        }
    }

    public static World getCurrentWorld()
    {
        return currentWorld;
    }

    public static BlockPos getCameraPosition()
    {
        return new BlockPos(cameraPositionX, cameraPositionY, cameraPositionZ);
    }

    public static boolean isCustomUniforms()
    {
        return customUniforms != null;
    }

    public static boolean canRenderQuads()
    {
        return !hasGeometryShaders || capabilities.GL_NV_geometry_shader4;
    }

    static
    {
        shaderPacksDir = new File(Minecraft.getMinecraft().mcDataDir, "shaderpacks");
        configFile = new File(Minecraft.getMinecraft().mcDataDir, "optionsshaders.txt");
    }
}
