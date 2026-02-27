package boldify.standard;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class StandardParser {

    public StandardParser() {
        // Create scanner and recieve input for path to the PDF
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter PDF file path: ");
        String filePath = sc.nextLine();

        // Load the PDF
        try(PDDocument document = Loader.loadPDF(new File(filePath))) {
            // Check if it has pages
            System.out.println(document.getPages());
        } catch(IOException e) {
            System.err.println("Error opening PDF file: " + e.getMessage());
        }

        // Done for now
        System.out.print("Program terminating...");
    }
}
