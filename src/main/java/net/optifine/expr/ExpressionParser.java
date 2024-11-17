package net.optifine.expr;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.src.Config;

public class ExpressionParser
{
    private final IExpressionResolver expressionResolver;

    public ExpressionParser(IExpressionResolver expressionResolver)
    {
        this.expressionResolver = expressionResolver;
    }

    public IExpressionFloat parseFloat(String str) throws ParseException
    {
        IExpression iexpression = this.parse(str);

        if (!(iexpression instanceof IExpressionFloat))
        {
            throw new ParseException("Not a float expression: " + iexpression.getExpressionType());
        }
        else
        {
            return (IExpressionFloat)iexpression;
        }
    }

    public IExpressionBool parseBool(String str) throws ParseException
    {
        IExpression iexpression = this.parse(str);

        if (!(iexpression instanceof IExpressionBool))
        {
            throw new ParseException("Not a boolean expression: " + iexpression.getExpressionType());
        }
        else
        {
            return (IExpressionBool)iexpression;
        }
    }

    public IExpression parse(String str) throws ParseException
    {
        try
        {
            Token[] atoken = TokenParser.parse(str);

            if (atoken == null)
            {
                return null;
            }
            else
            {
                Deque<Token> deque = new ArrayDeque(Arrays.asList(atoken));
                return this.parseInfix(deque);
            }
        }
        catch (IOException ioexception)
        {
            throw new ParseException(ioexception.getMessage(), ioexception);
        }
    }

    private IExpression parseInfix(Deque<Token> deque) throws ParseException
    {
        if (deque.isEmpty())
        {
            return null;
        }
        else
        {
            List<IExpression> list = new LinkedList();
            List<Token> list1 = new LinkedList();
            IExpression iexpression = this.parseExpression(deque);
            checkNull(iexpression, "Missing expression");
            list.add(iexpression);

            while (true)
            {
                Token token = deque.poll();

                if (token == null)
                {
                    return this.makeInfix(list, list1);
                }

                if (token.getType() != TokenType.OPERATOR)
                {
                    throw new ParseException("Invalid operator: " + token);
                }

                IExpression iexpression1 = this.parseExpression(deque);
                checkNull(iexpression1, "Missing expression");
                list1.add(token);
                list.add(iexpression1);
            }
        }
    }

    private IExpression makeInfix(List<IExpression> listExpr, List<Token> listOper) throws ParseException
    {
        List<FunctionType> list = new LinkedList();

        for (Token token : listOper)
        {
            FunctionType functiontype = FunctionType.parse(token.getText());
            checkNull(functiontype, "Invalid operator: " + token);
            list.add(functiontype);
        }

        return this.makeInfixFunc(listExpr, list);
    }

    private IExpression makeInfixFunc(List<IExpression> listExpr, List<FunctionType> listFunc) throws ParseException
    {
        if (listExpr.size() != listFunc.size() + 1)
        {
            throw new ParseException("Invalid infix expression, expressions: " + listExpr.size() + ", operators: " + listFunc.size());
        }
        else if (listExpr.size() == 1)
        {
            return listExpr.get(0);
        }
        else
        {
            int i = Integer.MAX_VALUE;
            int j = Integer.MIN_VALUE;

            for (FunctionType functiontype : listFunc)
            {
                i = Math.min(functiontype.getPrecedence(), i);
                j = Math.max(functiontype.getPrecedence(), j);
            }

            if (j >= i && j - i <= 10)
            {
                for (int k = j; k >= i; --k)
                {
                    this.mergeOperators(listExpr, listFunc, k);
                }

                if (listExpr.size() == 1 && listFunc.size() == 0)
                {
                    return listExpr.get(0);
                }
                else
                {
                    throw new ParseException("Error merging operators, expressions: " + listExpr.size() + ", operators: " + listFunc.size());
                }
            }
            else
            {
                throw new ParseException("Invalid infix precedence, min: " + i + ", max: " + j);
            }
        }
    }

    private void mergeOperators(List<IExpression> listExpr, List<FunctionType> listFuncs, int precedence) throws ParseException
    {
        for (int i = 0; i < listFuncs.size(); ++i)
        {
            FunctionType functiontype = listFuncs.get(i);

            if (functiontype.getPrecedence() == precedence)
            {
                listFuncs.remove(i);
                IExpression iexpression = listExpr.remove(i);
                IExpression iexpression1 = listExpr.remove(i);
                IExpression iexpression2 = makeFunction(functiontype, new IExpression[] {iexpression, iexpression1});
                listExpr.add(i, iexpression2);
                --i;
            }
        }
    }

    private IExpression parseExpression(Deque<Token> deque) throws ParseException
    {
        Token token = deque.poll();
        checkNull(token, "Missing expression");

        switch (token.getType())
        {
            case NUMBER:
                return makeConstantFloat(token);

            case IDENTIFIER:
                FunctionType functiontype = this.getFunctionType(token, deque);

                if (functiontype != null)
                {
                    return this.makeFunction(functiontype, deque);
                }

                return this.makeVariable(token);

            case BRACKET_OPEN:
                return this.makeBracketed(token, deque);

            case OPERATOR:
                FunctionType functiontype1 = FunctionType.parse(token.getText());
                checkNull(functiontype1, "Invalid operator: " + token);

                if (functiontype1 == FunctionType.PLUS)
                {
                    return this.parseExpression(deque);
                }
                else if (functiontype1 == FunctionType.MINUS)
                {
                    IExpression iexpression1 = this.parseExpression(deque);
                    return makeFunction(FunctionType.NEG, new IExpression[] {iexpression1});
                }
                else if (functiontype1 == FunctionType.NOT)
                {
                    IExpression iexpression = this.parseExpression(deque);
                    return makeFunction(FunctionType.NOT, new IExpression[] {iexpression});
                }

            default:
                throw new ParseException("Invalid expression: " + token);
        }
    }

