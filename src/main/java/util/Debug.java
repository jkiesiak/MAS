package util;


/**
 *  A class for Debugging that allows for output on the console.
 *  That output can be switched on or off.
 */

public class Debug {

    /**  Do we want to allow normal messages to be printed on the console? */
    public static final boolean allowMessages = false;
    /**  Do we want to allow alert-messages to be printed on the console? */
    public static final boolean allowAlerts = true;
    /**  Do we want to allow messages for instant debugging to be printed on the console? */
    public static final boolean allowTestMessages = true;

    /**
     *  Prints a message on the console
     *
     * @param  caller   The object from which the message is issued
     * @param  message  the message itself
     */
    public static void print(Object caller, String message) {
        if (allowMessages) {
            System.out.println("**MSG** [from " + caller + "] :: " + message);
        }
    }

    /**
     *  Prints an alert-message on the console
     *
     * @param  caller   The object from which the alert-message is issued
     * @param  message  the alert-message itself
     */
    public static void alert(Object caller, String message) {
        if (allowAlerts) {
            //System.out.println("*********");
            System.err.println("**ALERT** [from " + caller + "] :: " + message);
            //System.out.println("*********");
        }
    }

    /**
     *  Prints a message on the console. For instant debugging purposes only.
     *
     * @param  caller   The object from which the message is issued
     * @param  message  the message itself
     */
    public static void test(Object caller, String message) {
        if (allowTestMessages) {
            System.err.println("**DEBUG** [from " + caller + "] :: " + message);
        }
    }
}
