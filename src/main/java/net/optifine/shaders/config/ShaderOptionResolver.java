package net.optifine.shaders.config;

import java.util.HashMap;
import java.util.Map;
import net.optifine.expr.IExpression;
import net.optifine.expr.IExpressionResolver;

public class ShaderOptionResolver implements IExpressionResolver
{
    private final Map<String, ExpressionShaderOptionSwitch> mapOptions = new HashMap();

    public ShaderOptionResolver(ShaderOption[] options)
    {
        for (int i = 0; i < options.length; ++i)
        {
            ShaderOption shaderoption = options[i];

            if (shaderoption instanceof ShaderOptionSwitch shaderoptionswitch)
            {
                ExpressionShaderOptionSwitch expressionshaderoptionswitch = new ExpressionShaderOptionSwitch(shaderoptionswitch);
                this.mapOptions.put(shaderoption.getName(), expressionshaderoptionswitch);
            }
        }
    }

    public IExpression getExpression(String name)
    {
        ExpressionShaderOptionSwitch expressionshaderoptionswitch = this.mapOptions.get(name);
        return expressionshaderoptionswitch;
    }
}
