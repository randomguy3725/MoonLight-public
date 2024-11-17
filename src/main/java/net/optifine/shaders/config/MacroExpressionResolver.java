package net.optifine.shaders.config;

import java.util.Map;
import net.minecraft.src.Config;
import net.optifine.expr.ConstantFloat;
import net.optifine.expr.FunctionBool;
import net.optifine.expr.FunctionType;
import net.optifine.expr.IExpression;
import net.optifine.expr.IExpressionResolver;

public class MacroExpressionResolver implements IExpressionResolver
{
    private Map<String, String> mapMacroValues = null;

    public MacroExpressionResolver(Map<String, String> mapMacroValues)
    {
        this.mapMacroValues = mapMacroValues;
    }

    public IExpression getExpression(String name)
    {
        String s = "defined_";

        if (name.startsWith(s))
        {
            String s2 = name.substring(s.length());
            return this.mapMacroValues.containsKey(s2) ? new FunctionBool(FunctionType.TRUE, null) : new FunctionBool(FunctionType.FALSE, null);
        }
        else
        {
            while (this.mapMacroValues.containsKey(name))
            {
                String s1 = this.mapMacroValues.get(name);

                if (s1 == null || s1.equals(name))
                {
                    break;
                }

                name = s1;
            }

            int i = Config.parseInt(name, Integer.MIN_VALUE);

            if (i == Integer.MIN_VALUE)
            {
                Config.warn("Unknown macro value: " + name);
                return new ConstantFloat(0.0F);
            }
            else
            {
                return new ConstantFloat((float)i);
            }
        }
    }
}
