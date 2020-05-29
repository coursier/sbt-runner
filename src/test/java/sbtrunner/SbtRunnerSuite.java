package sbtrunner;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sbtrunner.args.HelpAskedException;
import sbtrunner.args.ParsedArgs;
import sbtrunner.args.ParsingArgumentsException;
import sbtrunner.args.ScalaVersions;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

class SbtRunnerSuite {

    void help(String arg) {
        boolean gotHelp = false;

        try {
            ParsedArgs.process(Collections.singletonList(arg).iterator());
        } catch (HelpAskedException ex) {
            gotHelp = true;
        } catch (IOException | ParsingArgumentsException ex) {
            throw new RuntimeException(ex);
        }

        Assertions.assertTrue(gotHelp);
    }

    @Test
    void shortHelp() {
        help("-h");
    }

    @Test
    void longHelp() {
        help("-help");
    }

    ParsedArgs parse(String arg) {
        try {
            return ParsedArgs.process(Collections.singletonList(arg).iterator());
        } catch (IOException | ParsingArgumentsException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test
    void javaProperty() {
        ParsedArgs args = parse("-Dfoo=bar");
        Map<String, String> map = args.getPropertiesAsMap();
        Assertions.assertEquals("bar", map.get("foo"));
    }

    @Test
    void scala213() {
        ParsedArgs args = parse("-213");
        Assertions.assertArrayEquals(new String[]{"++ " + ScalaVersions.latest213}, args.getArgs());
    }

}
