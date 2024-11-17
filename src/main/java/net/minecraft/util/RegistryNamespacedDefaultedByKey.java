package net.minecraft.util;

import org.apache.commons.lang3.Validate;

public class RegistryNamespacedDefaultedByKey<K, V> extends RegistryNamespaced<K, V>
{
    private final K defaultValueKey;
    private V defaultValue;

    public RegistryNamespacedDefaultedByKey(K defaultValueKeyIn)
    {
        this.defaultValueKey = defaultValueKeyIn;
    }

    public void register(int id, K key, V value)
    {
        if (this.defaultValueKey.equals(key))
        {
            this.defaultValue = value;
        }

        super.register(id, key, value);
    }

    public void validateKey()
    {
        Validate.notNull(this.defaultValueKey);
    }

    public V getObject(K name)
    {
        V v = super.getObject(name);
        return v == null ? this.defaultValue : v;
    }

    public V getObjectById(int id)
    {
        V v = super.getObjectById(id);
        return v == null ? this.defaultValue : v;
    }
}
