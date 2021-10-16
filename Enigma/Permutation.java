package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Sameer Keswani
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;

        for (int i = 0; i < alphabet.size(); i += 1) {
            boolean isSelfMap = true;
            for (int j = 0; j < cycles.length(); j += 1) {
                if (cycles.charAt(j) == alphabet.toChar(i)) {
                    isSelfMap = false;
                }
            }
            if (isSelfMap) {
                _selfMap += alphabet.toChar(i);
            }
        }





    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        int index = wrap(p);
        char ch = _alphabet.toChar(index);
        char first = (char) 0;
        char permuted = (char) 0;

        if (!checkIfInSelfMap(ch)) {
            for (int i = 0; i < _cycles.length(); i += 1) {
                if (_cycles.charAt(i) == '(') {
                    first = _cycles.charAt(i + 1);
                } else if ((_cycles.charAt(i) == ch)) {
                    if ((_cycles.charAt(i + 1) == ')')) {
                        permuted = first;
                    } else {
                        permuted = _cycles.charAt(i + 1);
                    }
                }
            }
        } else {
            permuted = ch;
        }

        return _alphabet.toInt(permuted);
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        int index = wrap(c);
        char ch = _alphabet.toChar(index);
        char inverted = (char) 0;
        char last = (char) 0;


        if (!checkIfInSelfMap(ch)) {
            for (int i = _cycles.length() - 1; i >= 0; i -= 1) {
                if (_cycles.charAt(i) == ')') {
                    last = _cycles.charAt(i - 1);
                } else if ((_cycles.charAt(i) == ch)) {
                    if ((_cycles.charAt(i - 1) == '(')) {
                        inverted = last;
                    } else {
                        inverted = _cycles.charAt(i - 1);
                    }
                }
            }
        } else {
            inverted = ch;
        }
        return _alphabet.toInt(inverted);
    }


    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        if (!_alphabet.contains(p)) {
            throw new EnigmaException("character does not exist in alphabet");
        }
        int index = _alphabet.toInt(p);
        int indexInAlphabet = permute(index);

        return _alphabet.toChar(wrap(indexInAlphabet));
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        if (!_alphabet.contains(c)) {
            throw new EnigmaException("character does not exist in alphabet");
        }

        int index = _alphabet.toInt(c);
        int indexInAlphabet = invert(index);

        return _alphabet.toChar(wrap(indexInAlphabet));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        if (_selfMap.length() == 0) {
            return true;
        }
        return false;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /**The alphabet letters that map to themseleves. */
    private String _selfMap = "";

    /** The cycle of letters mapped to each other. */
    private String _cycles;

    /** @param c @return true/false.
     * Check if a letter is mapped to itself.*/
    boolean checkIfInSelfMap(char c) {
        for (int i = 0; i < _selfMap.length(); i += 1) {
            if (_selfMap.charAt(i) == c) {
                return true;
            }
        }
        return false;
    }

}
