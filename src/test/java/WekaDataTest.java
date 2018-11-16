import com.google.common.collect.ArrayListMultimap;
import org.junit.Test;
import org.junit.Assert;
import weka.core.Instances;

import java.util.ArrayList;

public class WekaDataTest {
    @Test
    public void test_createDataset(){
        Instances data = WekaData.makeDataset();
        //length is +1-1 for extra class feature but its zero indexed
        Assert.assertEquals(CommType.values().length, data.classIndex());
    }

    @Test
    public void test_count_comms() {
        int[] ans = {1, 2, 1, 1, 0, 1};

        ArrayList<Communication> dummy_comms = new ArrayList<Communication>();
        dummy_comms.add(new Communication(CommType.REQUEST_COOP, 1, 0));
        dummy_comms.add(new Communication(CommType.REQUEST_BETRAY, 1, 0));
        dummy_comms.add(new Communication(CommType.REQUEST_BETRAY, 1, 0));
        dummy_comms.add(new Communication(CommType.PROMISE_IGNORE, 1, 0));
        dummy_comms.add(new Communication(CommType.PROMISE_COOP, 1, 0));
        dummy_comms.add(new Communication(CommType.PROMISE_IGNORE, 1, 0));

        int[] actual = WekaData.getCommCounts(dummy_comms);

        //TODO: need different equals for array
        Assert.assertEquals(ans, actual);
    }
}
