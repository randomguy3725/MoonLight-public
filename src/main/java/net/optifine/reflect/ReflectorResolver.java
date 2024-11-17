package net.optifine.reflect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReflectorResolver
{
    private static final List<IResolvable> RESOLVABLES = Collections.<IResolvable>synchronizedList(new ArrayList());
    private static boolean resolved = false;

    protected static void register(IResolvable resolvable)
    {
        if (!resolved)
        {
            RESOLVABLES.add(resolvable);
        }
        else
        {
            resolvable.resolve();
        }
    }

    public static void resolve()
    {
        if (!resolved)
        {
            for (IResolvable iresolvable : RESOLVABLES)
            {
                iresolvable.resolve();
            }

            resolved = true;
        }
    }
}
