package boldify;

import boldify.standard.StandardParser;

public class Main {
    public static void main(String[] args) {
        // One argument is expected
        allow2Arguments(args);

        String inputPath = args[0];
        String outputPath = args[1];
        new StandardParser(inputPath,  outputPath);
    }

    private static void allow2Arguments(String[] args){
        if(args.length!=2){
            System.err.println("2 arguments expected");
            System.exit(1);
        }
    }
}
