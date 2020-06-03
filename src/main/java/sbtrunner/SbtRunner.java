package sbtrunner;

import sbtrunner.args.HelpAskedException;
import sbtrunner.args.ParsedArgs;
import sbtrunner.args.ParsingArgumentsException;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.*;

public class SbtRunner {

    public static void main(String[] args) {
        Iterator<String> it = Arrays.asList(args).iterator();
        ParsedArgs parsedArgs = null;
        try {
            parsedArgs = ParsedArgs.process(it);
        } catch (HelpAskedException ex) {
            try {
                HelpAskedException.printHelp();
                System.exit(0);
            } catch (IOException ex0) {
                throw new RuntimeException(ex0);
            }
        } catch (ParsingArgumentsException | IOException ex) {
            throw new RuntimeException(ex);
        }

        if (!parsedArgs.isNewOrCreate() &&
                !new File("build.sbt").isFile() &&
                !new File("project").isDirectory()) {
            System.err.println(
                    "[warn] Neither build.sbt nor a 'project' directory in the current directory: " +
                            Paths.get(".").toAbsolutePath().normalize());

            Console console = System.console();

            if (console != null) {
                while (true) {
                    System.err.println("c) continue");
                    System.err.println("q) quit");
                    System.err.print("? ");

                    String input = console.readLine();
                    if (input.equals("c") || input.equals("C")) {
                        break;
                    } else if (input.equals("q") || input.equals("Q")) {
                        System.exit(1);
                    }
                }
            }
        }

        parsedArgs.setSystemProperties();

        Class<?> cls;
        try {
            cls = Class.forName("xsbt.boot.Boot");
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }

        Method main;
        try {
            main = cls.getMethod("main", String[].class);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }

        String[] sbtArgs = parsedArgs.getArgs();

        try {
            main.invoke(null, (Object) sbtArgs);
        } catch (InvocationTargetException ex) {
            Throwable cause = ex.getCause();
            if (cause == null) {
                System.err.println("Something went wrong: " + ex);
                ex.printStackTrace(System.err);
            } else
                throw new RuntimeException(cause);
        } catch (IllegalAccessException ex) {
            System.err.println("Something went wrong: " + ex);
            ex.printStackTrace(System.err);
        }
    }
}
