package sbtrunner.args;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class ParsedArgs {

    private final String[] args;
    private final List<Map.Entry<String, String>> properties;

    ParsedArgs(String[] args, List<Map.Entry<String, String>> properties) {
        this.args = args;
        this.properties = properties;
    }

    public static ParsedArgs of(String[] args, List<Map.Entry<String, String>> properties) {
        return new ParsedArgs(args, properties);
    }


    static String nextArgument(Iterator<String> input, String type, String opt) throws ParsingArgumentsException {
        String arg;

        if (!input.hasNext() || (arg = input.next()).startsWith("-")) {
            throw new MissingArgumentException(type, opt);
        }

        return arg;
    }

    static List<String> readOptionFile(File file) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath());
        ArrayList<String> args = new ArrayList<>();
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                int idx = line.indexOf('#');
                String arg;
                if (idx >= 0)
                    arg = line.substring(0, idx);
                else
                    arg = line;
                arg = arg.trim();

                if (!arg.isEmpty()) {
                    args.add(arg);
                }
            }
        }
        return args;
    }

    public static ParsedArgs process(Iterator<String> input) throws IOException, ParsingArgumentsException {
        ArrayList<String> remainingArgs = new ArrayList<>();
        ArrayList<Map.Entry<String, String>> properties = new ArrayList<>();
        File optFile = null;
        ArrayList<String> scalacArgs = new ArrayList<>();

        // trying to emulate the parser from https://github.com/paulp/sbt-extras/blob/e252487b9bfdea6a6c520a13a13bb780dc9394fe/sbt#L430-L478
        while (input.hasNext()) {
            String arg = input.next();

            if (arg.equals("-h") || arg.equals("-help")) {
                throw new HelpAskedException();
            } else if (arg.startsWith("-D")) {
                String content = arg.substring(2);
                int idx = content.indexOf('=');
                String key;
                String value;
                if (idx >= 0) {
                    key = content.substring(0, idx);
                    value = content.substring(idx + 1);
                } else {
                    key = content;
                    value = "";
                }
                properties.add(new AbstractMap.SimpleEntry<>(key, value));
            } else if (arg.equals("-d")) {
                remainingArgs.add("--debug");
            } else if (arg.equals("-w")) {
                remainingArgs.add("--warn");
            } else if (arg.equals("-q")) {
                remainingArgs.add("--error");
            } else if (arg.equals("-trace")) {
                String traceLevel = nextArgument(input, "integer", "-trace");
                remainingArgs.add("set traceLevel in ThisBuild := \"" + traceLevel + "\"");
            } else if (arg.equals("-debug-inc")) {
                properties.add(new AbstractMap.SimpleEntry<>("xsbt.inc.debug", "true"));
            } else if (arg.equals("-no-colors")) {
                properties.add(new AbstractMap.SimpleEntry<>("sbt.log.noformat", "true"));
            } else if (arg.equals("-sbt-dir")) {
                properties.add(new AbstractMap.SimpleEntry<>("sbt.global.base", nextArgument(input, "path", "-sbt-dir")));
            } else if (arg.equals("-sbt-boot")) {
                properties.add(new AbstractMap.SimpleEntry<>("sbt.boot.directory", nextArgument(input, "path", "-sbt-boot")));
            } else if (arg.equals("-ivy")) {
                properties.add(new AbstractMap.SimpleEntry<>("sbt.ivy.home", nextArgument(input, "path", "-ivy")));
            } else if (arg.equals("-no-share")) {
                properties.add(new AbstractMap.SimpleEntry<>("sbt.global.base", "project/.sbtboot"));
                properties.add(new AbstractMap.SimpleEntry<>("sbt.boot.directory", "project/.boot"));
                properties.add(new AbstractMap.SimpleEntry<>("sbt.ivy.home", "project/.ivy"));
            } else if (arg.equals("-offline")) {
                remainingArgs.add("set offline in Global := true");
            } else if (arg.equals("-prompt")) {
                String prompt = nextArgument(input, "expr", "-prompt");
                remainingArgs.add("set shellPrompt in ThisBuild := (s => { val e = Project.extract(s) ; \"" + prompt + "\" })");
            } else if (arg.equals("-script")) {
                properties.add(new AbstractMap.SimpleEntry<>("sbt.main.class", "sbt.ScriptMain"));
                remainingArgs.add(0, nextArgument(input, "file", "-script"));
            } else if (arg.equals("-sbt-opts")) {
                optFile = new File(nextArgument(input, "path", "-sbt-opts"));
            } else if (arg.equals("-28")) {
                remainingArgs.add("++ " + ScalaVersions.latest28);
            } else if (arg.equals("-29")) {
                remainingArgs.add("++ " + ScalaVersions.latest29);
            } else if (arg.equals("-210")) {
                remainingArgs.add("++ " + ScalaVersions.latest210);
            } else if (arg.equals("-211")) {
                remainingArgs.add("++ " + ScalaVersions.latest211);
            } else if (arg.equals("-212")) {
                remainingArgs.add("++ " + ScalaVersions.latest212);
            } else if (arg.equals("-213")) {
                remainingArgs.add("++ " + ScalaVersions.latest213);
            } else if (arg.equals("-scala-version")) {
                String scalaVersion = nextArgument(input, "version", "-scala-version");
                if (scalaVersion.endsWith("-SNAPSHOT")) {
                    remainingArgs.add("set resolvers += Resolver.sonatypeRepo(\"snapshots\")");
                }
                remainingArgs.add("++ " + scalaVersion);
            } else if (arg.equals("-binary-version")) {
                String scalaBinaryVersion = nextArgument(input, "version", "-binary-version");
                remainingArgs.add("set scalaBinaryVersion in ThisBuild := \"" + scalaBinaryVersion + "\"");
            } else if (arg.equals("-scala-home")) {
                String home = nextArgument(input, "path", "-scala-home");
                remainingArgs.add("set scalaHome in ThisBuild := _root_.scala.Some(file(\"" + home + "\"))");
            } else if (arg.startsWith("-S")) {
                String scalacArg = arg.substring(2);
                scalacArgs.add(scalacArg);
            } else {
                remainingArgs.add(arg);
            }
        }

        if (scalacArgs.size() > 0) {
            StringBuilder b = new StringBuilder("set scalacOptions in ThisBuild += \"");
            boolean isFirst = true;
            for (String arg : scalacArgs) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    b.append(" ");
                }
                b.append(arg);
            }
            b.append("\"");
            remainingArgs.add(b.toString());
        }

        if (optFile != null) {
            List<String> fromFile = readOptionFile(optFile);
            remainingArgs.addAll(fromFile);
        }

        return ParsedArgs.of(remainingArgs.toArray(new String[0]), properties);
    }




    public String[] getArgs() {
        return args;
    }

    public List<Map.Entry<String, String>> getProperties() {
        return properties;
    }

    public Map<String, String> getPropertiesAsMap() {
        HashMap<String, String> map = new HashMap<>();
        for (Map.Entry<String, String> entry : properties) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    public void setSystemProperties() {
        for (Map.Entry<String, String> entry : properties) {
            String key = entry.getKey();
            String value = entry.getValue();
            System.setProperty(key, value);
        }
    }
}
