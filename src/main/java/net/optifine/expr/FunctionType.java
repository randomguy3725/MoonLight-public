package net.optifine.expr;

import java.util.HashMap;
import java.util.Map;

import net.optifine.shaders.uniform.Smoother;
import net.optifine.util.MathUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Config;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public enum FunctionType
{
    PLUS(10, ExpressionType.FLOAT, "+", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT}),
    MINUS(10, ExpressionType.FLOAT, "-", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT}),
    MUL(11, ExpressionType.FLOAT, "*", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT}),
    DIV(11, ExpressionType.FLOAT, "/", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT}),
    MOD(11, ExpressionType.FLOAT, "%", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT}),
    NEG(12, ExpressionType.FLOAT, "neg", new ExpressionType[]{ExpressionType.FLOAT}),
    PI(ExpressionType.FLOAT, "pi", new ExpressionType[0]),
    SIN(ExpressionType.FLOAT, "sin", new ExpressionType[]{ExpressionType.FLOAT}),
    COS(ExpressionType.FLOAT, "cos", new ExpressionType[]{ExpressionType.FLOAT}),
    ASIN(ExpressionType.FLOAT, "asin", new ExpressionType[]{ExpressionType.FLOAT}),
    ACOS(ExpressionType.FLOAT, "acos", new ExpressionType[]{ExpressionType.FLOAT}),
    TAN(ExpressionType.FLOAT, "tan", new ExpressionType[]{ExpressionType.FLOAT}),
    ATAN(ExpressionType.FLOAT, "atan", new ExpressionType[]{ExpressionType.FLOAT}),
    ATAN2(ExpressionType.FLOAT, "atan2", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT}),
    TORAD(ExpressionType.FLOAT, "torad", new ExpressionType[]{ExpressionType.FLOAT}),
    TODEG(ExpressionType.FLOAT, "todeg", new ExpressionType[]{ExpressionType.FLOAT}),
    MIN(ExpressionType.FLOAT, "min", (new ParametersVariable()).first(ExpressionType.FLOAT).repeat(ExpressionType.FLOAT)),
    MAX(ExpressionType.FLOAT, "max", (new ParametersVariable()).first(ExpressionType.FLOAT).repeat(ExpressionType.FLOAT)),
    CLAMP(ExpressionType.FLOAT, "clamp", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT, ExpressionType.FLOAT}),
    ABS(ExpressionType.FLOAT, "abs", new ExpressionType[]{ExpressionType.FLOAT}),
    FLOOR(ExpressionType.FLOAT, "floor", new ExpressionType[]{ExpressionType.FLOAT}),
    CEIL(ExpressionType.FLOAT, "ceil", new ExpressionType[]{ExpressionType.FLOAT}),
    EXP(ExpressionType.FLOAT, "exp", new ExpressionType[]{ExpressionType.FLOAT}),
    FRAC(ExpressionType.FLOAT, "frac", new ExpressionType[]{ExpressionType.FLOAT}),
    LOG(ExpressionType.FLOAT, "log", new ExpressionType[]{ExpressionType.FLOAT}),
    POW(ExpressionType.FLOAT, "pow", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT}),
    RANDOM(ExpressionType.FLOAT, "random", new ExpressionType[0]),
    ROUND(ExpressionType.FLOAT, "round", new ExpressionType[]{ExpressionType.FLOAT}),
    SIGNUM(ExpressionType.FLOAT, "signum", new ExpressionType[]{ExpressionType.FLOAT}),
    SQRT(ExpressionType.FLOAT, "sqrt", new ExpressionType[]{ExpressionType.FLOAT}),
    FMOD(ExpressionType.FLOAT, "fmod", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT}),
    TIME(ExpressionType.FLOAT, "time", new ExpressionType[0]),
    IF(ExpressionType.FLOAT, "if", (new ParametersVariable()).first(ExpressionType.BOOL, ExpressionType.FLOAT).repeat(ExpressionType.BOOL, ExpressionType.FLOAT).last(ExpressionType.FLOAT)),
    NOT(12, ExpressionType.BOOL, "!", new ExpressionType[]{ExpressionType.BOOL}),
    AND(3, ExpressionType.BOOL, "&&", new ExpressionType[]{ExpressionType.BOOL, ExpressionType.BOOL}),
    OR(2, ExpressionType.BOOL, "||", new ExpressionType[]{ExpressionType.BOOL, ExpressionType.BOOL}),
    GREATER(8, ExpressionType.BOOL, ">", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT}),
    GREATER_OR_EQUAL(8, ExpressionType.BOOL, ">=", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT}),
    SMALLER(8, ExpressionType.BOOL, "<", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT}),
    SMALLER_OR_EQUAL(8, ExpressionType.BOOL, "<=", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT}),
    EQUAL(7, ExpressionType.BOOL, "==", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT}),
    NOT_EQUAL(7, ExpressionType.BOOL, "!=", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT}),
    BETWEEN(7, ExpressionType.BOOL, "between", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT, ExpressionType.FLOAT}),
    EQUALS(7, ExpressionType.BOOL, "equals", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT, ExpressionType.FLOAT}),
    IN(ExpressionType.BOOL, "in", (new ParametersVariable()).first(ExpressionType.FLOAT).repeat(ExpressionType.FLOAT).last(ExpressionType.FLOAT)),
    SMOOTH(ExpressionType.FLOAT, "smooth", (new ParametersVariable()).first(ExpressionType.FLOAT).repeat(ExpressionType.FLOAT).maxCount(4)),
    TRUE(ExpressionType.BOOL, "true", new ExpressionType[0]),
    FALSE(ExpressionType.BOOL, "false", new ExpressionType[0]),
    VEC2(ExpressionType.FLOAT_ARRAY, "vec2", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT}),
    VEC3(ExpressionType.FLOAT_ARRAY, "vec3", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT, ExpressionType.FLOAT}),
    VEC4(ExpressionType.FLOAT_ARRAY, "vec4", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT, ExpressionType.FLOAT, ExpressionType.FLOAT});

    private final int precedence;
    private final ExpressionType expressionType;
    private final String name;
    private final IParameters parameters;
    public static FunctionType[] VALUES = values();
    private static final Map<Integer, Float> mapSmooth = new HashMap();

    FunctionType(ExpressionType expressionType, String name, ExpressionType[] parameterTypes)
    {
        this(0, expressionType, name, parameterTypes);
    }

    FunctionType(int precedence, ExpressionType expressionType, String name, ExpressionType[] parameterTypes)
    {
        this(precedence, expressionType, name, new Parameters(parameterTypes));
    }

    FunctionType(ExpressionType expressionType, String name, IParameters parameters)
    {
        this(0, expressionType, name, parameters);
    }

    FunctionType(int precedence, ExpressionType expressionType, String name, IParameters parameters)
    {
        this.precedence = precedence;
        this.expressionType = expressionType;
        this.name = name;
        this.parameters = parameters;
    }

    public String getName()
    {
        return this.name;
    }

    public int getPrecedence()
    {
        return this.precedence;
    }

    public ExpressionType getExpressionType()
    {
        return this.expressionType;
    }

    public IParameters getParameters()
    {
        return this.parameters;
    }

    public int getParameterCount(IExpression[] arguments)
    {
        return this.parameters.getParameterTypes(arguments).length;
    }

    public ExpressionType[] getParameterTypes(IExpression[] arguments)
    {
        return this.parameters.getParameterTypes(arguments);
    }

    public float evalFloat(IExpression[] args)
    {
        switch (this)
        {
            case PLUS:
                return evalFloat(args, 0) + evalFloat(args, 1);

            case MINUS:
                return evalFloat(args, 0) - evalFloat(args, 1);

            case MUL:
                return evalFloat(args, 0) * evalFloat(args, 1);

            case DIV:
                return evalFloat(args, 0) / evalFloat(args, 1);

            case MOD:
                float f = evalFloat(args, 0);
                float f1 = evalFloat(args, 1);
                return f - f1 * (float)((int)(f / f1));

            case NEG:
                return -evalFloat(args, 0);

            case PI:
                return MathHelper.PI;

            case SIN:
                return MathHelper.sin(evalFloat(args, 0));

            case COS:
                return MathHelper.cos(evalFloat(args, 0));

            case ASIN:
                return MathUtils.asin(evalFloat(args, 0));

            case ACOS:
                return MathUtils.acos(evalFloat(args, 0));

            case TAN:
                return (float)Math.tan(evalFloat(args, 0));

            case ATAN:
                return (float)Math.atan(evalFloat(args, 0));

            case ATAN2:
                return (float)MathHelper.atan2(evalFloat(args, 0), evalFloat(args, 1));

            case TORAD:
                return MathUtils.toRad(evalFloat(args, 0));

            case TODEG:
                return MathUtils.toDeg(evalFloat(args, 0));

            case MIN:
                return this.getMin(args);

            case MAX:
                return this.getMax(args);

            case CLAMP:
                return MathHelper.clamp_float(evalFloat(args, 0), evalFloat(args, 1), evalFloat(args, 2));

            case ABS:
                return MathHelper.abs(evalFloat(args, 0));

            case EXP:
                return (float)Math.exp(evalFloat(args, 0));

            case FLOOR:
                return (float)MathHelper.floor_float(evalFloat(args, 0));

            case CEIL:
                return (float)MathHelper.ceiling_float_int(evalFloat(args, 0));

            case FRAC:
                return (float)MathHelper.func_181162_h(evalFloat(args, 0));

            case LOG:
                return (float)Math.log(evalFloat(args, 0));

            case POW:
                return (float)Math.pow(evalFloat(args, 0), evalFloat(args, 1));

            case RANDOM:
                return (float)Math.random();

            case ROUND:
                return (float)Math.round(evalFloat(args, 0));

            case SIGNUM:
                return Math.signum(evalFloat(args, 0));

            case SQRT:
                return MathHelper.sqrt_float(evalFloat(args, 0));

            case FMOD:
                float f2 = evalFloat(args, 0);
                float f3 = evalFloat(args, 1);
                return f2 - f3 * (float)MathHelper.floor_float(f2 / f3);

            case TIME:
                Minecraft minecraft = Minecraft.getMinecraft();
                World world = minecraft.theWorld;

                if (world == null)
                {
                    return 0.0F;
                }

                return (float)(world.getTotalWorldTime() % 24000L) + Config.renderPartialTicks;

            case IF:
                int i = (args.length - 1) / 2;

                for (int k = 0; k < i; ++k)
                {
                    int l = k * 2;

                    if (evalBool(args, l))
                    {
                        return evalFloat(args, l + 1);
                    }
                }

                return evalFloat(args, i * 2);

            case SMOOTH:
                int j = (int)evalFloat(args, 0);
                float f4 = evalFloat(args, 1);
                float f5 = args.length > 2 ? evalFloat(args, 2) : 1.0F;
                float f6 = args.length > 3 ? evalFloat(args, 3) : f5;
                float f7 = Smoother.getSmoothValue(j, f4, f5, f6);
                return f7;

            default:
                Config.warn("Unknown function type: " + this);
                return 0.0F;
        }
    }

    private float getMin(IExpression[] exprs)
    {
        if (exprs.length == 2)
        {
            return Math.min(evalFloat(exprs, 0), evalFloat(exprs, 1));
        }
        else
        {
            float f = evalFloat(exprs, 0);

            for (int i = 1; i < exprs.length; ++i)
            {
                float f1 = evalFloat(exprs, i);

                if (f1 < f)
                {
                    f = f1;
                }
            }

            return f;
        }
    }

    private float getMax(IExpression[] exprs)
    {
        if (exprs.length == 2)
        {
            return Math.max(evalFloat(exprs, 0), evalFloat(exprs, 1));
        }
        else
        {
            float f = evalFloat(exprs, 0);

            for (int i = 1; i < exprs.length; ++i)
            {
                float f1 = evalFloat(exprs, i);

                if (f1 > f)
                {
                    f = f1;
                }
            }

            return f;
        }
    }

    private static float evalFloat(IExpression[] exprs, int index)
    {
        IExpressionFloat iexpressionfloat = (IExpressionFloat)exprs[index];
        float f = iexpressionfloat.eval();
        return f;
    }

    public boolean evalBool(IExpression[] args)
    {
        switch (this)
        {
            case TRUE:
                return true;

            case FALSE:
                return false;

            case NOT:
                return !evalBool(args, 0);

            case AND:
                return evalBool(args, 0) && evalBool(args, 1);

            case OR:
                return evalBool(args, 0) || evalBool(args, 1);

            case GREATER:
                return evalFloat(args, 0) > evalFloat(args, 1);

            case GREATER_OR_EQUAL:
                return evalFloat(args, 0) >= evalFloat(args, 1);

            case SMALLER:
                return evalFloat(args, 0) < evalFloat(args, 1);

            case SMALLER_OR_EQUAL:
                return evalFloat(args, 0) <= evalFloat(args, 1);

            case EQUAL:
                return evalFloat(args, 0) == evalFloat(args, 1);

            case NOT_EQUAL:
                return evalFloat(args, 0) != evalFloat(args, 1);

            case BETWEEN:
                float f = evalFloat(args, 0);
                return f >= evalFloat(args, 1) && f <= evalFloat(args, 2);

            case EQUALS:
                float f1 = evalFloat(args, 0) - evalFloat(args, 1);
                float f2 = evalFloat(args, 2);
                return Math.abs(f1) <= f2;

            case IN:
                float f3 = evalFloat(args, 0);

                for (int i = 1; i < args.length; ++i)
                {
                    float f4 = evalFloat(args, i);

                    if (f3 == f4)
                    {
                        return true;
                    }
                }

                return false;

            default:
                Config.warn("Unknown function type: " + this);
                return false;
        }
    }

    private static boolean evalBool(IExpression[] exprs, int index)
    {
        IExpressionBool iexpressionbool = (IExpressionBool)exprs[index];
        boolean flag = iexpressionbool.eval();
        return flag;
    }

    public float[] evalFloatArray(IExpression[] args)
    {
        switch (this)
        {
            case VEC2:
                return new float[] {evalFloat(args, 0), evalFloat(args, 1)};
            case VEC3:
                return new float[] {evalFloat(args, 0), evalFloat(args, 1), evalFloat(args, 2)};
            case VEC4:
                return new float[] {evalFloat(args, 0), evalFloat(args, 1), evalFloat(args, 2), evalFloat(args, 3)};
            default:
                Config.warn("Unknown function type: " + this);
                return null;
        }
    }

    public static FunctionType parse(String str)
    {
        for (int i = 0; i < VALUES.length; ++i)
        {
            FunctionType functiontype = VALUES[i];

            if (functiontype.getName().equals(str))
            {
                return functiontype;
            }
        }

        return null;
    }
}
