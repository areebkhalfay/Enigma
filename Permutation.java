package enigma;

import static enigma.EnigmaException.*;

import java.util.Scanner;
import java.util.ArrayList;

/**
 * Represents a permutation of a range of integers starting at 0 corresponding
 * to the characters of an alphabet.
 *
 * @author Areeb Khalfay
 */
class Permutation {

    /**
     * Set this Permutation to that specified by CYCLES, a string in the
     * form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     * is interpreted as a permutation in cycle notation.  Characters in the
     * alphabet that are not included in any cycle map to themselves.
     * Whitespace is ignored.
     */

    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        listOfCycles(cycles, _cycles);
    }

    /**
     * Helper Method to instantiate the list of cycles.
     * @param cycles String
     * @param cyclesArrayList ArrayList<String>
     * @returns ArrayList
     */
    ArrayList<String> listOfCycles(String cycles,
                                   ArrayList<String> cyclesArrayList) {
        Scanner scannerCycles = new Scanner(cycles).useDelimiter("[(]");
        int x = 0;
        while (scannerCycles.hasNext()) {
            String str = scannerCycles.next().replace("(", "").
                    replace(")", "").trim();
            _cycles.add(x, str);
            x++;
        }
        Character[] alphabet = new Character[]{'A', 'B', 'C', 'D',
            'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
            'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        for (Character newchar: alphabet) {
            if (_alphabet.contains(newchar)) {
                if (!cycles.contains(Character.toString(newchar))) {
                    _cycles.add(Character.toString(newchar));
                }
            }
        }
        return _cycles;
    }

    /**
     * Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     * c0c1...cm.
     */
    private void addCycle(String cycle) {
        _cycles.add(cycle);
    }

    /**
     * Return the value of P modulo the size of this permutation.
     */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /**
     * Returns the size of the alphabet I permute.
     */
    int size() {
        return _alphabet.size();
    }

    /**
     * Return the result of applying this permutation to P modulo the
     * alphabet size.
     */
    int permute(int p) {
        int checkedInput = wrap(p);
        char inputChar = _alphabet.toChar(checkedInput);
        char outputChar = permute(inputChar);
        int outputInt = _alphabet.toInt(outputChar);
        return outputInt;
    }

    /**
     * Return the result of applying the inverse of this permutation
     * to  C modulo the alphabet size.
     */
    int invert(int c) {
        int checkedInput = wrap(c);
        char inputChar = _alphabet.toChar(checkedInput);
        char outputChar = invert(inputChar);
        int outputInt = _alphabet.toInt(outputChar);
        return outputInt;
    }

    /**
     * Return the result of applying this permutation to the index of P
     * in ALPHABET, and converting the result to a character of ALPHABET.
     */
    char permute(char p) {
        if (_cycles.isEmpty()) {
            return p;
        }
        String stringP = Character.toString(p);
        for (String x : _cycles) {
            if (x.contains(stringP)) {
                int currentindex = x.indexOf(stringP);
                if (currentindex + 1 == x.length()) {
                    return x.charAt(0);
                } else {
                    return x.charAt(currentindex + 1);
                }
            }
        }
        return p;
    }

    /**
     * Return the result of applying the inverse of this permutation to C.
     */
    char invert(char c) {
        String stringP = Character.toString(c);
        for (String x : _cycles) {
            if (x.contains(stringP)) {
                int currentindex = x.indexOf(c);
                if (currentindex == 0) {
                    return x.charAt(x.length() - 1);
                } else {
                    return x.charAt(currentindex - 1);
                }
            }
        }
        return c;
    }

    /**
     * Return the alphabet used to initialize this Permutation.
     */
    Alphabet alphabet() {
        return _alphabet;
    }

    /**
     * Return true iff this permutation is a derangement (i.e., a
     * permutation for which no value maps to itself).
     */
    boolean derangement() {
        for (String x : _cycles) {
            if (x.length() == 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Alphabet of this permutation.
     */
    private Alphabet _alphabet;

    /**
     * ArrayList of Cycles of this permutation.
     */
    private ArrayList<String> _cycles = new ArrayList<>();
}
