package net.optifine.expr;

public class FunctionBool implements IExpressionBool
{
    private final FunctionType type;
    private final IExpression[] arguments;

    public FunctionBool(FunctionType type, IExpression[] arguments)
    {
        this.type = type;
        this.arguments = arguments;
    }

    public boolean eval()
    {
        return this.type.evalBool(this.arguments);
    }

    public ExpressionType getExpressionType()
    {
        return ExpressionType.BOOL;
    }

    public String toString()
    {
        return this.type + "()";
    }
}
