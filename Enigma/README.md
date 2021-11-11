**Relevant Classes**

Alphabet.java - Represents all the characters that are to be used in the Machine

Permutation.java - Represents the mappings of characters to other characters

PermutationTest.java - Contains unit tests for Permutation.java

Rotor.java - Represents a single rotor of the Enigma machine
- FixedRotor.java - Represents a rotor that does not rotate
-   Reflector.java - Represents a rotor that maps a letter to itself (not another rotor) 
- MovingRotor.java - Represents a rotor that can rotate

Rotor.Test - Contains unit tests for Rotor.java

MovingRotorTest - Contains unit tests for MovingRotor.java

Machine.java - Represents a group of rotors that work together to encrypt/decrypt a string

Main.java - Parses an input file containing the settings of the Machine to set up the Machine and proceed to encrypt/decrypt a string
