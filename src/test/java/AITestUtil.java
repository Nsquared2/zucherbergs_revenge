import weka.core.DenseInstance;

import java.util.ArrayList;

public class AITestUtil {
    public static ArrayList<Integer> dummy_ids(int num){
        ArrayList<Integer> ids = new ArrayList<Integer>();
        for(int i = 1; i <= num; i++){
            ids.add(i);
        }
        return ids;
    }

    public static void setAIDirs(){
        AIHandler.setDataPathForTesting("./ai_test/data/");
        AIHandler.setModelPathForTesting("./ai_test/models/");
    }

    public static DenseInstance makeDummyInstance(){
        DenseInstance instance = new DenseInstance(WekaData.num_attrs);

        for(int i=0; i < WekaData.num_attrs; i+=1){
            instance.setValue(i, 1);
        }

        return instance;
    }
}
