package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Areeb Khalfay
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
    void advance() {
        super.set(permutation().wrap(setting() + 1));
    }

    @Override
    boolean atNotch() {
        String positionString =
                Character.toString(alphabet().toChar(super.setting()));
        if (_notches.substring(1).contains(positionString)) {
            return true;
        }
        return false;
    }

    @Override
    boolean rotates() {
        return true;
    }
    /** String of the notches for given Moving Rotor. */
    private String _notches;

}
