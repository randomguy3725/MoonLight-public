package net.optifine.shaders.config;

import net.optifine.expr.ExpressionType;
import net.optifine.expr.IExpressionBool;

public class ExpressionShaderOptionSwitch implements IExpressionBool
{
    private final ShaderOptionSwitch shaderOption;

    public ExpressionShaderOptionSwitch(ShaderOptionSwitch shaderOption)
    {
        this.shaderOption = shaderOption;
    }

    public boolean eval()
    {
        return ShaderOptionSwitch.isTrue(this.shaderOption.getValue());
    }

    public ExpressionType getExpressionType()
    {
        return ExpressionType.BOOL;
    }

    public String toString()
    {
        return "" + this.shaderOption;
    }
}
