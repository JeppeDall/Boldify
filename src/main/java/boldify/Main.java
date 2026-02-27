package boldify;

public class Main {
    public static void main(String[] args) {
        allow1Argument(args);
        String fileToProcess = args[0];


        // Succesfull for now
        System.out.println(fileToProcess);
    }

    private static void allow1Argument(String[] args){
        if(args.length!=1){
            System.err.println("Only 1 argument expected: path to PDF file");
            System.exit(1);
        }
    }
}
