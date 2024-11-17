package net.optifine.shaders.config;

public class ShaderMacro
{
    private final String name;
    private final String value;

    public ShaderMacro(String name, String value)
    {
        this.name = name;
        this.value = value;
    }

    public String getName()
    {
        return this.name;
    }

    public String getValue()
    {
        return this.value;
    }

    public String getSourceLine()
    {
        return "#define " + this.name + " " + this.value;
    }

    public String toString()
    {
        return this.getSourceLine();
    }
}
