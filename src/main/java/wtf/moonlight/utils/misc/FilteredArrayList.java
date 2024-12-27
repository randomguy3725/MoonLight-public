package wtf.moonlight.utils.misc;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class FilteredArrayList<T, F> implements Iterable<F> {

    /* fields */
    private final Function<? super T, ? extends F> mapper;
    private final Supplier<Comparator<F>> comparator;

    private final Collection<T> list;
    private List<F> filtered;

    /* constructors */
    public FilteredArrayList(@NotNull Collection<T> list, @NotNull Function<? super T, ? extends F> mapper, @Nullable Supplier<Comparator<F>> comparator) {
        this.mapper = mapper;
        this.comparator = comparator;

        this.list = list;
        update();
    }

    /* methods */
    public void update() {
        this.filtered = new ObjectArrayList<>(list.size());

        for (T t : list) {
            F f = mapper.apply(t);
            if (f != null) filtered.add(f);
        }

        sortFiltered();
    }

    public void add(T t) {
        if (!list.contains(t)) {
            list.add(t);

            F f = mapper.apply(t);

            if (f != null) {
                filtered.add(f);
                sortFiltered();
            }
        }
    }

    public boolean remove(T t) {
        if (list.remove(t)) {
            F f = mapper.apply(t);
            if (f != null) filtered.remove(f);

            return true;
        }

        return false;
    }

    public void clear() {
        list.clear();
        filtered.clear();
    }

    public F get(int index) {
        return filtered.get(index);
    }

    public int indexOf(@NotNull T t) {
        F f = mapper.apply(t);
        return f != null ? filtered.indexOf(f) : -1;

    }

    @NotNull
    public Stream<F> stream() {
        return filtered.stream();
    }

    @NotNull
    public List<F> subList(int fromIndex, int toIndex) {
        return filtered.subList(fromIndex, toIndex);
    }

    public int size() {
        return filtered.size();
    }

    public boolean isEmpty() {
        return size() < 1;
    }

    @NotNull
    public List<F> getFiltered() {
        return filtered;
    }

    @NotNull
    public Collection<T> getUnfiltered() {
        return list;
    }

    @Override
    @NotNull
    public Iterator<F> iterator() {
        return filtered.iterator();
    }

    //region Internal part
    private void sortFiltered() {
        if (comparator != null) filtered.sort(comparator.get());
    }

    @NotNull
    private Set<F> mapCollection(@NotNull Collection<? extends T> c) {
        Set<F> result = new ObjectOpenHashSet<>(c.size());

        for (T t : c) {
            F f = mapper.apply(t);
            if (f != null) result.add(f);
        }

        return result;
    }
    //endregion

}
