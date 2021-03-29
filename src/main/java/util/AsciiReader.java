/*
 * Author: Olivier Glorieux
 * dec 2000
 */

package util;

import java.io.*;
import java.lang.reflect.Constructor;

@SuppressWarnings({"rawtypes", "unchecked"})
public class AsciiReader {
    protected StreamTokenizer st;
    private final BufferedReader br;

    // Constructor
    public AsciiReader(String f) throws FileNotFoundException {
        // Default state ok ?
        br = new BufferedReader(new FileReader(f));
        st = new StreamTokenizer(br);
        st.resetSyntax();
        st.commentChar('#');
        st.quoteChar('"');
        st.wordChars('0', '9');
        st.wordChars('-', '-');
        st.wordChars('\\', '\\');
        st.wordChars('/', '/');
        st.wordChars('_', '_');
        st.wordChars('.', '.');
        st.wordChars('a', 'z');
        st.wordChars('A', 'Z');
        st.whitespaceChars(' ', ' ');
        st.whitespaceChars('\t', '\t');
        st.whitespaceChars('\r', '\r');
        st.whitespaceChars('\n', '\n');
        st.whitespaceChars(':', ':');
        st.eolIsSignificant(false);
    }

    // Read operations
    public String readNext() throws IOException {
        st.nextToken();
        return st.sval;
    }

    public int readInt() throws IOException {
        String str = readNext();
        return Integer.parseInt(str);
    }

    public double readDouble() throws IOException {
        String str = readNext();
        return Double.parseDouble(str);
    }

    public float readFloat() throws IOException {
        String str = readNext();
        return Float.parseFloat(str);
    }

    //verification operations
    /**
     * Reads a token from the stream and checks if it matches the argument
     * @param verify : the token to be verified
     * @post returns if verify matches the read token
     * @throws IOException is thrown when it is no match
     */
    public void check(String verify) throws IOException {
        String token = readNext();
        if (! (token).equals(verify)) {
            throw new IOException(verify + " expected but " + token + " found.");
        }
    }

    /**
     * @semantics Reads a Class constructor with its arguments from file and initializes a new object.
     * FileFormat: <classname> nbArgs <int> [<argType><value>]*
     * 			   argType must be String, Int, Float or Double
     */
    public Object readClassConstructor() throws IOException {
        String classname = readNext();

        check("nbArgs");
        int nbArgs = readInt();
        Class[] args = new Class[nbArgs];
        Object[] values = new Object[nbArgs];

        for (int i = 0; i < nbArgs; i++) {
            values[i] = readTypeAndValue();
            switch (values[i].getClass().getName()) {
                case "java.lang.Integer":
                    args[i] = Integer.TYPE;
                    break;
                case "java.lang.Double":
                    args[i] = Double.TYPE;
                    break;
                case "java.lang.Float":
                    args[i] = Float.TYPE;
                    break;
                default:
                    args[i] = values[i].getClass();
                    break;
            }
        }

        Object result = null;
        try {
            Class c = Class.forName(classname);
            Constructor con = c.getConstructor(args);
            result = con.newInstance(values);
        } catch (ClassNotFoundException e) {
            System.err.println("ReadClassConstructor failed for " + classname +
                               " error: class not found " + e.getMessage());
        } catch (Exception e) {
            System.err.println("ReadClassConstructor failed for " + classname +
                               " error: " + e.getMessage());
        }
        return result;
    }

    public Object readTypeAndValue() throws IOException {
        String type = readNext();
        switch (type) {
            case "String":
                return readNext();
            case "Integer":
                return readInt();
            case "Float":
                return readFloat();
            case "Double":
                return readDouble();
            default:
                return null;
        }
    }

    //terminator
    public void close() {
        try {
            br.close();
        } catch (IOException e) {
            System.err.println("Could not close AsciiReader: " + e.getMessage());
        }
    }

}
