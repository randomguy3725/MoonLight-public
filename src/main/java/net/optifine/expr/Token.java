package net.optifine.expr;

public class Token
{
    private final TokenType type;
    private final String text;

    public Token(TokenType type, String text)
    {
        this.type = type;
        this.text = text;
    }

    public TokenType getType()
    {
        return this.type;
    }

    public String getText()
    {
        return this.text;
    }

    public String toString()
    {
        return this.text;
    }
}
