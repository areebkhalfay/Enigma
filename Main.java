package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Areeb Khalfay
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
            _helperinput = getInput(args[1]);
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
    public void process() {
        Machine enigma = readConfig();
        if (!_input.hasNext("\\*")) {
            throw new EnigmaException("This is an incorrect config file.");
        }
        String settings = _input.nextLine();
        String holder =_helperinput.nextLine();
        setUp(enigma, settings);
        if (!_input.hasNextLine()) {
            throw new EnigmaException("There is no more input to parse.");
        }
        while (_helperinput.hasNext("\\n")) {
            _input.nextLine();
            _helperinput.nextLine();
            _output.println(" ");
        }
        while (_input.hasNextLine() && !_input.hasNext("\\*")) {
            String newline = _input.nextLine().toUpperCase();
            if (newline.equals("")){
                _output.println(" ");
            }
            else {
                newline = enigma.convert(newline);
            }
            printMessageLine(newline);
            _helperinput.nextLine();
        }
        String buff;
        if (!_input.hasNextLine()) {
            throw new EnigmaException("No more input.");
        }
        buff = _input.nextLine();
        setUp(enigma, settings);
        int count = 0;
        while (_helperinput.hasNext("\\*")) {
            _helperinput.nextLine();
            count++;
        }
        if (buff.equals("")) {
            _output.println(" ");
        }
        while (_helperinput.hasNextLine() && !_helperinput.hasNext("\\*")) {
            String newline = enigma.convert
                    (_helperinput.nextLine().toUpperCase());
            printMessageLine(newline);
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. Used Stack Overflow for else if the first if else.*/
    public Machine readConfig() {
        try {
            if (_config.hasNext("[A-Z][-][A-Z]")) {
                String alphabetConfig = (_config.next());
                Scanner alphabetScanner = new
                        Scanner(alphabetConfig).useDelimiter("-");
                ArrayList<Character> alphabetChars = new ArrayList<>();
                while (alphabetScanner.hasNext()) {
                    Character alphabetBuffer = alphabetScanner.next().charAt(0);
                    alphabetChars.add(alphabetBuffer);
                }
                Character firstChar = alphabetChars.get(0);
                Character lastChar = alphabetChars.get(1);
                _alphabet = new CharacterRange(firstChar, lastChar);
            } else {
                String name = _config.next();
                int greatestIndex = 0;
                for (int i = 0; i < name.length(); i++) {
                    int current = (int) name.charAt(i);
                    if (current > greatestIndex) {
                        greatestIndex = current;
                    }
                }
                char smallestChar = name.charAt(0);
                for (int i = 1; i < name.length(); ++i) {
                    if (name.charAt(i) < smallestChar) {
                        smallestChar = name.charAt(i);
                    }
                }
                Character firstChar = smallestChar;
                Character lastChar = (char) greatestIndex;
                _alphabet = new CharacterRange(firstChar, lastChar);
            }
            String numRotorsString = _config.next();
            int numRotors = Integer.parseInt(numRotorsString);
            String numPawlsString = _config.next();
            int numPawls = Integer.parseInt(numPawlsString);
            ArrayList<Rotor> allRotors = new ArrayList<>();
            while (_config.hasNext()) {
                allRotors.add(readRotor());
            }
            return new Machine(_alphabet, numRotors, numPawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String rotorName = _config.next();
            String rotorTypeAndNotches = _config.next();
            ArrayList<String> cycles = new ArrayList<>();
            String temp = _config.next();
            String buffer = temp;
            cycles.add(temp);
            while (temp.contains("(") || buffer.contains("(")) {
                if (!_config.hasNext("[(][A-Z]+[)]")) {
                    break;
                }
                buffer = _config.next();
                if (buffer.contains("(")) {
                    cycles.add(buffer);
                }
                temp = buffer;
            }
            StringBuilder cyclesBuffer = new StringBuilder();
            for (String x : cycles) {
                cyclesBuffer.append(x + " ");
            }
            String cyclesString = cyclesBuffer.toString();
            Permutation permbuff = new Permutation(cyclesString, _alphabet);
            if (rotorTypeAndNotches.substring(0, 1).equals("M")) {
                return new MovingRotor(rotorName,
                        permbuff, rotorTypeAndNotches);
            } else if (rotorTypeAndNotches.substring(0, 1).equals("N")) {
                return new FixedRotor(rotorName, permbuff);
            } else if (rotorTypeAndNotches.substring(0, 1).equals("R")) {
                return new Reflector(rotorName, permbuff);
            } else {
                throw error("This is not an acceptable rotor type.");
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    public void setUp(Machine M, String settings) {
        Scanner settingsScanner = new Scanner(settings);
        if (settingsScanner.hasNext("\\*")) {
            settingsScanner.next();

        }
        String[] rotors = new String[M.numRotors()];
        for (int i = 0; i < M.numRotors(); i++) {
            String next = settingsScanner.next();
            rotors[i] = next;
        }
        M.insertRotors(rotors);
        M.setRotors(settingsScanner.next());
        while (settingsScanner.hasNext("[(][A-Z]+[)]")) {
            String temp = settingsScanner.next();
            String buffer = temp;
            ArrayList<String> cycles = new ArrayList<>();
            while (temp.contains("(") || buffer.contains("(")) {
                if (!settingsScanner.hasNext("[(][A-Z]+[)]")) {
                    cycles.add(buffer);
                    break;
                }
                if (buffer.contains("(")) {
                    cycles.add(buffer);
                }
                buffer = settingsScanner.next();
                temp = buffer;
            }
            StringBuilder cyclesBuffer = new StringBuilder();
            for (String x : cycles) {
                cyclesBuffer.append(x + " ");
            }
            String cyclesString = cyclesBuffer.toString();
            Permutation permbuff = new Permutation(cyclesString, _alphabet);
            M.setPlugboard(permbuff);
        }
        if (M.getplugBoard() == null) {
            M.setPlugboard(null);
        }

    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String helper = msg.replaceAll("\\s", "").toUpperCase();
        for (int i = 0; i < helper.length(); i += 5) {
            int limit = helper.length() - i;
            if (limit > 5) {
                _output.print(helper.substring(i, i + 5) + " ");
            } else {
                _output.println(helper.substring(i, i + limit));
            }
        }
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** Second input. */
    private Scanner _helperinput;

}
