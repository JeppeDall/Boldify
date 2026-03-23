package boldify.standard;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class StandardParser {

    public StandardParser() {
        // Create scanner and recieve input for path to the PDF
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter PDF file path: ");
        // Expecting something like: C:\Users\Jeppe\OneDrive\Skrivebord\test.pdf
        String filePath = sc.nextLine();

        // Load the PDF
        try(PDDocument document = Loader.loadPDF(new File(filePath))) {
            // Print the number of pages
            System.out.println("Number of pages in given document is: " + document.getNumberOfPages());

            // Create object for extracting text
            PDFTextStripper stripper = new PDFTextStripper();
            // Get the text from all pages
            System.out.println(stripper.getText(document));

        } catch(IOException e) {
            System.err.println("Error opening PDF file: " + e.getMessage());
        }

        // Done for now
        System.out.print("Program terminating...");
    }
}
