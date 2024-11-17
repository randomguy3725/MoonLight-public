package net.optifine.shaders.uniform;

import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.shaders.Shaders;

public enum ShaderParameterFloat
{
    BIOME("biome"),
    TEMPERATURE("temperature"),
    RAINFALL("rainfall"),
    HELD_ITEM_ID(Shaders.uniform_heldItemId),
    HELD_BLOCK_LIGHT_VALUE(Shaders.uniform_heldBlockLightValue),
    HELD_ITEM_ID2(Shaders.uniform_heldItemId2),
    HELD_BLOCK_LIGHT_VALUE2(Shaders.uniform_heldBlockLightValue2),
    WORLD_TIME(Shaders.uniform_worldTime),
    WORLD_DAY(Shaders.uniform_worldDay),
    MOON_PHASE(Shaders.uniform_moonPhase),
    FRAME_COUNTER(Shaders.uniform_frameCounter),
    FRAME_TIME(Shaders.uniform_frameTime),
    FRAME_TIME_COUNTER(Shaders.uniform_frameTimeCounter),
    SUN_ANGLE(Shaders.uniform_sunAngle),
    SHADOW_ANGLE(Shaders.uniform_shadowAngle),
    RAIN_STRENGTH(Shaders.uniform_rainStrength),
    ASPECT_RATIO(Shaders.uniform_aspectRatio),
    VIEW_WIDTH(Shaders.uniform_viewWidth),
    VIEW_HEIGHT(Shaders.uniform_viewHeight),
    NEAR(Shaders.uniform_near),
    FAR(Shaders.uniform_far),
    WETNESS(Shaders.uniform_wetness),
    EYE_ALTITUDE(Shaders.uniform_eyeAltitude),
    EYE_BRIGHTNESS(Shaders.uniform_eyeBrightness, new String[]{"x", "y"}),
    TERRAIN_TEXTURE_SIZE(Shaders.uniform_terrainTextureSize, new String[]{"x", "y"}),
    TERRRAIN_ICON_SIZE(Shaders.uniform_terrainIconSize),
    IS_EYE_IN_WATER(Shaders.uniform_isEyeInWater),
    NIGHT_VISION(Shaders.uniform_nightVision),
    BLINDNESS(Shaders.uniform_blindness),
    SCREEN_BRIGHTNESS(Shaders.uniform_screenBrightness),
    HIDE_GUI(Shaders.uniform_hideGUI),
    CENTER_DEPT_SMOOTH(Shaders.uniform_centerDepthSmooth),
    ATLAS_SIZE(Shaders.uniform_atlasSize, new String[]{"x", "y"}),
    CAMERA_POSITION(Shaders.uniform_cameraPosition, new String[]{"x", "y", "z"}),
    PREVIOUS_CAMERA_POSITION(Shaders.uniform_previousCameraPosition, new String[]{"x", "y", "z"}),
    SUN_POSITION(Shaders.uniform_sunPosition, new String[]{"x", "y", "z"}),
    MOON_POSITION(Shaders.uniform_moonPosition, new String[]{"x", "y", "z"}),
    SHADOW_LIGHT_POSITION(Shaders.uniform_shadowLightPosition, new String[]{"x", "y", "z"}),
    UP_POSITION(Shaders.uniform_upPosition, new String[]{"x", "y", "z"}),
    SKY_COLOR(Shaders.uniform_skyColor, new String[]{"r", "g", "b"}),
    GBUFFER_PROJECTION(Shaders.uniform_gbufferProjection, new String[]{"0", "1", "2", "3"}, new String[]{"0", "1", "2", "3"}),
    GBUFFER_PROJECTION_INVERSE(Shaders.uniform_gbufferProjectionInverse, new String[]{"0", "1", "2", "3"}, new String[]{"0", "1", "2", "3"}),
    GBUFFER_PREVIOUS_PROJECTION(Shaders.uniform_gbufferPreviousProjection, new String[]{"0", "1", "2", "3"}, new String[]{"0", "1", "2", "3"}),
    GBUFFER_MODEL_VIEW(Shaders.uniform_gbufferModelView, new String[]{"0", "1", "2", "3"}, new String[]{"0", "1", "2", "3"}),
    GBUFFER_MODEL_VIEW_INVERSE(Shaders.uniform_gbufferModelViewInverse, new String[]{"0", "1", "2", "3"}, new String[]{"0", "1", "2", "3"}),
    GBUFFER_PREVIOUS_MODEL_VIEW(Shaders.uniform_gbufferPreviousModelView, new String[]{"0", "1", "2", "3"}, new String[]{"0", "1", "2", "3"}),
    SHADOW_PROJECTION(Shaders.uniform_shadowProjection, new String[]{"0", "1", "2", "3"}, new String[]{"0", "1", "2", "3"}),
    SHADOW_PROJECTION_INVERSE(Shaders.uniform_shadowProjectionInverse, new String[]{"0", "1", "2", "3"}, new String[]{"0", "1", "2", "3"}),
    SHADOW_MODEL_VIEW(Shaders.uniform_shadowModelView, new String[]{"0", "1", "2", "3"}, new String[]{"0", "1", "2", "3"}),
    SHADOW_MODEL_VIEW_INVERSE(Shaders.uniform_shadowModelViewInverse, new String[]{"0", "1", "2", "3"}, new String[]{"0", "1", "2", "3"});

