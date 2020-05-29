package sbtrunner.args;

public class MissingArgumentException extends ParsingArgumentsException {

    private final String type;
    private final String opt;

    public MissingArgumentException(String type, String opt) {
        super(opt + " requires <" + type + "> argument", 1);
        this.type = type;
        this.opt = opt;
    }

    public String getType() {
        return type;
    }
    public String getOpt() {
        return opt;
    }
}
