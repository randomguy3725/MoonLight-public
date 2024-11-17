package net.minecraft.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Iterator;
import java.util.Map;

public class RegistryNamespaced<K, V> extends RegistrySimple<K, V> implements IObjectIntIterable<V>
{
    protected final ObjectIntIdentityMap<V> underlyingIntegerMap = new ObjectIntIdentityMap();
    protected final Map<V, K> inverseObjectRegistry;

    public RegistryNamespaced()
    {
        this.inverseObjectRegistry = ((BiMap)this.registryObjects).inverse();
    }

    public void register(int id, K key, V value)
    {
        this.underlyingIntegerMap.put(value, id);
        this.putObject(key, value);
    }

    protected Map<K, V> createUnderlyingMap()
    {
        return HashBiMap.create();
    }

    public V getObject(K name)
    {
        return super.getObject(name);
    }

    public K getNameForObject(V value)
    {
        return this.inverseObjectRegistry.get(value);
    }

    public boolean containsKey(K key)
    {
        return super.containsKey(key);
    }

    public int getIDForObject(V value)
    {
        return this.underlyingIntegerMap.get(value);
    }

    public V getObjectById(int id)
    {
        return this.underlyingIntegerMap.getByValue(id);
    }

    public Iterator<V> iterator()
    {
        return this.underlyingIntegerMap.iterator();
    }
}
