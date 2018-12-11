package enigma;

import org.junit.Test;
/** Piece of Code Written to test Main.
 * @author Areeb Khalfay */
public class MainTest {
    /** Testing the code to make sure the
     *  right output is displayed in the file. */
    @Test
    public void mainTest() {
        String[] args = new String[]
        {"testing/correct/default.conf", "testing/correct/carroll1.inp",
            "testing/correct/carrol1.out"};
        Main main = new Main(args);
        main.process();
    }
}
