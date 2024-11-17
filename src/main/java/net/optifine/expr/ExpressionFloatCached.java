package net.optifine.expr;

public class ExpressionFloatCached implements IExpressionFloat, IExpressionCached
{
    private final IExpressionFloat expression;
    private boolean cached;
    private float value;

    public ExpressionFloatCached(IExpressionFloat expression)
    {
        this.expression = expression;
    }

    public float eval()
    {
        if (!this.cached)
        {
            this.value = this.expression.eval();
            this.cached = true;
        }

        return this.value;
    }

    public void reset()
    {
        this.cached = false;
    }

    public ExpressionType getExpressionType()
    {
        return ExpressionType.FLOAT;
    }

    public String toString()
    {
        return "cached(" + this.expression + ")";
    }
}
