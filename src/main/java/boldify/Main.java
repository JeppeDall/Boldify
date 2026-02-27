package boldify;

import boldify.standard.StandardParser;

public class Main {
    public static void main(String[] args) {
        // No arguments are expected
        allow1Argument(args);

        String arg = args[0];

        StandardParser parser = switch (arg) {
            case "standard" -> new StandardParser();
            default -> throw new RuntimeException("Not a valid version");
        };
    }

    private static void allow1Argument(String[] args){
        if(args.length!=1){
            System.err.println("No arguments expected");
            System.exit(1);
        }
    }
}
