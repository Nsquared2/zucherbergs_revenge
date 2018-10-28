import org.junit.Test;
import org.junit.Assert;

import java.util.ArrayList;

public class EasyAIPlayerTest {
    @Test
    public void returns_correct_number_of_actions(){
        AIPlayer ai = AIHandler.create_ai("easy", 1, "joe");
        ArrayList<Integer> ids = new ArrayList<Integer>();
        ids.add(1); ids.add(2); ids.add(3);
        ArrayList<Action> actions = ai.round_action(ids);
        Assert.assertEquals(actions.size(), 3);
    }
}
