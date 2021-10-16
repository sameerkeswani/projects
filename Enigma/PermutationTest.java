package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Sameer Keswani
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void checkSize() {
        Alphabet a1 = new Alphabet("ABCD");
        String cycles1 = "(BA) (CD)";
        Permutation perm1 = new Permutation(cycles1, a1);
        assertEquals(a1.size(), perm1.size());

        String cycles2 = "";
        Permutation perm2 = new Permutation(cycles2, a1);
        assertEquals(a1.size(), perm2.size());

        Alphabet a2 = new Alphabet(UPPER_STRING);
        String cycles3 = "(ABCD) (GFERT) (LO)";
        Permutation perm3 = new Permutation(cycles3, a2);
        assertEquals(a2.size(), perm3.size());

        Permutation perm4 = new Permutation("", new Alphabet(""));
        assertEquals(0, perm4.size());
    }

    @Test
    public void testPermuteInt() {
        Permutation perm1 = new Permutation("(BACD)", new Alphabet("ABCD"));
        assertEquals(2, perm1.permute(0));
        assertEquals(1, perm1.permute(3));

        Permutation perm2 = new Permutation("(BADC)",
                new Alphabet(UPPER_STRING));
        assertEquals(2, perm2.permute(3));
        assertEquals(5, perm2.permute(5));
        assertEquals(25, perm2.permute(25));

        Permutation perm3 = new Permutation("", new Alphabet(UPPER_STRING));
        assertEquals(10, perm3.permute(10));
    }

    @Test
    public void testInvertInt() {
        Permutation perm1 = new Permutation("(BACD)", new Alphabet("ABCD"));
        assertEquals(3, perm1.invert(1));
        assertEquals(0, perm1.invert(2));

        Permutation perm2 = new Permutation("(BADC)",
                new Alphabet(UPPER_STRING));
        assertEquals(0, perm2.invert(3));
        assertEquals(5, perm2.invert(5));
        assertEquals(25, perm2.invert(25));

        Permutation perm3 = new Permutation("", new Alphabet(UPPER_STRING));
        assertEquals(10, perm3.invert(10));

    }

    @Test
    public void testPermuteChar() {
        Permutation perm1 = new Permutation("(BACD)", new Alphabet("ABCD"));
        assertEquals('C', perm1.permute('A'));
        assertEquals('B', perm1.permute('D'));

        Permutation perm2 = new Permutation("(BADC)",
                new Alphabet(UPPER_STRING));
        assertEquals('C', perm2.permute('D'));
        assertEquals('F', perm2.permute('F'));
        assertEquals('Z', perm2.permute('Z'));

        Permutation perm3 = new Permutation("", new Alphabet(UPPER_STRING));
        assertEquals('H', perm3.permute('H'));
    }

    @Test
    public void testInvertChar() {
        Permutation perm1 = new Permutation("(BACD)", new Alphabet("ABCD"));
        assertEquals('D', perm1.invert('B'));
        assertEquals('A', perm1.invert('C'));

        Permutation perm2 = new Permutation("(BADC)",
                new Alphabet(UPPER_STRING));
        assertEquals('A', perm2.invert('D'));
        assertEquals('E', perm2.invert('E'));
        assertEquals('Z', perm2.invert('Z'));

        Permutation perm3 = new Permutation("", new Alphabet(UPPER_STRING));
        assertEquals('G', perm3.invert('G'));
    }

    @Test (expected = EnigmaException.class)
    public void testNotInAlphabetInvert() {
        Permutation perm1 = new Permutation("(BACD)", new Alphabet("ABCD"));
        perm1.invert('F');
    }

    @Test (expected = EnigmaException.class)
    public void testNotInAlphabetPermute() {
        Permutation perm2 = new Permutation("(BACD)", new Alphabet("ABCD"));
        perm2.permute('F');
    }

    @Test
    public void testAlphabet() {
        Alphabet a1 = new Alphabet("ABCD");
        Permutation perm1 = new Permutation("(AB) (CD)", a1);
        assertTrue(a1.equals(perm1.alphabet()));
    }

    @Test
    public void testDerangement() {
        Alphabet a1 = new Alphabet("ABCD");
        Permutation perm1 = new Permutation("(AB) (CD)", a1);
        assertTrue(perm1.derangement());

        Alphabet a2 = new Alphabet("ABCDEF");
        Permutation perm2 = new Permutation("(AB) (CD)", a2);
        assertFalse(perm2.derangement());

        Permutation perm3 = new Permutation("", new Alphabet(UPPER_STRING));
        assertFalse(perm3.derangement());
    }

}
