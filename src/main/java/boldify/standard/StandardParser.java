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
import java.util.ArrayList;
import java.util.List;

public class StandardParser {

    // Load both fonts once for the entire document - this font is standard for the used library
    private final PDFont regularFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private final PDFont boldFont    = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

    public StandardParser(String inputPath, String outputPath) {
        // Load the PDF and boldify it
        try(PDDocument inputDocument = Loader.loadPDF(new File(inputPath))) {
            // Create an output PDF to insert processed pages
            PDDocument outputDocument = new PDDocument();

            // Process the input PDF
            processPDF(inputDocument, outputDocument);

            // Save the new PDF to a given filepath adn close
            outputDocument.save(new File(outputPath));
            outputDocument.close();
        } catch(IOException e) {
            System.err.println("Error opening PDF file: " + e.getMessage());
        }

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

            // Save each page in a list with each line being a list within it
            List<List<TextRun>> boldifiedLines = boldifyText(text);

            // Add the page to the final PDF
            addToPDF(boldifiedLines, outputDocument, regularFont, boldFont);
        }
    }

    /** Use the TextRun record to create lines from some page text
     *
     * @param text the text from PDFTextStripper
     * @return A lsit of lines consisting of TextRuns
     */
    private List<List<TextRun>> boldifyText(String text) {
        List<List<TextRun>> lines = new ArrayList<>();

        // For each line in the page
        for ( String line : text.split("\\r?\\n")) {
            List <TextRun> runs = new ArrayList<>();
            String[] words = line.split(" ");

            // For each word in the line
            for (int w = 0; w < words.length; w++) {
                String word = words[w];
                // Boldify first letters of each word
                String[] parts = boldifyWord(word);

                // Write the word back, adding a space after the word unless it is the last on the line
                String bold = parts[0];
                String nonBold = parts[1] + (w < words.length - 1 ? " " : "");

                runs.add(new TextRun(bold, true));
                runs.add(new TextRun(nonBold, false));
            }
            // Add the lines to the list representing the page
            lines.add(runs);
        }
        // Return the whole page
        return lines;
    }

    /** Split a single word into its bold and regular parts
     *
     * @param word The word to split
     * @return [boldPart, regularPart]
     */
    public String[] boldifyWord(String word) {
        // Count the letters in each word
        int letterCount = 0;
        for (char c : word.toCharArray()) {
            if (Character.isLetter(c)) { letterCount++; }
        }
        int boldLetters = getBoldLength(letterCount);

        // Walk the word to find where the bold portion ends
        int boldUntil = 0;
        int lettersFound = 0;
        for (int i = 0; i < word.length(); i++) {
            if (Character.isLetter(word.charAt(i))) {
                lettersFound++;
            }
            if (lettersFound == boldLetters) {
                boldUntil = i + 1;
                break;
            }
        }

        return new String[]{
                word.substring(0, boldUntil),
                word.substring(boldUntil)};
    }

    /** Takes a word and returns how many characters to make bold depending on its length
     *
     * @param wordLength The number of characters in the word
     * @return How many words to make bold
     */
    private int getBoldLength(int wordLength) {
        if (wordLength <= 2) return 0;
        if (wordLength <= 5) return 2;
        if (wordLength <= 7) return 3;
        if (wordLength <= 9) return 4;
        return wordLength / 2;
    }

    /** Write boldified lines to a new page in the output document
     *
     * @param lines Lines of TextRuns to write
     * @param outputDocument The document to write to
     * @param regularFont The font that is not bold
     * @param boldFont The font that is bold
     * @throws IOException
     */
    private void addToPDF(List<List<TextRun>> lines, PDDocument outputDocument,
                          PDFont regularFont, PDFont boldFont) throws IOException {
        PDPage newPage = new PDPage();
        outputDocument.addPage(newPage);

        try (PDPageContentStream cs = new PDPageContentStream(outputDocument, newPage)) {
            cs.beginText();
            cs.newLineAtOffset(50, 750);
            cs.setLeading(16f);

            try {
                for (List<TextRun> line : lines) {
                    for (TextRun run : line) {
                        if (run.text().isEmpty()) continue;
                        PDFont font = run.bold() ? boldFont : regularFont;
                        cs.setFont(font, 12);
                        cs.showText(sanitizeForFont(run.text(), font));
                    }
                    cs.newLine();
                }
            } finally {
                cs.endText();
            }
        }
    }

    /** Check characters and replace illegal ones with ?
     *
     * @param text The text to check
     * @param font The font to encode a character as
     * @return The sanitized String
     */
    private String sanitizeForFont(String text, PDFont font) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); ) {
            int codePoint = text.codePointAt(i);
            String ch = new String(Character.toChars(codePoint));
            try {
                font.encode(ch);
                sb.append(ch);
            } catch (Exception e) {
                sb.append("?");
            }
            i += Character.charCount(codePoint);
        }
        return sb.toString();
    }
}