    private final String name;
    private ShaderUniformBase uniform;
    private String[] indexNames1;
    private String[] indexNames2;

    ShaderParameterFloat(String name)
    {
        this.name = name;
    }

    ShaderParameterFloat(ShaderUniformBase uniform)
    {
        this.name = uniform.getName();
        this.uniform = uniform;

        if (!instanceOf(uniform, ShaderUniform1f.class, ShaderUniform1i.class))
        {
            throw new IllegalArgumentException("Invalid uniform type for enum: " + this + ", uniform: " + uniform.getClass().getName());
        }
    }

    ShaderParameterFloat(ShaderUniformBase uniform, String[] indexNames1)
    {
        this.name = uniform.getName();
        this.uniform = uniform;
        this.indexNames1 = indexNames1;

        if (!instanceOf(uniform, ShaderUniform2i.class, ShaderUniform2f.class, ShaderUniform3f.class, ShaderUniform4f.class))
        {
            throw new IllegalArgumentException("Invalid uniform type for enum: " + this + ", uniform: " + uniform.getClass().getName());
        }
    }

    ShaderParameterFloat(ShaderUniformBase uniform, String[] indexNames1, String[] indexNames2)
    {
        this.name = uniform.getName();
        this.uniform = uniform;
        this.indexNames1 = indexNames1;
        this.indexNames2 = indexNames2;

        if (!instanceOf(uniform, ShaderUniformM4.class))
        {
            throw new IllegalArgumentException("Invalid uniform type for enum: " + this + ", uniform: " + uniform.getClass().getName());
        }
    }

    public String getName()
    {
        return this.name;
    }

    public ShaderUniformBase getUniform()
    {
        return this.uniform;
    }

    public String[] getIndexNames1()
    {
        return this.indexNames1;
    }

    public String[] getIndexNames2()
    {
        return this.indexNames2;
    }

    public float eval(int index1, int index2)
    {
        if (this.indexNames1 == null || index1 >= 0 && index1 <= this.indexNames1.length)
        {
            if (this.indexNames2 == null || index2 >= 0 && index2 <= this.indexNames2.length)
            {
                switch (this)
                {
                    case BIOME:
                        BlockPos blockpos2 = Shaders.getCameraPosition();
                        BiomeGenBase biomegenbase2 = Shaders.getCurrentWorld().getBiomeGenForCoords(blockpos2);
                        return (float)biomegenbase2.biomeID;

                    case TEMPERATURE:
                        BlockPos blockpos1 = Shaders.getCameraPosition();
                        BiomeGenBase biomegenbase1 = Shaders.getCurrentWorld().getBiomeGenForCoords(blockpos1);
                        return biomegenbase1 != null ? biomegenbase1.getFloatTemperature(blockpos1) : 0.0F;

                    case RAINFALL:
                        BlockPos pos = Shaders.getCameraPosition();
                        BiomeGenBase biome = Shaders.getCurrentWorld().getBiomeGenForCoords(pos);
                        return biome != null ? biome.getFloatRainfall() : 0.0F;

                    default:
                        if (this.uniform instanceof ShaderUniform1f)
                        {
                            return ((ShaderUniform1f)this.uniform).getValue();
                        }
                        else if (this.uniform instanceof ShaderUniform1i)
                        {
                            return (float)((ShaderUniform1i)this.uniform).getValue();
                        }
                        else if (this.uniform instanceof ShaderUniform2i)
                        {
                            return (float)((ShaderUniform2i)this.uniform).getValue()[index1];
                        }
                        else if (this.uniform instanceof ShaderUniform2f)
                        {
                            return ((ShaderUniform2f)this.uniform).getValue()[index1];
                        }
                        else if (this.uniform instanceof ShaderUniform3f)
                        {
                            return ((ShaderUniform3f)this.uniform).getValue()[index1];
                        }
                        else if (this.uniform instanceof ShaderUniform4f)
                        {
                            return ((ShaderUniform4f)this.uniform).getValue()[index1];
                        }
                        else if (this.uniform instanceof ShaderUniformM4)
                        {
                            return ((ShaderUniformM4)this.uniform).getValue(index1, index2);
                        }
                        else
                        {
                            throw new IllegalArgumentException("Unknown uniform type: " + this);
                        }
                }
            }
            else
            {
                Config.warn("Invalid index2, parameter: " + this + ", index: " + index2);
                return 0.0F;
            }
        }
        else
        {
            Config.warn("Invalid index1, parameter: " + this + ", index: " + index1);
            return 0.0F;
        }
    }

    private static boolean instanceOf(Object obj, Class... classes)
    {
        if (obj == null)
        {
            return false;
        }
        else
        {
            Class oclass = obj.getClass();

            for (int i = 0; i < classes.length; ++i)
            {
                Class oclass1 = classes[i];

                if (oclass1.isAssignableFrom(oclass))
                {
                    return true;
                }
            }

            return false;
        }
    }
}
