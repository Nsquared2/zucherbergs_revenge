import java.util.*;

import weka.core.*;
import weka.core.converters.ConverterUtils;

public class WekaData {
    //holds communications counts between 2 players for current rounds
    static int[] comm_vector = new int[World.num_comms()];

    //TODO: Don't use this here, use this as a prior stored internally in model
    //holds enemy actions for all previous rounds
    static int[] action_vector = new int[World.num_actions()];

    static int num_attrs = 6;

    static HashMap<Object, Integer> index_map = Util.zipToHashMap(Arrays.asList(CommType.values()), Util.range(0, World.num_comms()));

    /**
     * Create dataset from xml file
     */
    public static Instances makeDataset(){
        ArrayList<Attribute> atts = new ArrayList<Attribute>();

        for(CommType comm: CommType.values()) {
            Attribute x = new Attribute(comm.toString());
            atts.add(x);
        }

        List y_vals = new ArrayList(3);
        for(ActionType action: ActionType.values()){
            y_vals.add(action.toString());
        }
        Attribute y = new Attribute("y", y_vals);
        atts.add(y);

        Instances dataset = new Instances("Dataset", atts, 50000);

        //Set which attribute is class label
        dataset.setClassIndex(dataset.numAttributes() - 1);

        return dataset;
    }

    public static int[] getCommCounts(Collection<Communication> comms){
        int[] comm_vector = new int[6]; //array of zeros

        for(Communication comm: comms){
            //TODO: Verify this with Communication structure
            int index = index_map.get(comm.getAction());
            comm_vector[index] += 1;
        }

        return comm_vector;
    }

    /**
     * Takes communications for round and returns
     */
    public static SparseInstance makeInstance(Collection<Communication> comms, Instances eval_data){
        SparseInstance instance = new SparseInstance(num_attrs);
        instance.setDataset(eval_data);
        int[] comm_vector = getCommCounts(comms);

        for(int i=0; i < num_attrs; i+=1){
            instance.setValue(i, comm_vector[i]);
        }

        return instance;
    }

    /**
     * Takes communications and round actions for round and creates instance
     * @param comms Communications for round
     * @param enemy_choice Action of the enemy
     * @param round_instances Dataset instance will be associated with
     * @return
     */
    public static DenseInstance makeInstance(Collection<Communication> comms, ActionType enemy_choice, Instances round_instances){
        DenseInstance instance = new DenseInstance(num_attrs+1);
        instance.setDataset(round_instances);
        int[] comm_vector = getCommCounts(comms);

        for(int i=0; i < num_attrs; i+=1){
            instance.setValue(i, comm_vector[i]);
        }

        instance.setValue(num_attrs, enemy_choice.toString());

        return instance;
    }

    /**
     * Merge two sets of instances by appending row wise. Not to be confused with weka mergeInstances
     * @param data1 Instances
     * @param data2 Instances
     * @return Merged Instances
     * @throws Exception
     */
    public static Instances mergeInstances(Instances data1, Instances data2)
    {
        // Check where are the string attributes
        int asize = data1.numAttributes();
        boolean strings_pos[] = new boolean[asize];
        for(int i=0; i<asize; i++)
        {
            Attribute att = data1.attribute(i);
            strings_pos[i] = ((att.type() == Attribute.STRING) ||
                    (att.type() == Attribute.NOMINAL));
        }

        // Create a new dataset
        Instances dest = new Instances(data1);
        dest.setRelationName(data1.relationName() + "+" + data2.relationName());

        ConverterUtils.DataSource source = new ConverterUtils.DataSource(data2);
        try {
            Instances instances = source.getStructure();
            Instance instance = null;
            while (source.hasMoreElements(instances)) {
                instance = source.nextElement(instances);
                dest.add(instance);

                // Copy string attributes
                for (int i = 0; i < asize; i++) {
                    if (strings_pos[i]) {
                        dest.instance(dest.numInstances() - 1)
                                .setValue(i, instance.stringValue(i));
                    }
                }
            }
        }
        catch (Exception e){
            System.out.println("Instances could not be merged " + e.toString());
        }

        return dest;
    }
}
