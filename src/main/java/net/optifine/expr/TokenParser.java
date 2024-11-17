package net.optifine.expr;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class TokenParser
{
    public static Token[] parse(String str) throws IOException, ParseException
    {
        Reader reader = new StringReader(str);
        PushbackReader pushbackreader = new PushbackReader(reader);
        List<Token> list = new ArrayList();

        while (true)
        {
            int i = pushbackreader.read();

            if (i < 0)
            {
                Token[] atoken = list.toArray(new Token[list.size()]);
                return atoken;
            }

            char c0 = (char)i;

            if (!Character.isWhitespace(c0))
            {
                TokenType tokentype = TokenType.getTypeByFirstChar(c0);

                if (tokentype == null)
                {
                    throw new ParseException("Invalid character: '" + c0 + "', in: " + str);
                }

                Token token = readToken(c0, tokentype, pushbackreader);
                list.add(token);
            }
        }
    }

    private static Token readToken(char chFirst, TokenType type, PushbackReader pr) throws IOException
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append(chFirst);

        while (true)
        {
            int i = pr.read();

            if (i < 0)
            {
                break;
            }

            char c0 = (char)i;

            if (!type.hasCharNext(c0))
            {
                pr.unread(c0);
                break;
            }

            stringbuffer.append(c0);
        }

        return new Token(type, stringbuffer.toString());
    }
}
