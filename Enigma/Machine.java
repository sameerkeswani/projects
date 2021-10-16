package enigma;


import java.util.ArrayList;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Sameer Keswani
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            ArrayList<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors =  allRotors;
        if (_numRotors == 0) {
            throw new EnigmaException("Need more than 0 rotors");
        }
        if (_pawls >= _numRotors) {
            throw new EnigmaException("Can not have more pawls than numRotors");
        }


    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _machineRotors = new ArrayList<Rotor>();
        for (String name : rotors) {
            for (Rotor rotor : _allRotors) {
                if (rotor.name().equals(name)) {
                    _machineRotors.add(rotor);
                    break;
                }
            }
        }

        if (!_machineRotors.get(0).reflecting()) {
            throw new EnigmaException("The first rotor must be a reflector.");
        }
        int totalMoving = 0;
        for (Rotor rotor : _machineRotors) {
            if (rotor.rotates()) {
                totalMoving += 1;
            }
        }
        if (totalMoving != _pawls) {
            throw new EnigmaException("wrong rotors inserted");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != numRotors() - 1) {
            throw new EnigmaException("Setting must have a "
                    + "length of numRotors()-1");
        }

        for (int i = 1; i < _numRotors; i += 1) {
            Rotor modifiedRotor = _machineRotors.get(i);
            modifiedRotor.set(setting.charAt(i - 1));
            _machineRotors.set(i, modifiedRotor);
        }

    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;

    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        int index = _numRotors - 1;
        boolean rotateNext = false;

        while (_machineRotors.get(index).rotates()) {
            Rotor currentRotor = _machineRotors.get(index);
            Rotor previousRotor = _machineRotors.get(index - 1);
            if (currentRotor.rotates()) {
                if (currentRotor.atNotch() && previousRotor.rotates()) {
                    currentRotor.advance();
                    rotateNext = true;
                } else if (rotateNext) {
                    currentRotor.advance();
                    rotateNext = false;
                } else if (index == _numRotors - 1) {
                    currentRotor.advance();
                }
            }
            index -= 1;
        }

        int converted = _plugboard.permute(c);

        for (int i = _numRotors - 1; i > -1; i -= 1) {
            converted = _machineRotors.get(i).convertForward(converted);
        }
        for (int i = 1; i < _numRotors; i += 1) {
            converted = _machineRotors.get(i).convertBackward(converted);
        }

        converted = _plugboard.invert(converted);




        return converted;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        msg = msg.replaceAll("\\s", "");
        String translated = "";
        for (int i = 0; i < msg.length(); i += 1) {
            int converted = convert(_alphabet.toInt(msg.charAt(i)));
            translated += _alphabet.toChar(converted);
        }
        return translated;
    }

    /** Checks to make sure the rotors are in a valid orde. */
    void checkMachineRotors() {
        if (!_machineRotors.get(0).reflecting()) {
            throw new EnigmaException("The first rotor must be a reflector.");
        }

        for (int i = 1; i < _numRotors - numPawls() - 1; i += 1) {
            if (_machineRotors.get(i).rotates()) {
                throw new EnigmaException("Expecting a Fixed Rotor");
            }
        }

        for (int i = _numRotors - numPawls(); i < _numRotors; i += 1) {
            if (!_machineRotors.get(i).rotates()) {
                throw new EnigmaException("Expecting a Moving Rotor");
            }
        }


    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotors in current Machine. */
    private int _numRotors;

    /** Number of pawls/moving rotors in current Machine. */
    private int _pawls;

    /** Collection of all possible rotors to be chosen from. */
    private Collection<Rotor> _allRotors;

    /**Rotors being used by machine. */
    private ArrayList<Rotor> _machineRotors = new ArrayList<Rotor>();

    /**The plugboard permutation. */
    private Permutation _plugboard;
}
