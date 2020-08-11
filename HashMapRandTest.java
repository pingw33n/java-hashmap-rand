import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HashMapRandTest {
    private static final int ITERS = 100;

    public static void main(String[] args) {
        checkHashMap();
        checkHashSet();
    }

    private static void checkHashMap() {
        checkMap(new HashMap<>(Map.of("a", "1", "b", "2", "c", "3")));
    }

    private static void checkHashSet() {
        checkSet(new HashSet<>(Set.of("1", "2", "3")));
    }

    private static <K, V> void checkMap(Map<K, V> map) {
        equalsMapForEach(map, map);
        checkSet(map.entrySet());
        checkSet(map.keySet());
        checkIterable(map.values());
    }

    private static <T> void checkSet(Set<T> set) {
        checkIterable(set);
        checkStream(set::stream);
        checkIterable(set);
    }

    private static <T> void checkStream(Supplier<Stream<T>> f) {
        assert falseOnce(() -> equalsStream(f.get(), f.get()));
        assert falseOnce(() -> equalsStreamForEach(f.get(), f.get()));
        assert falseOnce(() -> equalsStreamForEachOrdered(f.get(), f.get()));
    }

    private static <T> void checkIterator(Supplier<Iterator<T>> f) {
        assert falseOnce(() -> equalsIterator(f.get(), f.get()));
        assert falseOnce(() -> equalsIteratorForEachRemaining(f.get(), f.get()));
    }

    private static <T> void checkIterable(Iterable<T> f) {
        checkIterator(f::iterator);
        checkSpliterator(f::spliterator);
        assert falseOnce(() -> equalsIterableForEach(f, f));
    }

    private static <T> void checkSpliterator(Supplier<Spliterator<T>> f) {
        assert falseOnce(() -> equalsSpliterator(f.get(), f.get()));
        assert falseOnce(() -> equalsSpliteratorForEachRemaining(f.get(), f.get()));
    }

    private static <T> boolean equalsSpliteratorForEachRemaining(Spliterator<T> a, Spliterator<T> b) {
        List<T> aa = new ArrayList<>();
        List<T> bb = new ArrayList<>();
        a.forEachRemaining(aa::add);
        b.forEachRemaining(bb::add);
        return equalsIterator(aa.iterator(), bb.iterator());
    }

    private static <T> boolean equalsSpliterator(Spliterator<T> a, Spliterator<T> b) {
        List<T> aa = new ArrayList<>();
        List<T> bb = new ArrayList<>();
        while (a.tryAdvance(aa::add)) { }
        while (b.tryAdvance(bb::add)) { }
        return equalsIterator(aa.iterator(), bb.iterator());
    }

    private static boolean falseOnce(Supplier<Boolean> f) {
        for (int i = 0; i < ITERS; i++) {
            if (!f.get()) {
                return true;
            }
        }
        return false;
    }

    private static <T> boolean equalsIterator(Iterator<T> a, Iterator<T> b) {
        List<T> aa = new ArrayList<>();
        while (a.hasNext()) {
            aa.add(a.next());
        }
        List<T> bb = new ArrayList<>();
        while (b.hasNext()) {
            bb.add(b.next());
        }
        assert aa.size() == bb.size();
        return aa.equals(bb);
    }

    private static <T> boolean equalsStream(Stream<T> a, Stream<T> b) {
        return equalsIterator(a.collect(Collectors.toList()).iterator(),
                              b.collect(Collectors.toList()).iterator());
    }

    private static <K ,V> boolean equalsMapForEach(Map<K, V> a, Map<K, V> b) {
        List<List<Object>> aa = new ArrayList<>();
        List<List<Object>> bb = new ArrayList<>();
        a.forEach((k, v) -> aa.add(Arrays.asList(k, v)));
        b.forEach((k, v) -> bb.add(Arrays.asList(k, v)));
        return equalsIterator(aa.iterator(), bb.iterator());
    }

    private static <T> boolean equalsIterableForEach(Iterable<T> a, Iterable<T> b) {
        List<T> aa = new ArrayList<>();
        List<T> bb = new ArrayList<>();
        a.forEach(aa::add);
        b.forEach(bb::add);
        return equalsIterator(aa.iterator(), bb.iterator());
    }

    private static <T> boolean equalsStreamForEach(Stream<T> a, Stream<T> b) {
        List<T> aa = new ArrayList<>();
        List<T> bb = new ArrayList<>();
        a.forEach(aa::add);
        b.forEach(bb::add);
        return equalsIterator(aa.iterator(), bb.iterator());
    }

    private static <T> boolean equalsStreamForEachOrdered(Stream<T> a, Stream<T> b) {
        List<T> aa = new ArrayList<>();
        List<T> bb = new ArrayList<>();
        a.forEachOrdered(aa::add);
        b.forEachOrdered(bb::add);
        return equalsIterator(aa.iterator(), bb.iterator());
    }

    private static <T> boolean equalsIteratorForEachRemaining(Iterator<T> a, Iterator<T> b) {
        List<T> aa = new ArrayList<>();
        List<T> bb = new ArrayList<>();
        a.forEachRemaining(aa::add);
        b.forEachRemaining(bb::add);
        return equalsIterator(aa.iterator(), bb.iterator());
    }
}
