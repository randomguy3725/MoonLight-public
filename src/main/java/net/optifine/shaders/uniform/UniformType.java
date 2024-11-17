package net.optifine.shaders.uniform;

import net.optifine.expr.ExpressionType;
import net.optifine.expr.IExpression;
import net.optifine.expr.IExpressionBool;
import net.optifine.expr.IExpressionFloat;
import net.optifine.expr.IExpressionFloatArray;

public enum UniformType
{
    BOOL,
    INT,
    FLOAT,
    VEC2,
    VEC3,
    VEC4;

    public ShaderUniformBase makeShaderUniform(String name)
    {
        switch (this)
        {
            case BOOL:
                return new ShaderUniform1i(name);

            case INT:
                return new ShaderUniform1i(name);

            case FLOAT:
                return new ShaderUniform1f(name);

            case VEC2:
                return new ShaderUniform2f(name);

            case VEC3:
                return new ShaderUniform3f(name);

            case VEC4:
                return new ShaderUniform4f(name);

            default:
                throw new RuntimeException("Unknown uniform type: " + this);
        }
    }

    public void updateUniform(IExpression expression, ShaderUniformBase uniform)
    {
        switch (this)
        {
            case BOOL:
                this.updateUniformBool((IExpressionBool)expression, (ShaderUniform1i)uniform);
                return;

            case INT:
                this.updateUniformInt((IExpressionFloat)expression, (ShaderUniform1i)uniform);
                return;

            case FLOAT:
                this.updateUniformFloat((IExpressionFloat)expression, (ShaderUniform1f)uniform);
                return;

            case VEC2:
                this.updateUniformFloat2((IExpressionFloatArray)expression, (ShaderUniform2f)uniform);
                return;

            case VEC3:
                this.updateUniformFloat3((IExpressionFloatArray)expression, (ShaderUniform3f)uniform);
                return;

            case VEC4:
                this.updateUniformFloat4((IExpressionFloatArray)expression, (ShaderUniform4f)uniform);
                return;

            default:
                throw new RuntimeException("Unknown uniform type: " + this);
        }
    }

    private void updateUniformBool(IExpressionBool expression, ShaderUniform1i uniform)
    {
        boolean flag = expression.eval();
        int i = flag ? 1 : 0;
        uniform.setValue(i);
    }

    private void updateUniformInt(IExpressionFloat expression, ShaderUniform1i uniform)
    {
        int i = (int)expression.eval();
        uniform.setValue(i);
    }

    private void updateUniformFloat(IExpressionFloat expression, ShaderUniform1f uniform)
    {
        float f = expression.eval();
        uniform.setValue(f);
    }

    private void updateUniformFloat2(IExpressionFloatArray expression, ShaderUniform2f uniform)
    {
        float[] afloat = expression.eval();

        if (afloat.length != 2)
        {
            throw new RuntimeException("Value length is not 2, length: " + afloat.length);
        }
        else
        {
            uniform.setValue(afloat[0], afloat[1]);
        }
    }

    private void updateUniformFloat3(IExpressionFloatArray expression, ShaderUniform3f uniform)
    {
        float[] afloat = expression.eval();

        if (afloat.length != 3)
        {
            throw new RuntimeException("Value length is not 3, length: " + afloat.length);
        }
        else
        {
            uniform.setValue(afloat[0], afloat[1], afloat[2]);
        }
    }

    private void updateUniformFloat4(IExpressionFloatArray expression, ShaderUniform4f uniform)
    {
        float[] afloat = expression.eval();

        if (afloat.length != 4)
        {
            throw new RuntimeException("Value length is not 4, length: " + afloat.length);
        }
        else
        {
            uniform.setValue(afloat[0], afloat[1], afloat[2], afloat[3]);
        }
    }

    public boolean matchesExpressionType(ExpressionType expressionType)
    {
        switch (this)
        {
            case BOOL:
                return expressionType == ExpressionType.BOOL;

            case INT:
                return expressionType == ExpressionType.FLOAT;

            case FLOAT:
                return expressionType == ExpressionType.FLOAT;

            case VEC2:
            case VEC3:
            case VEC4:
                return expressionType == ExpressionType.FLOAT_ARRAY;

            default:
                throw new RuntimeException("Unknown uniform type: " + this);
        }
    }

    public static UniformType parse(String type)
    {
        UniformType[] auniformtype = values();

        for (int i = 0; i < auniformtype.length; ++i)
        {
            UniformType uniformtype = auniformtype[i];

            if (uniformtype.name().toLowerCase().equals(type))
            {
                return uniformtype;
            }
        }

        return null;
    }
}
