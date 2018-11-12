import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import weka.core.DenseInstance;

public class InstanceMaker {
    //holds communications counts between 2 players for current rounds
    static int[] comm_vector = new int[6];

    //TODO: Don't use this here, use this as a prior stored internally in model
    //holds enemy actions for all previous rounds
    static int[] action_vector = new int[4];

    static int num_attrs = 6;

    static Map id_map = MapUtil.zipToMap(MapUtil.range(0, 6), Arrays.asList(CommType.values()));

    public static DenseInstance makeInstance(ArrayList<Communication> comms){
        DenseInstance Instance = new DenseInstance(this.num_attrs);
        for(int i=0; i < this.num_attrs; i+=1){
            for(Communication comm: comms){
                i
            }
        }

    }

}