    private static IExpression makeConstantFloat(Token token) throws ParseException
    {
        float f = Config.parseFloat(token.getText(), Float.NaN);

        if (f == Float.NaN)
        {
            throw new ParseException("Invalid float value: " + token);
        }
        else
        {
            return new ConstantFloat(f);
        }
    }

    private FunctionType getFunctionType(Token token, Deque<Token> deque) throws ParseException
    {
        Token token1 = deque.peek();

        if (token1 != null && token1.getType() == TokenType.BRACKET_OPEN)
        {
            FunctionType enumfunctiontype1 = FunctionType.parse(token1.getText());
            checkNull(enumfunctiontype1, "Unknown function: " + token1);
            return enumfunctiontype1;
        }
        else
        {
            FunctionType functiontype = FunctionType.parse(token.getText());

            if (functiontype == null)
            {
                return null;
            }
            else if (functiontype.getParameterCount(new IExpression[0]) > 0)
            {
                throw new ParseException("Missing arguments: " + functiontype);
            }
            else
            {
                return functiontype;
            }
        }
    }

    private IExpression makeFunction(FunctionType type, Deque<Token> deque) throws ParseException
    {
        if (type.getParameterCount(new IExpression[0]) == 0)
        {
            Token token = deque.peek();

            if (token == null || token.getType() != TokenType.BRACKET_OPEN)
            {
                return makeFunction(type, new IExpression[0]);
            }
        }

        Token token1 = deque.poll();
        Deque<Token> deque2 = getGroup(deque, TokenType.BRACKET_CLOSE, true);
        IExpression[] aiexpression = this.parseExpressions(deque2);
        return makeFunction(type, aiexpression);
    }

    private IExpression[] parseExpressions(Deque<Token> deque) throws ParseException
    {
        List<IExpression> list = new ArrayList();

        while (true)
        {
            Deque<Token> deque2 = getGroup(deque, TokenType.COMMA, false);
            IExpression iexpression = this.parseInfix(deque2);

            if (iexpression == null)
            {
                IExpression[] aiexpression = list.toArray(new IExpression[list.size()]);
                return aiexpression;
            }

            list.add(iexpression);
        }
    }

    private static IExpression makeFunction(FunctionType type, IExpression[] args) throws ParseException
    {
        ExpressionType[] aexpressiontype = type.getParameterTypes(args);

        if (args.length != aexpressiontype.length)
        {
            throw new ParseException("Invalid number of arguments, function: \"" + type.getName() + "\", count arguments: " + args.length + ", should be: " + aexpressiontype.length);
        }
        else
        {
            for (int i = 0; i < args.length; ++i)
            {
                IExpression iexpression = args[i];
                ExpressionType expressiontype = iexpression.getExpressionType();
                ExpressionType expressiontype1 = aexpressiontype[i];

                if (expressiontype != expressiontype1)
                {
                    throw new ParseException("Invalid argument type, function: \"" + type.getName() + "\", index: " + i + ", type: " + expressiontype + ", should be: " + expressiontype1);
                }
            }

            if (type.getExpressionType() == ExpressionType.FLOAT)
            {
                return new FunctionFloat(type, args);
            }
            else if (type.getExpressionType() == ExpressionType.BOOL)
            {
                return new FunctionBool(type, args);
            }
            else if (type.getExpressionType() == ExpressionType.FLOAT_ARRAY)
            {
                return new FunctionFloatArray(type, args);
            }
            else
            {
                throw new ParseException("Unknown function type: " + type.getExpressionType() + ", function: " + type.getName());
            }
        }
    }

    private IExpression makeVariable(Token token) throws ParseException
    {
        if (this.expressionResolver == null)
        {
            throw new ParseException("Model variable not found: " + token);
        }
        else
        {
            IExpression iexpression = this.expressionResolver.getExpression(token.getText());

            if (iexpression == null)
            {
                throw new ParseException("Model variable not found: " + token);
            }
            else
            {
                return iexpression;
            }
        }
    }

    private IExpression makeBracketed(Token token, Deque<Token> deque) throws ParseException
    {
        Deque<Token> deque2 = getGroup(deque, TokenType.BRACKET_CLOSE, true);
        return this.parseInfix(deque2);
    }

    private static Deque<Token> getGroup(Deque<Token> deque, TokenType tokenTypeEnd, boolean tokenEndRequired) throws ParseException
    {
        Deque<Token> deque3 = new ArrayDeque();
        int i = 0;
        Iterator iterator = deque.iterator();

        while (iterator.hasNext())
        {
            Token token = (Token)iterator.next();
            iterator.remove();

            if (i == 0 && token.getType() == tokenTypeEnd)
            {
                return deque3;
            }

            deque3.add(token);

            if (token.getType() == TokenType.BRACKET_OPEN)
            {
                ++i;
            }

            if (token.getType() == TokenType.BRACKET_CLOSE)
            {
                --i;
            }
        }

        if (tokenEndRequired)
        {
            throw new ParseException("Missing end token: " + tokenTypeEnd);
        }
        else
        {
            return deque3;
        }
    }

    private static void checkNull(Object obj, String message) throws ParseException
    {
        if (obj == null)
        {
            throw new ParseException(message);
        }
    }
}
