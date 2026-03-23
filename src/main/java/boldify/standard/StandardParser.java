package boldify.standard;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentNameDestinationDictionary;
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
            // Print the text extracted from the PDF
            processPDF(document);
        } catch(IOException e) {
            System.err.println("Error opening PDF file: " + e.getMessage());
        }

        // Done for now
        System.out.print("Program terminating...");
    }

    /** Boldify the document using a for-loop which handles a single page at a time to minimize memory usage
     *
     * @param document The document to process
     * @throws IOException
     */
    public void processPDF(PDDocument document) throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        // Loop through pages, calling the boldify algorithm on each page
        int numberOfPages = document.getNumberOfPages();
        for (int i = 0; i < numberOfPages; i++) {
            // Process only 1 page at a time
            // i is 1-indexed in the library
            stripper.setStartPage(i+1);
            stripper.setEndPage(i+1);
            // Get the text from page i
            String text = stripper.getText(document);
            // Call boldify algorithm
            processPage(text, i+1);
        }
    }

    /** Helper function to handle editing of text in a PDF.
     * Currently Fake-it. Should implement some boldify algorithm
     * @param text
     * @param pageNumber
     * @throws IOException
     */
    private void processPage(String text, int pageNumber) throws IOException {
        System.out.println("Processing page " + pageNumber);
        System.out.println("Text content of the page: \n" + text);
    }
}
