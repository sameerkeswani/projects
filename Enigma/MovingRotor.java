package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Sameer Keswani
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
    }
    @Override
    boolean rotates() {
        return true;
    }

    @Override
    boolean atNotch() {
        char ch = alphabet().toChar(setting());
        for (int i = 0; i < _notches.length(); i += 1) {
            if (_notches.charAt(i) == ch) {
                return true;
            }
        }
        return false;
    }

    @Override
    void advance() {
        set(setting() + 1);
    }

    /**The notches of a rotor. */
    private String _notches;




}
