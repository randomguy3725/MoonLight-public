package net.minecraft.util;

public interface IRegistry<K, V> extends Iterable<V>
{
    V getObject(K name);

    void putObject(K key, V value);
}
