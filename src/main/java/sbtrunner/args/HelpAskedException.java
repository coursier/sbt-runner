package sbtrunner.args;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class HelpAskedException extends ParsingArgumentsException {

    static final String helpResourcePath = "sbtrunner/help.txt";

    public static void printHelp() throws IOException {
        try (InputStream is = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(helpResourcePath)) {
            if (is == null) {
                System.err.println("[" + helpResourcePath + " resource not found]");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("#"))
                    System.out.println(line);
            }
        }
    }

    public HelpAskedException() {
        super(null, 0);
    }
}
