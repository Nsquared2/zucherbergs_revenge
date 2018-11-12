import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.Iterator;

import static com.google.common.collect.Lists.newLinkedList;

public class MapUtil {

    public static <K, V> Map<K, V> zipToMap(List<K> keys, List<V> values) {
        return IntStream.range(0, keys.size()).boxed().collect(Collectors.toMap(keys::get, values::get));
    }

    public static <K, V> HashMap<K, V> zipToHashMap(List<K> keys, List<V> values) {
        HashMap map = new HashMap();
        Iterator<K> i1 = keys.iterator();
        Iterator<V> i2 = values.iterator();
        while (i1.hasNext() || i2.hasNext()) map.put(i1.next(), i2.next());
        return map;
    }

    public static List<Integer> range(int min, int max) {
        List<Integer> list = newLinkedList();
        for (int i = min; i <= max; i++) {
            list.add(i);
        }

        return list;
    }
}
