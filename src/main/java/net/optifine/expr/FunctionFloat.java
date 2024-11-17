package net.optifine.expr;

import net.optifine.shaders.uniform.Smoother;

public class FunctionFloat implements IExpressionFloat
{
    private final FunctionType type;
    private final IExpression[] arguments;
    private int smoothId = -1;

    public FunctionFloat(FunctionType type, IExpression[] arguments)
    {
        this.type = type;
        this.arguments = arguments;
    }

    public float eval()
    {
        IExpression[] aiexpression = this.arguments;

        switch (this.type)
        {
            case SMOOTH:
                IExpression iexpression = aiexpression[0];

                if (!(iexpression instanceof ConstantFloat))
                {
                    float f = evalFloat(aiexpression, 0);
                    float f1 = aiexpression.length > 1 ? evalFloat(aiexpression, 1) : 1.0F;
                    float f2 = aiexpression.length > 2 ? evalFloat(aiexpression, 2) : f1;

                    if (this.smoothId < 0)
                    {
                        this.smoothId = Smoother.getNextId();
                    }

                    float f3 = Smoother.getSmoothValue(this.smoothId, f, f1, f2);
                    return f3;
                }

            default:
                return this.type.evalFloat(this.arguments);
        }
    }

    private static float evalFloat(IExpression[] exprs, int index)
    {
        IExpressionFloat iexpressionfloat = (IExpressionFloat)exprs[index];
        float f = iexpressionfloat.eval();
        return f;
    }

    public ExpressionType getExpressionType()
    {
        return ExpressionType.FLOAT;
    }

    public String toString()
    {
        return this.type + "()";
    }
}
