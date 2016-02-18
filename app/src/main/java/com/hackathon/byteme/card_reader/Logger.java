package com.hackathon.byteme.card_reader;

/**
 * Created by bighoon on 2016-02-18.
 */
public final class Logger {
    static boolean debug = true;
    static boolean fullStackTrace = false;

    private Logger(){}

    /**
     * Prints the msg out to stdout with a stack trace if debug is true
     * Prints 1 line of calling stack trace or full stack depending on internal boolean value
     * @param msg - string to be printed / logged
     */
    public static void print(String msg)
    {
        if (debug)
        {
            System.out.println("LOGGER MESSAGE - { " + msg + " }");
            StackTraceElement traces[] = Thread.currentThread().getStackTrace();
            if (fullStackTrace)
            {
                for(StackTraceElement e : traces)
                System.out.println("\ttrace - " + e.toString());
            }
            else {
                System.out.println("\ttrace - " + traces[traces.length - 2].toString());
                System.out.println("\ttrace - " + traces[traces.length - 3].toString());
            }
        }
    }
}
