package net.optifine.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class StrUtils
{
    public static boolean equalsMask(String str, String mask, char wildChar, char wildCharSingle)
    {
        if (mask != null && str != null)
        {
            if (mask.indexOf(wildChar) < 0)
            {
                return mask.indexOf(wildCharSingle) < 0 ? mask.equals(str) : equalsMaskSingle(str, mask, wildCharSingle);
            }
            else
            {
                List list = new ArrayList();
                String s = "" + wildChar;

                if (mask.startsWith(s))
                {
                    list.add("");
                }

                StringTokenizer stringtokenizer = new StringTokenizer(mask, s);

                while (stringtokenizer.hasMoreElements())
                {
                    list.add(stringtokenizer.nextToken());
                }

                if (mask.endsWith(s))
                {
                    list.add("");
                }

                String s1 = (String)list.get(0);

                if (!startsWithMaskSingle(str, s1, wildCharSingle))
                {
                    return false;
                }
                else
                {
                    String s2 = (String)list.get(list.size() - 1);

                    if (!endsWithMaskSingle(str, s2, wildCharSingle))
                    {
                        return false;
                    }
                    else
                    {
                        int i = 0;

                        for (int j = 0; j < list.size(); ++j)
                        {
                            String s3 = (String)list.get(j);

                            if (s3.length() > 0)
                            {
                                int k = indexOfMaskSingle(str, s3, i, wildCharSingle);

                                if (k < 0)
                                {
                                    return false;
                                }

                                i = k + s3.length();
                            }
                        }

                        return true;
                    }
                }
            }
        }
        else
        {
            return mask == str;
        }
    }

    private static boolean equalsMaskSingle(String str, String mask, char wildCharSingle)
    {
        if (str != null && mask != null)
        {
            if (str.length() != mask.length())
            {
                return false;
            }
            else
            {
                for (int i = 0; i < mask.length(); ++i)
                {
                    char c0 = mask.charAt(i);

                    if (c0 != wildCharSingle && str.charAt(i) != c0)
                    {
                        return false;
                    }
                }

                return true;
            }
        }
        else
        {
            return str == mask;
        }
    }

    private static int indexOfMaskSingle(String str, String mask, int startPos, char wildCharSingle)
    {
        if (str != null && mask != null)
        {
            if (startPos >= 0 && startPos <= str.length())
            {
                if (str.length() < startPos + mask.length())
                {
                    return -1;
                }
                else
                {
                    for (int i = startPos; i + mask.length() <= str.length(); ++i)
                    {
                        String s = str.substring(i, i + mask.length());

                        if (equalsMaskSingle(s, mask, wildCharSingle))
                        {
                            return i;
                        }
                    }

                    return -1;
                }
            }
            else
            {
                return -1;
            }
        }
        else
        {
            return -1;
        }
    }

    private static boolean endsWithMaskSingle(String str, String mask, char wildCharSingle)
    {
        if (str != null && mask != null)
        {
            if (str.length() < mask.length())
            {
                return false;
            }
            else
            {
                String s = str.substring(str.length() - mask.length());
                return equalsMaskSingle(s, mask, wildCharSingle);
            }
        }
        else
        {
            return str == mask;
        }
    }

    private static boolean startsWithMaskSingle(String str, String mask, char wildCharSingle)
    {
        if (str != null && mask != null)
        {
            if (str.length() < mask.length())
            {
                return false;
            }
            else
            {
                String s = str.substring(0, mask.length());
                return equalsMaskSingle(s, mask, wildCharSingle);
            }
        }
        else
        {
            return str == mask;
        }
    }

    public static boolean equalsMask(String str, String[] masks, char wildChar)
    {
        for (int i = 0; i < masks.length; ++i)
        {
            String s = masks[i];

            if (equalsMask(str, s, wildChar))
            {
                return true;
            }
        }

        return false;
    }

    public static boolean equalsMask(String str, String mask, char wildChar)
    {
        if (mask != null && str != null)
        {
            if (mask.indexOf(wildChar) < 0)
            {
                return mask.equals(str);
            }
            else
            {
                List list = new ArrayList();
                String s = "" + wildChar;

                if (mask.startsWith(s))
                {
                    list.add("");
                }

                StringTokenizer stringtokenizer = new StringTokenizer(mask, s);

                while (stringtokenizer.hasMoreElements())
                {
                    list.add(stringtokenizer.nextToken());
                }

                if (mask.endsWith(s))
                {
                    list.add("");
                }

                String s1 = (String)list.get(0);

                if (!str.startsWith(s1))
                {
                    return false;
                }
                else
                {
                    String s2 = (String)list.get(list.size() - 1);

                    if (!str.endsWith(s2))
                    {
                        return false;
                    }
                    else
                    {
                        int i = 0;

                        for (int j = 0; j < list.size(); ++j)
                        {
                            String s3 = (String)list.get(j);

                            if (s3.length() > 0)
                            {
                                int k = str.indexOf(s3, i);

                                if (k < 0)
                                {
                                    return false;
                                }

                                i = k + s3.length();
                            }
                        }

                        return true;
                    }
                }
            }
        }
        else
        {
            return mask == str;
        }
    }

    public static String[] split(String str, String separators)
    {
        if (str != null && str.length() > 0)
        {
            if (separators == null)
            {
                return new String[] {str};
            }
            else
            {
                List list = new ArrayList();
                int i = 0;

                for (int j = 0; j < str.length(); ++j)
                {
                    char c0 = str.charAt(j);

                    if (equals(c0, separators))
                    {
                        list.add(str.substring(i, j));
                        i = j + 1;
                    }
                }

                list.add(str.substring(i));
                return (String[]) list.toArray(new String[list.size()]);
            }
        }
        else
        {
            return new String[0];
        }
    }

    private static boolean equals(char ch, String matches)
    {
        for (int i = 0; i < matches.length(); ++i)
        {
            if (matches.charAt(i) == ch)
            {
                return true;
            }
        }

        return false;
    }

    public static boolean equalsTrim(String a, String b)
    {
        if (a != null)
        {
            a = a.trim();
        }

        if (b != null)
        {
            b = b.trim();
        }

        return equals(a, b);
    }

    public static boolean isEmpty(String string)
    {
        return string == null || string.trim().length() <= 0;
    }

    public static String stringInc(String str)
    {
        int i = parseInt(str, -1);

        if (i == -1)
        {
            return "";
        }
        else
        {
            ++i;
            String s = "" + i;
            return s.length() > str.length() ? "" : fillLeft("" + i, str.length(), '0');
        }
    }

    public static int parseInt(String s, int defVal)
    {
        if (s == null)
        {
            return defVal;
        }
        else
        {
            try
            {
                return Integer.parseInt(s);
            }
            catch (NumberFormatException var3)
            {
                return defVal;
            }
        }
    }

    public static boolean isFilled(String string)
    {
        return !isEmpty(string);
    }

    public static String addIfNotContains(String target, String source)
    {
        for (int i = 0; i < source.length(); ++i)
        {
            if (target.indexOf(source.charAt(i)) < 0)
            {
                target = target + source.charAt(i);
            }
        }

        return target;
    }

    public static String fillLeft(String s, int len, char fillChar)
    {
        if (s == null)
        {
            s = "";
        }

        if (s.length() >= len)
        {
            return s;
        }
        else
        {
            StringBuffer stringbuffer = new StringBuffer();
            int i = len - s.length();

            while (stringbuffer.length() < i)
            {
                stringbuffer.append(fillChar);
            }

            return stringbuffer + s;
        }
    }

    public static String fillRight(String s, int len, char fillChar)
    {
        if (s == null)
        {
            s = "";
        }

        if (s.length() >= len)
        {
            return s;
        }
        else
        {
            StringBuffer stringbuffer = new StringBuffer(s);

            while (stringbuffer.length() < len)
            {
                stringbuffer.append(fillChar);
            }

            return stringbuffer.toString();
        }
    }

    public static boolean equals(Object a, Object b)
    {
        return a == b || (a != null && a.equals(b) || b != null && b.equals(a));
    }

    public static boolean startsWith(String str, String[] prefixes)
    {
        if (str == null)
        {
            return false;
        }
        else if (prefixes == null)
        {
            return false;
        }
        else
        {
            for (int i = 0; i < prefixes.length; ++i)
            {
                String s = prefixes[i];

                if (str.startsWith(s))
                {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean endsWith(String str, String[] suffixes)
    {
        if (str == null)
        {
            return false;
        }
        else if (suffixes == null)
        {
            return false;
        }
        else
        {
            for (int i = 0; i < suffixes.length; ++i)
            {
                String s = suffixes[i];

                if (str.endsWith(s))
                {
                    return true;
                }
            }

            return false;
        }
    }

    public static String removePrefix(String str, String prefix)
    {
        if (str != null && prefix != null)
        {
            if (str.startsWith(prefix))
            {
                str = str.substring(prefix.length());
            }

            return str;
        }
        else
        {
            return str;
        }
    }

    public static String removeSuffix(String str, String suffix)
    {
        if (str != null && suffix != null)
        {
            if (str.endsWith(suffix))
            {
                str = str.substring(0, str.length() - suffix.length());
            }

            return str;
        }
        else
        {
            return str;
        }
    }

    public static String replaceSuffix(String str, String suffix, String suffixNew)
    {
        if (str != null && suffix != null)
        {
            if (!str.endsWith(suffix))
            {
                return str;
            }
            else
            {
                if (suffixNew == null)
                {
                    suffixNew = "";
                }

                str = str.substring(0, str.length() - suffix.length());
                return str + suffixNew;
            }
        }
        else
        {
            return str;
        }
    }

    public static String replacePrefix(String str, String prefix, String prefixNew)
    {
        if (str != null && prefix != null)
        {
            if (!str.startsWith(prefix))
            {
                return str;
            }
            else
            {
                if (prefixNew == null)
                {
                    prefixNew = "";
                }

                str = str.substring(prefix.length());
                return prefixNew + str;
            }
        }
        else
        {
            return str;
        }
    }

    public static int findPrefix(String[] strs, String prefix)
    {
        if (strs != null && prefix != null)
        {
            for (int i = 0; i < strs.length; ++i)
            {
                String s = strs[i];

                if (s.startsWith(prefix))
                {
                    return i;
                }
            }

            return -1;
        }
        else
        {
            return -1;
        }
    }

    public static int findSuffix(String[] strs, String suffix)
    {
        if (strs != null && suffix != null)
        {
            for (int i = 0; i < strs.length; ++i)
            {
                String s = strs[i];

                if (s.endsWith(suffix))
                {
                    return i;
                }
            }

            return -1;
        }
        else
        {
            return -1;
        }
    }

    public static String[] remove(String[] strs, int start, int end)
    {
        if (strs == null)
        {
            return strs;
        }
        else if (end > 0 && start < strs.length)
        {
            if (start >= end)
            {
                return strs;
            }
            else
            {
                List<String> list = new ArrayList(strs.length);

                for (int i = 0; i < strs.length; ++i)
                {
                    String s = strs[i];

                    if (i < start || i >= end)
                    {
                        list.add(s);
                    }
                }

                String[] astring = list.toArray(new String[list.size()]);
                return astring;
            }
        }
        else
        {
            return strs;
        }
    }

    public static String removeSuffix(String str, String[] suffixes)
    {
        if (str != null && suffixes != null)
        {
            int i = str.length();

            for (int j = 0; j < suffixes.length; ++j)
            {
                String s = suffixes[j];
                str = removeSuffix(str, s);

                if (str.length() != i)
                {
                    break;
                }
            }

            return str;
        }
        else
        {
            return str;
        }
    }

    public static String removePrefix(String str, String[] prefixes)
    {
        if (str != null && prefixes != null)
        {
            int i = str.length();

            for (int j = 0; j < prefixes.length; ++j)
            {
                String s = prefixes[j];
                str = removePrefix(str, s);

                if (str.length() != i)
                {
                    break;
                }
            }

            return str;
        }
        else
        {
            return str;
        }
    }

    public static String removePrefixSuffix(String str, String[] prefixes, String[] suffixes)
    {
        str = removePrefix(str, prefixes);
        str = removeSuffix(str, suffixes);
        return str;
    }

    public static String removePrefixSuffix(String str, String prefix, String suffix)
    {
        return removePrefixSuffix(str, new String[] {prefix}, new String[] {suffix});
    }

    public static String getSegment(String str, String start, String end)
    {
        if (str != null && start != null && end != null)
        {
            int i = str.indexOf(start);

            if (i < 0)
            {
                return null;
            }
            else
            {
                int j = str.indexOf(end, i);
                return j < 0 ? null : str.substring(i, j + end.length());
            }
        }
        else
        {
            return null;
        }
    }

    public static String addSuffixCheck(String str, String suffix)
    {
        return str != null && suffix != null ? (str.endsWith(suffix) ? str : str + suffix) : str;
    }

    public static String addPrefixCheck(String str, String prefix)
    {
        return str != null && prefix != null ? (str.endsWith(prefix) ? str : prefix + str) : str;
    }

    public static String trim(String str, String chars)
    {
        if (str != null && chars != null)
        {
            str = trimLeading(str, chars);
            str = trimTrailing(str, chars);
            return str;
        }
        else
        {
            return str;
        }
    }

    public static String trimLeading(String str, String chars)
    {
        if (str != null && chars != null)
        {
            int i = str.length();

            for (int j = 0; j < i; ++j)
            {
                char c0 = str.charAt(j);

                if (chars.indexOf(c0) < 0)
                {
                    return str.substring(j);
                }
            }

            return "";
        }
        else
        {
            return str;
        }
    }

    public static String trimTrailing(String str, String chars)
    {
        if (str != null && chars != null)
        {
            int i = str.length();
            int j;

            for (j = i; j > 0; --j)
            {
                char c0 = str.charAt(j - 1);

                if (chars.indexOf(c0) < 0)
                {
                    break;
                }
            }

            return j == i ? str : str.substring(0, j);
        }
        else
        {
            return str;
        }
    }
}
