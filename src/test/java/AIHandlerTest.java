import org.junit.Test;

public class AIHandlerTest{
    @Test(expected = IllegalArgumentException.class)
    public void wrong_difficulty_exception(){
        AIPlayer ai = AIHandler.create_ai("not_a_difficulty", 1, "joe");
   }

}