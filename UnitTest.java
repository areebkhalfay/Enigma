package enigma;

import org.junit.Test;
import ucb.junit.textui;

import static org.junit.Assert.assertEquals;

/** The suite of all JUnit tests for the enigma package.
 *  @author Areeb Khalfay
 */
public class UnitTest {

    /** Run the JUnit tests in this package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(PermutationTest.class, MovingRotorTest.class);
    }

    @Test
    public void checkPermutation() {
        CharacterRange alphabet = new CharacterRange('A', 'Z');
        String cyclestest1 = "(AELTPHQXRU) "
                +
                "(BKNW) (CMOY) (DFG) (IV) (JZ) (S)";
        String cyclestest2 = "(AELTPHQXRU) "
                +
                "(BKNW) (CMOY) (DFG) (IV) (JZS)";
        Permutation test1 = new Permutation(cyclestest1, alphabet);
        Permutation test2 = new Permutation(cyclestest2, alphabet);
        assertEquals('A', test1.permute('U'));
        assertEquals('U', test1.invert('A'));
        assertEquals('S', test1.permute('S'));
        assertEquals('S', test1.invert('S'));
        assertEquals('K', test1.permute('B'));
        assertEquals('I', test1.permute('V'));
        assertEquals('I', test1.invert('V'));
        assertEquals(6, test1.permute(5));
        assertEquals(13, test1.invert(22));
        assertEquals(false, test1.derangement());
        assertEquals(true, test2.derangement());

    }
}


