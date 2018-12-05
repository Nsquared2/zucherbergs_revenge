import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newLinkedList;

public class Util {

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
        for (int i = min; i < max; i++) {
            list.add(i);
        }

        return list;
    }

    public static <C extends Iterable<Double>> int argmax(C arr){
        Iterator<Double> iter = arr.iterator();
        double max = iter.next();
        int max_id = 0;
        int i = 1;

        while(iter.hasNext()) {
            if(iter.next() > max)
                max_id = i;
            i += 1;
        }

        return max_id;
    }

    public static int argmax(double[] arr){
        double max = arr[0];
        int max_id = 0;

        for(int i = 0; i < arr.length; i+=1){
            if(arr[i] > max)
                max_id = i;
        }

        return max_id;
    }

    public static  <E extends Enum<E>> E enumIndexToValue(Class<E> my_enum, int index){
        List<E> vals = Arrays.asList(my_enum.getEnumConstants());
        return vals.get(index);
    }

    public static void scalarArrayMultiply(double[] arr, double scalar){
        for(int i = 0; i < arr.length; i++){
            arr[i] *= scalar;
        }
    }

    public static void elementwiseAdd(double[] arr1, double[] arr2){
        for(int i = 0; i < arr1.length; i++){
            arr1[i] += arr2[i];
        }
    }
}
