import org.junit.Test;
import org.junit.Assert;

public class AIHandlerTest{
    @Test(expected = IllegalArgumentException.class)
    public void wrong_difficulty_exception(){
        AIPlayer ai = AIHandler.create_ai("not_a_difficulty", 1, "joe");
   }

   @Test
    public void make_easy(){
       AIPlayer ai = AIHandler.create_ai("easy", 1, "joe");
       Assert.assertTrue(ai instanceof EasyAIPlayer);
   }

}