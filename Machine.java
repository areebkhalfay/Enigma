package enigma;

import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * Class that represents a complete enigma machine.
 *
 * @author Areeb Khalfay
 */
class Machine {

    /**
     * A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     * and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     * available rotors.
     */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _numPawls = pawls;
        for (Rotor r : allRotors) {
            _allRotors.put(r.name().toUpperCase(), r);
        }
    }

    /**
     * Return the number of rotor slots I have.
     */
    int numRotors() {
        return _numRotors;
    }

    /**
     * Return the number pawls (and thus rotating rotors) I have.
     */
    int numPawls() {
        return _numPawls;
    }

    /**
     * Return the plugBoard.
     */
    public Permutation getplugBoard() {
        return _plugBoard;
    }

    /**
     * Set my rotor slots to the rotors named ROTORS from my set of
     * available rotors (ROTORS[0] names the reflector).
     * Initially, all rotors are set at their 0 setting.
     */
    void insertRotors(String[] rotors) {
        ArrayList<String> newRotors = new ArrayList<>();
        for (String preRotor : rotors) {
            newRotors.add(preRotor.toUpperCase());
        }
        for (String newRotor : newRotors) {
            for (HashMap.Entry<String, Rotor> rotor : _allRotors.entrySet()) {
                String key = rotor.getKey();
                if (newRotor.equals(key)) {
                    _rotorsOrdered.add(rotor.getValue());
                }
            }
        }
    }

    /**
     * Set my rotors according to SETTING, which must be a string of
     * numRotors()-1 upper-case letters. The first letter refers to the
     * leftmost rotor setting (not counting the reflector).
     */
    void setRotors(String setting) {
        assert setting.length() == numRotors() - 1;
        for (int count = 0; count < setting.length(); count++) {
            char ch = setting.charAt(count);
            _settings.add(ch);
        }
        for (int index = 1; index < numRotors(); index++) {
            char setting2 = _settings.get(index - 1);
            _rotorsOrdered.get(index).set(setting2);
        }
    }

    /**
     * Set the plugboard to PLUGBOARD.
     */
    void setPlugboard(Permutation plugBoard) {
        _plugBoard = plugBoard;
    }

    /**
     * Returns the result of converting the input character C (as an
     * index in the range 0..alphabet size - 1), after first advancing
     * the machine.
     */
    int convert(int c) {
        int cPreliminary = c;
        for (int i = numRotors() - 1; i > numPawls() - 1; i--) {
            if (_rotorsOrdered.get(i).atNotch()) {
                _rotorsOrdered.get(i - 1).advance();
            }
            if (_rotorsOrdered.get(i - 1).atNotch()) {
                _rotorsOrdered.get(i - 1).advance();
                _rotorsOrdered.get(i - 2).advance();
            }
        }
        _rotorsOrdered.get(_numRotors - 1).advance();

        if (_plugBoard != null) {
            cPreliminary = _plugBoard.permute(c);
        }
        for (int index = numRotors() - 1; index > -1; index--) {
            cPreliminary = _rotorsOrdered.get(index)
                    .convertForward(cPreliminary);
        }
        for (int index = 1; index < numRotors(); index++) {
            cPreliminary = _rotorsOrdered.get(index)
                    .convertBackward(cPreliminary);
        }
        if (_plugBoard != null) {
            cPreliminary = _plugBoard.permute(cPreliminary);
        }
        return cPreliminary;
    }

    /**
     * Returns the encoding/decoding of MSG, updating the state of
     * the rotors accordingly.
     */
    String convert(String msg) {
        Scanner message = new Scanner(msg).useDelimiter("");
        StringBuilder convertedString = new StringBuilder();
        while (message.hasNext()) {
            String buffer = message.next();
            if (buffer.equals(" ")) {
                convertedString.append(" ");
            } else {
                int next = _alphabet.toInt(buffer.charAt(0));
                convertedString.append(_alphabet.toChar(convert(next)));
            }
        }
        String convertedmessage = convertedString.toString();
        return convertedmessage;
    }

    /**
     * Common alphabet of my rotors.
     */
    private final Alphabet _alphabet;

    /**
     * Number of Rotors in this Enigma Machine Object.
     */
    private int _numRotors;

    /**
     * Number of Pawls in this Enigma Machine Object.
     */
    private int _numPawls;

    /**
     * HashMap containing all available Rotors.
     */
    private HashMap<String, Rotor> _allRotors = new HashMap<>();

    /**
     * ArrayList of inserted Rotors in order.
     */
    private ArrayList<Rotor> _rotorsOrdered = new ArrayList<>();

    /**
     * Settings for the Rotors.
     */
    private ArrayList<Character> _settings = new ArrayList<>();

    /**
     * PlugBoard Permuation.
     */
    private Permutation _plugBoard;

}
