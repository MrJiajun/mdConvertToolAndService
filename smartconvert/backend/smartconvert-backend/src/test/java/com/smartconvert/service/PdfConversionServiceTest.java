package com.smartconvert.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PdfConversionServiceTest {

    private PdfConversionService pdfConversionService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        pdfConversionService = new PdfConversionService();
    }

    @Test
    @DisplayName("Should handle empty input stream for PDF to markdown")
    void pdfToMarkdown_shouldHandleEmptyInput() {
        assertThrows(Exception.class, () -> {
            ByteArrayInputStream emptyStream = new ByteArrayInputStream(new byte[0]);
            pdfConversionService.pdfToMarkdown(emptyStream);
        });
    }

    @Test
    @DisplayName("Should convert markdown to PDF file")
    void markdownToPdf_shouldConvertMarkdown() throws IOException {
        String markdown = "# Main Title\n\nThis is a paragraph.\n\n## Subtitle\n\nAnother paragraph.";
        Path outputPath = tempDir.resolve("output.pdf");
        
        pdfConversionService.markdownToPdf(markdown, outputPath);
        
        assertTrue(Files.exists(outputPath));
        assertTrue(Files.size(outputPath) > 0);
    }

    @Test
    @DisplayName("Should handle headings in PDF conversion")
    void markdownToPdf_shouldHandleHeadings() throws IOException {
        String markdown = "# Heading 1\n## Heading 2\n### Heading 3";
        Path outputPath = tempDir.resolve("output.pdf");
        
        pdfConversionService.markdownToPdf(markdown, outputPath);
        
        assertTrue(Files.exists(outputPath));
    }

    @Test
    @DisplayName("Should handle list items in PDF conversion")
    void markdownToPdf_shouldHandleListItems() throws IOException {
        String markdown = "- Item 1\n- Item 2\n- Item 3";
        Path outputPath = tempDir.resolve("output.pdf");
        
        pdfConversionService.markdownToPdf(markdown, outputPath);
        
        assertTrue(Files.exists(outputPath));
    }

    @Test
    @DisplayName("Should handle bold and italic text in PDF conversion")
    void markdownToPdf_shouldHandleFormattedText() throws IOException {
        String markdown = "This is **bold** and *italic* text.";
        Path outputPath = tempDir.resolve("output.pdf");
        
        pdfConversionService.markdownToPdf(markdown, outputPath);
        
        assertTrue(Files.exists(outputPath));
    }

    @Test
    @DisplayName("Should handle horizontal rule in PDF conversion")
    void markdownToPdf_shouldHandleHorizontalRule() throws IOException {
        String markdown = "Content above\n\n---\n\nContent below";
        Path outputPath = tempDir.resolve("output.pdf");
        
        pdfConversionService.markdownToPdf(markdown, outputPath);
        
        assertTrue(Files.exists(outputPath));
    }
}
