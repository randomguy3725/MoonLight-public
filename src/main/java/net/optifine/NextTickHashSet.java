package net.optifine;

import com.google.common.collect.Iterators;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.util.BlockPos;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.NextTickListEntry;

public class NextTickHashSet extends TreeSet
{
    private final LongHashMap longHashMap = new LongHashMap();
    private int minX = Integer.MIN_VALUE;
    private int minZ = Integer.MIN_VALUE;
    private int maxX = Integer.MIN_VALUE;
    private int maxZ = Integer.MIN_VALUE;
    private static final int UNDEFINED = Integer.MIN_VALUE;

    public NextTickHashSet(Set oldSet)
    {
        for (Object object : oldSet)
        {
            this.add(object);
        }
    }

    public boolean contains(Object obj)
    {
        if (!(obj instanceof NextTickListEntry nextticklistentry))
        {
            return false;
        }
        else
        {
            Set set = this.getSubSet(nextticklistentry, false);
            return set != null && set.contains(nextticklistentry);
        }
    }

    public boolean add(Object obj)
    {
        if (!(obj instanceof NextTickListEntry nextticklistentry))
        {
            return false;
        }
        else
        {

            if (nextticklistentry == null)
            {
                return false;
            }
            else
            {
                Set set = this.getSubSet(nextticklistentry, true);
                boolean flag = set.add(nextticklistentry);
                boolean flag1 = super.add(obj);

                if (flag != flag1)
                {
                    throw new IllegalStateException("Added: " + flag + ", addedParent: " + flag1);
                }
                else
                {
                    return flag1;
                }
            }
        }
    }

    public boolean remove(Object obj)
    {
        if (!(obj instanceof NextTickListEntry nextticklistentry))
        {
            return false;
        }
        else
        {
            Set set = this.getSubSet(nextticklistentry, false);

            if (set == null)
            {
                return false;
            }
            else
            {
                boolean flag = set.remove(nextticklistentry);
                boolean flag1 = super.remove(nextticklistentry);

                if (flag != flag1)
                {
                    throw new IllegalStateException("Added: " + flag + ", addedParent: " + flag1);
                }
                else
                {
                    return flag1;
                }
            }
        }
    }

    private Set getSubSet(NextTickListEntry entry, boolean autoCreate)
    {
        if (entry == null)
        {
            return null;
        }
        else
        {
            BlockPos blockpos = entry.position;
            int i = blockpos.getX() >> 4;
            int j = blockpos.getZ() >> 4;
            return this.getSubSet(i, j, autoCreate);
        }
    }

    private Set getSubSet(int cx, int cz, boolean autoCreate)
    {
        long i = ChunkCoordIntPair.chunkXZ2Int(cx, cz);
        HashSet hashset = (HashSet)this.longHashMap.getValueByKey(i);

        if (hashset == null && autoCreate)
        {
            hashset = new HashSet();
            this.longHashMap.add(i, hashset);
        }

        return hashset;
    }

    public Iterator iterator()
    {
        if (this.minX == Integer.MIN_VALUE)
        {
            return super.iterator();
        }
        else if (this.size() <= 0)
        {
            return Iterators.emptyIterator();
        }
        else
        {
            int i = this.minX >> 4;
            int j = this.minZ >> 4;
            int k = this.maxX >> 4;
            int l = this.maxZ >> 4;
            List list = new ArrayList();

            for (int i1 = i; i1 <= k; ++i1)
            {
                for (int j1 = j; j1 <= l; ++j1)
                {
                    Set set = this.getSubSet(i1, j1, false);

                    if (set != null)
                    {
                        list.add(set.iterator());
                    }
                }
            }

            if (list.size() <= 0)
            {
                return Iterators.emptyIterator();
            }
            else if (list.size() == 1)
            {
                return (Iterator)list.get(0);
            }
            else
            {
                return Iterators.concat(list.iterator());
            }
        }
    }

    public void setIteratorLimits(int minX, int minZ, int maxX, int maxZ)
    {
        this.minX = Math.min(minX, maxX);
        this.minZ = Math.min(minZ, maxZ);
        this.maxX = Math.max(minX, maxX);
        this.maxZ = Math.max(minZ, maxZ);
    }

    public void clearIteratorLimits()
    {
        this.minX = Integer.MIN_VALUE;
        this.minZ = Integer.MIN_VALUE;
        this.maxX = Integer.MIN_VALUE;
        this.maxZ = Integer.MIN_VALUE;
    }
}
