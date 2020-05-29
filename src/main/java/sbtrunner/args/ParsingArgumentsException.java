package sbtrunner.args;

public abstract class ParsingArgumentsException extends Exception {

    private final int exitCode;

    public ParsingArgumentsException(String message, int exitCode) {
        super(message);
        this.exitCode = exitCode;
    }

    public int getExitCode() {
        return exitCode;
    }
}
