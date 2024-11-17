package net.optifine.expr;

public class ConstantFloat implements IExpressionFloat
{
    private final float value;

    public ConstantFloat(float value)
    {
        this.value = value;
    }

    public float eval()
    {
        return this.value;
    }

    public ExpressionType getExpressionType()
    {
        return ExpressionType.FLOAT;
    }

    public String toString()
    {
        return "" + this.value;
    }
}
