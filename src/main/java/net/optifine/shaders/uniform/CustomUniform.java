package net.optifine.shaders.uniform;

import net.optifine.expr.IExpression;
import net.optifine.shaders.SMCLog;

public class CustomUniform
{
    private final String name;
    private final UniformType type;
    private final IExpression expression;
    private final ShaderUniformBase shaderUniform;

    public CustomUniform(String name, UniformType type, IExpression expression)
    {
        this.name = name;
        this.type = type;
        this.expression = expression;
        this.shaderUniform = type.makeShaderUniform(name);
    }

    public void setProgram(int program)
    {
        this.shaderUniform.setProgram(program);
    }

    public void update()
    {
        if (this.shaderUniform.isDefined())
        {
            try
            {
                this.type.updateUniform(this.expression, this.shaderUniform);
            }
            catch (RuntimeException runtimeexception)
            {
                SMCLog.severe("Error updating custom uniform: " + this.shaderUniform.getName());
                SMCLog.severe(runtimeexception.getClass().getName() + ": " + runtimeexception.getMessage());
                this.shaderUniform.disable();
                SMCLog.severe("Custom uniform disabled: " + this.shaderUniform.getName());
            }
        }
    }

    public void reset()
    {
        this.shaderUniform.reset();
    }

    public String getName()
    {
        return this.name;
    }

    public UniformType getType()
    {
        return this.type;
    }

    public IExpression getExpression()
    {
        return this.expression;
    }

    public ShaderUniformBase getShaderUniform()
    {
        return this.shaderUniform;
    }

    public String toString()
    {
        return this.type.name().toLowerCase() + " " + this.name;
    }
}
