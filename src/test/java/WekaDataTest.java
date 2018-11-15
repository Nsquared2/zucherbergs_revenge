import org.junit.Test;
import org.junit.Assert;
import weka.core.Instances;

public class WekaDataTest {
    @Test
    public void test_createDataset(){
        Instances data = WekaData.makeDataset();
        //length is +1-1 for extra class feature but its zero indexed
        Assert.assertEquals(CommType.values().length, data.classIndex());
    }
}
