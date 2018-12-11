package enigma;

import static enigma.EnigmaException.*;

/** Superclass that represents a rotor in the enigma machine.
 *  @author Areeb Khalfay
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        _position = 0;
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        return _position;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        _position = posn;
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        _position = alphabet().toInt(cposn);
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        int inputandsetting = _position + p;
        int wrappedinput = _permutation.wrap(inputandsetting);
        int permutedinput = _permutation.permute(wrappedinput);
        int outputandsetting = permutedinput - _position;
        int wrappedoutput = _permutation.wrap(outputandsetting);
        return wrappedoutput;
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        int inputandsetting = _position + e;
        int wrappedinput = _permutation.wrap(inputandsetting);
        int inverseinput = _permutation.invert(wrappedinput);
        int outputandsetting = inverseinput - _position;
        int wrappedoutput = _permutation.wrap(outputandsetting);
        return wrappedoutput;
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;

    /** The position of this rotor ie. the
     * setting of the rotor at given point in time, 0 by default. */
    private int _position;
}
