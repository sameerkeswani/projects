package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Sameer Keswani
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        if (!_input.hasNext()) {
            throw error("Need to provide input.");
        }
        Machine machine = readConfig();
        boolean hasAsterisk = false;
        while (_input.hasNextLine()) {
            String line = _input.nextLine();
            if (line.isEmpty()) {
                printMessageLine(line);
            } else if (line.charAt(0) == '*') {
                hasAsterisk = true;
                setUp(machine, line);
            } else {
                if (!hasAsterisk) {
                    throw error("Need * at first line");
                } else {
                    printMessageLine(machine.convert(line));
                }
            }

        }

    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            _alphabet = new Alphabet(_config.nextLine());
            _numRotors = _config.nextInt();
            _numPawls = _config.nextInt();

            _allRotors = new ArrayList<>();
            while (_config.hasNext()) {
                Rotor newRotor = readRotor();
                _allRotors.add(newRotor);
            }
            for (Rotor rotor1 : _allRotors) {
                for (Rotor rotor2 : _allRotors) {
                    if ((rotor1.name() == rotor2.name()) && rotor1 != rotor2) {
                        throw error("No duplicate rotors allowed in allRotors");
                    }
                }
            }
            return new Machine(_alphabet, _numRotors, _numPawls, _allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String rotorName = _config.next();

            String rotorTypeAndNotch = _config.next();
            char rotorType = rotorTypeAndNotch.charAt(0);
            String notches = rotorTypeAndNotch.substring(1);

            String permCycles = "";

            while (_config.hasNext("\\(.*\\)")) {
                permCycles += _config.next();
            }

            Permutation permutation = new Permutation(permCycles, _alphabet);
            Rotor rotor;
            if (rotorType == 'R') {
                Reflector reflector = new Reflector(rotorName, permutation);
                return reflector;
            } else if (rotorType == 'N') {
                FixedRotor fixed = new FixedRotor(rotorName, permutation);
                return fixed;
            } else if (rotorType == 'M') {
                MovingRotor moving = new MovingRotor(rotorName,
                        permutation, notches);
                return moving;
            } else {
                throw error("Invalid rotor type");
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        Scanner s = new Scanner(settings);
        ArrayList<String> setUp = new ArrayList<>();
        s.next("\\*");
        while (s.hasNext()) {
            setUp.add(s.next());
        }

        String[] rotorNames = new String[_numRotors];
        for (int i = 0; i < M.numRotors(); i += 1) {
            rotorNames[i] = setUp.get(i);
        }
        for (int i = 0; i < rotorNames.length; i += 1) {
            for (int j = 0; j < rotorNames.length; j += 1) {
                if ((rotorNames[i].equals(rotorNames[j]) && (i != j))) {
                    throw error("Can't have duplicate names.");
                }
            }
        }
        String[] allRotorNames = new String[_allRotors.size()];
        for (int i = 0; i < _allRotors.size(); i += 1) {
            allRotorNames[i] = _allRotors.get(i).name();
        }
        boolean checkBadName;
        for (String name1 : rotorNames) {
            checkBadName = true;
            for (String name2: allRotorNames) {
                if (name1.equals(name2)) {
                    checkBadName = false;
                }
            }
            if (checkBadName) {
                throw error("Bad rotor name");
            }
        }
        M.insertRotors(rotorNames);
        String notches = setUp.get(M.numRotors());
        for (int i = 0; i < notches.length(); i += 1) {
            if (!_alphabet.contains(notches.charAt(i))) {
                throw error("notches need to be in alphabet");
            }
        }
        if (notches.length() > _numRotors - 1
                || notches.length() < _numRotors - 1) {
            throw error("Wrong number of notches");
        }
        M.setRotors(notches);

        String plugboardSettings = "";
        for (int i = M.numRotors() + 1; i < setUp.size(); i += 1) {
            plugboardSettings += setUp.get(i);
        }
        M.setPlugboard(new Permutation(plugboardSettings, _alphabet));

        M.checkMachineRotors();

    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        msg.replaceAll("\\s", "");
        int perGroup = 5;
        for (int i = 0; i < msg.length(); i += 1) {
            if (perGroup > -1) {
                _output.print(msg.charAt(i));
                perGroup -= 1;
            }
            if (perGroup == 0) {
                perGroup = 5;
                _output.print(' ');
            }
        }
        _output.println();


    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** Number of pawls for current machine. */
    private int _numPawls;

    /** All rotors to be chosen from. */
    private ArrayList<Rotor> _allRotors;

    /** Number of Rotors in Machine. */
    private int _numRotors;


}
