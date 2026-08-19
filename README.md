# Boldify
Extract text from a PDF and replace some letters in bold letters to improve reading performance.

## What it does
Boldify extracts all the text from a PDF document and applies a simple algorithm that makes some letter bold. This allows for some people to increase their understanding and speed when reading.

## Prerequisites
- Java 21+
- Maven

## Dependencies
Managed via Maven ('pom.xml'):
- [Apache PDFBox 3.0.6](https://pdfbox.apache.org/) — PDF reading and writing

## Usage
Run with two arguments specifying the filepath to the input document as well as the filepath that the output document should be placed.
NOTE: an existing file with the same name will be overwritten.
Example filepath: C:\Some\Path\Containing\document.pdf

## How it works
1. The input PDF is loaded page by page to minimize memory usage in case of large PDFs
2. The text is extracted from each page using 'PDFTextStripper'
3. Each word is split into a bold partion followed by a non-bold portion based on its length
4. A new PDF is created using the edited text and each page gets written to a new blank PDF page

## Current limitations
- Font is hardcoded to Helvetica and Helvetica Bold
- Characters outside basic Latin (e.g. æ, ø, å) are replaced with `?`
- Only the text of the input document is extracted, thus the output will only contain whatever text is found