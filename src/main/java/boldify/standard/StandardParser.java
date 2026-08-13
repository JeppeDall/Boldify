package boldify.standard;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class StandardParser {

    public StandardParser() {
        String inputPath = "C:\\Users\\Jeppe\\OneDrive\\Skrivebord\\test.pdf";

        // Load the PDF and boldify it
        try(PDDocument inputDocument = Loader.loadPDF(new File(inputPath))) {
            // Create a output PDF to insert processed pages
            PDDocument outputDocument = new PDDocument();

            // Process the input PDF
            processPDF(inputDocument, outputDocument);

            // Save the new PDF to a given filepath
            String outputPath = "C:\\Users\\Jeppe\\OneDrive\\Skrivebord\\testOutput.pdf";
            outputDocument.save(new File(outputPath));
            outputDocument.close();
        } catch(IOException e) {
            System.err.println("Error opening PDF file: " + e.getMessage());
        }

        // Done for now
        System.out.print("Successfully parsed PDF file :)");
    }

    /** Boldify the document using a for-loop which handles a single page at a time to minimize memory usage
     *
     * @param inputdocument The document to process
     * @param outputDocument The document to save changes in
     * @throws IOException
     */
    public void processPDF(PDDocument inputdocument, PDDocument outputDocument) throws IOException {
        // Create a stripper for reading input documents contents
        PDFTextStripper stripper = new PDFTextStripper();

        // Loop through pages, calling the boldify algorithm on one page at a time
        int numberOfPages = inputdocument.getNumberOfPages();
        for (int i = 0; i < numberOfPages; i++) {
            // Process only 1 page at a time
            // i is 1-indexed in the library
            stripper.setStartPage(i+1);
            stripper.setEndPage(i+1);
            // Get the text from page i
            String text = stripper.getText(inputdocument);

            // Copy the font from the original page
            PDPage page = inputdocument.getPage(i);
            PDFont font = copyFont(outputDocument, page);

            // Call boldify algorithm
            processPage(text, outputDocument, font);
        }
    }

    /** Copy the font used on the page as the FOnt for the outputdocument.
     * If no font is found, teh default will be Helvetica
     *
     * @param outputDocument The document the font will be embedded into
     * @param page The source page to inspect
     * @throws IOException
     */
    private PDFont copyFont(PDDocument outputDocument, PDPage page) throws IOException {
        PDResources resources = page.getResources();
        if (resources != null) {
            for (COSName fontName : resources.getFontNames()) {
                PDFont sourceFont = resources.getFont(fontName);
                PDFontDescriptor descriptor = sourceFont.getFontDescriptor();

                if (descriptor != null) {
                    PDStream fontFile2 = descriptor.getFontFile2(); // embedded TrueType
                    if (fontFile2 != null) {
                        try (InputStream in = fontFile2.createInputStream()) {
                            return PDType0Font.load(outputDocument, in);
                        }
                    }
                }
                break; // only look at the first font on the page
            }
        }
        // Default to Helvetica if no font is found
        return new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    }

    /** Helper function to handle editing of text in a PDF.
     * Currently Fake-it. Should implement some boldify algorithm
     * @param text
     * @param font The font to use in output
     * @throws IOException
     */
    private void processPage(String text, PDDocument outputDocument, PDFont font) throws IOException {
        // Currently using input text as output text. Should have algorithm applied
        String editedText = text;
        addToPDF(editedText, outputDocument, font);
    }

    /** Add the given text to a PDDDocument, by creating a new page
     *
     * @param text The text to insert into a new page. Expected to be able to fit on a single page (currently)
     * @param outputDocument The document to insert into
     * @param font The font to use in output
     * @throws IOException
     */
    private void addToPDF(String text, PDDocument outputDocument, PDFont font) throws IOException {
        // Create a blank page and add it to the output document
        PDPage newPage = new PDPage();
        outputDocument.addPage(newPage);

        // Insert the contents into the new blank page
        try (PDPageContentStream cs = new PDPageContentStream(outputDocument, newPage)) {
            cs.beginText();
            cs.setFont(font, 12);

            // Set starting position (x, y) — origin is bottom-left corner of the page
            // A4 is 595 x 842 points, so this starts near the top-left
            cs.newLineAtOffset(50, 750);

            // Set line spacing for newLine() calls
            cs.setLeading(16f);

            // showText() cannot handle \n directly
            String[] lines = text.split("\\r?\\n");

            // Write each line separately
            for (String line : lines) {
                cs.showText(line);
                cs.newLine();
            }

            cs.endText();
        }
    }
}
