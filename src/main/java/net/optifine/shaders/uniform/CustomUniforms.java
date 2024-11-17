package net.optifine.shaders.uniform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.optifine.expr.IExpression;
import net.optifine.expr.IExpressionCached;

public class CustomUniforms
{
    private final CustomUniform[] uniforms;
    private final IExpressionCached[] expressionsCached;

    public CustomUniforms(CustomUniform[] uniforms, Map<String, IExpression> mapExpressions)
    {
        this.uniforms = uniforms;
        List<IExpressionCached> list = new ArrayList();

        for (String s : mapExpressions.keySet())
        {
            IExpression iexpression = mapExpressions.get(s);

            if (iexpression instanceof IExpressionCached iexpressioncached)
            {
                list.add(iexpressioncached);
            }
        }

        this.expressionsCached = list.toArray(new IExpressionCached[list.size()]);
    }

    public void setProgram(int program)
    {
        for (int i = 0; i < this.uniforms.length; ++i)
        {
            CustomUniform customuniform = this.uniforms[i];
            customuniform.setProgram(program);
        }
    }

    public void update()
    {
        this.resetCache();

        for (int i = 0; i < this.uniforms.length; ++i)
        {
            CustomUniform customuniform = this.uniforms[i];
            customuniform.update();
        }
    }

    private void resetCache()
    {
        for (int i = 0; i < this.expressionsCached.length; ++i)
        {
            IExpressionCached iexpressioncached = this.expressionsCached[i];
            iexpressioncached.reset();
        }
    }

    public void reset()
    {
        for (int i = 0; i < this.uniforms.length; ++i)
        {
            CustomUniform customuniform = this.uniforms[i];
            customuniform.reset();
        }
    }
}
