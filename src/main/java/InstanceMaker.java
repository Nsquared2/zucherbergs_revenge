import java.util.*;

import weka.core.DenseInstance;

public class InstanceMaker {
    //holds communications counts between 2 players for current rounds
    static int[] comm_vector = new int[6];

    //TODO: Don't use this here, use this as a prior stored internally in model
    //holds enemy actions for all previous rounds
    static int[] action_vector = new int[4];

    static int num_attrs = 6;

    static HashMap<Object, Integer> index_map = Util.zipToHashMap(Arrays.asList(CommType.values()), Util.range(0, 6));

    /**
     * Takes communications for round and returns
     */
    public static DenseInstance makeInstance(Collection<Communication> comms){
        DenseInstance instance = new DenseInstance(num_attrs);
        int[] comm_vector = new int[6]; //array of zeros

        for(int i=0; i < num_attrs; i+=1){
            for(Communication comm: comms){
                //TODO: Verify this with Communication structure
                int index = index_map.get(comm.getAction());
                comm_vector[index] += 1;
            }
        }

        for(int i=0; i < num_attrs; i+=1){
            instance.setValue(i, comm_vector[i]);
        }

        return instance;
    }


}
