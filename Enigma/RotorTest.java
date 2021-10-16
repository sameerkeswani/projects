package enigma;

import org.junit.Test;
import static org.junit.Assert.*;


/** The suite of all JUnit tests for the Rotor class.
 *  @author Sameer Keswani
 */
public class RotorTest {

    private Permutation perm1 = new Permutation("(DABC)", new Alphabet("ABCD"));
    private Permutation perm2 = new Permutation("(ABCDFELPQT", new Alphabet());

    @Test
    public void testName() {
        Rotor rotor1 = new Rotor("I", perm1);
        assertEquals("I", rotor1.name());

        Rotor rotor2 = new Rotor("II", perm2);
        assertEquals("II", rotor2.name());
    }

    @Test
    public void testAlphabet() {
        Rotor rotor1 = new Rotor("I", perm1);
        assertEquals(perm1.alphabet(), rotor1.alphabet());
    }

    @Test
    public void testPermutation() {
        Rotor rotor1 = new Rotor("I", perm1);
        assertEquals(perm1, rotor1.permutation());
    }

    @Test
    public void testSetting() {
        Rotor rotor1 = new Rotor("I", perm1);
        assertEquals(0, rotor1.setting());
        rotor1.set(3);
        assertEquals(3, rotor1.setting());
        rotor1.set(5);
        assertEquals(1, rotor1.setting());
        rotor1.set('A');
        assertEquals(0, rotor1.setting());


        Rotor rotor2 = new Rotor("II", perm2);
        assertEquals(0, rotor2.setting());
        rotor2.set(3);
        assertEquals(3, rotor2.setting());
        rotor2.set(5);
        assertEquals(5, rotor2.setting());
        rotor2.set(26);
        assertEquals(0, rotor2.setting());
    }

    @Test
    public void testConvertForward() {
        Rotor rotor1 = new Rotor("I", perm1);
        assertEquals(3, rotor1.convertForward(2));
        rotor1.set(rotor1.setting() + 1);
        assertEquals(0, rotor1.convertForward(3));


        Rotor rotor2 = new Rotor("II", perm2);
        assertEquals(1, rotor2.convertForward(0));
        rotor2.set(3);
        assertEquals(8, rotor2.convertForward(1));
    }

    @Test
    public void testConvertBackward() {
        Rotor rotor1 = new Rotor("I", perm1);
        assertEquals(2, rotor1.convertBackward(3));
        rotor1.set(rotor1.setting() + 1);
        assertEquals(1, rotor1.convertBackward(2));

        Rotor rotor2 = new Rotor("II", perm2);
        assertEquals(0, rotor2.convertBackward(1));
        rotor2.set(3);
        assertEquals(1, rotor2.convertBackward(8));
    }








}
