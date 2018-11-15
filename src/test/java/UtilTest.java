import org.junit.Test;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UtilTest {
    @Test
    public void test_zipToHashMap(){
        ArrayList<Integer> l1 = new ArrayList<Integer>();
        l1.add(1);
        l1.add(2);
        ArrayList<String> l2 = new ArrayList<String>();
        l2.add("1");
        l2.add("2");
        HashMap<Integer, String> map = Util.zipToHashMap(l1, l2);

        HashMap<Integer, String> ans = new HashMap<Integer, String>();
        ans.put(1, "1");
        ans.put(2, "2");
        Assert.assertEquals(ans, map);
    }

    @Test
    public void test_range(){
        List<Integer> ans = new ArrayList<Integer>();
        ans.add(0);
        ans.add(1);
        ans.add(2);

        List<Integer> actual = Util.range(0,3);
        Assert.assertEquals(ans, actual);
    }

    @Test
    public void test_argMax(){
        List<Double> dummy = new ArrayList<Double>();
        dummy.add(3.0);
        dummy.add(8.0);
        dummy.add(2.0);
        int actual = Util.argmax(dummy);
        Assert.assertEquals(1, actual);
    }

    @Test
    public void test_enumIndexToValue(){
        ActionType ans = ActionType.BETRAY;
        ActionType actual = Util.enumIndexToValue(ActionType.class, 1);
        Assert.assertEquals(ans, actual);
    }
}
