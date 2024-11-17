package net.optifine.expr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParametersVariable implements IParameters
{
    private final ExpressionType[] first;
    private final ExpressionType[] repeat;
    private final ExpressionType[] last;
    private int maxCount;
    private static final ExpressionType[] EMPTY = new ExpressionType[0];

    public ParametersVariable()
    {
        this(null, null, null);
    }

    public ParametersVariable(ExpressionType[] first, ExpressionType[] repeat, ExpressionType[] last)
    {
        this(first, repeat, last, Integer.MAX_VALUE);
    }

    public ParametersVariable(ExpressionType[] first, ExpressionType[] repeat, ExpressionType[] last, int maxCount)
    {
        this.maxCount = Integer.MAX_VALUE;
        this.first = normalize(first);
        this.repeat = normalize(repeat);
        this.last = normalize(last);
        this.maxCount = maxCount;
    }

    private static ExpressionType[] normalize(ExpressionType[] exprs)
    {
        return exprs == null ? EMPTY : exprs;
    }

    public ExpressionType[] getFirst()
    {
        return this.first;
    }

    public ExpressionType[] getRepeat()
    {
        return this.repeat;
    }

    public ExpressionType[] getLast()
    {
        return this.last;
    }

    public int getCountRepeat()
    {
        return this.first == null ? 0 : this.first.length;
    }

    public ExpressionType[] getParameterTypes(IExpression[] arguments)
    {
        int i = this.first.length + this.last.length;
        int j = arguments.length - i;
        int k = 0;

        for (int l = 0; l + this.repeat.length <= j && i + l + this.repeat.length <= this.maxCount; l += this.repeat.length)
        {
            ++k;
        }

        List<ExpressionType> list = new ArrayList();
        list.addAll(Arrays.asList(this.first));

        for (int i1 = 0; i1 < k; ++i1)
        {
            list.addAll(Arrays.asList(this.repeat));
        }

        list.addAll(Arrays.asList(this.last));
        ExpressionType[] aexpressiontype = list.toArray(new ExpressionType[list.size()]);
        return aexpressiontype;
    }

    public ParametersVariable first(ExpressionType... first)
    {
        return new ParametersVariable(first, this.repeat, this.last);
    }

    public ParametersVariable repeat(ExpressionType... repeat)
    {
        return new ParametersVariable(this.first, repeat, this.last);
    }

    public ParametersVariable last(ExpressionType... last)
    {
        return new ParametersVariable(this.first, this.repeat, last);
    }

    public ParametersVariable maxCount(int maxCount)
    {
        return new ParametersVariable(this.first, this.repeat, this.last, maxCount);
    }
}
