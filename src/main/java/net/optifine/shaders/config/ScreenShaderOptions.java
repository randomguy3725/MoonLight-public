package net.optifine.shaders.config;

public class ScreenShaderOptions
{
    private final String name;
    private final ShaderOption[] shaderOptions;
    private final int columns;

    public ScreenShaderOptions(String name, ShaderOption[] shaderOptions, int columns)
    {
        this.name = name;
        this.shaderOptions = shaderOptions;
        this.columns = columns;
    }

    public String getName()
    {
        return this.name;
    }

    public ShaderOption[] getShaderOptions()
    {
        return this.shaderOptions;
    }

    public int getColumns()
    {
        return this.columns;
    }
}
