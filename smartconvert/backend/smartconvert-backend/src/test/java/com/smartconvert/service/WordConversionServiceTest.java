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

class WordConversionServiceTest {

    private WordConversionService wordConversionService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        wordConversionService = new WordConversionService();
    }

    @Test
    @DisplayName("Should handle empty input stream for docx to markdown")
    void docxToMarkdown_shouldHandleEmptyInput() {
        // Empty docx content would cause an exception, so we test with null/empty stream
        assertThrows(Exception.class, () -> {
            ByteArrayInputStream emptyStream = new ByteArrayInputStream(new byte[0]);
            wordConversionService.docxToMarkdown(emptyStream);
        });
    }

    @Test
    @DisplayName("Should convert markdown to docx file")
    void markdownToDocx_shouldConvertMarkdown() throws IOException {
        String markdown = "# Main Title\n\nThis is a paragraph.\n\n## Subtitle\n\nAnother paragraph.";
        Path outputPath = tempDir.resolve("output.docx");
        
        wordConversionService.markdownToDocx(markdown, outputPath);
        
        assertTrue(Files.exists(outputPath));
        assertTrue(Files.size(outputPath) > 0);
    }

    @Test
    @DisplayName("Should handle bold text in markdown")
    void markdownToDocx_shouldHandleBoldText() throws IOException {
        String markdown = "This is **bold** text.";
        Path outputPath = tempDir.resolve("output.docx");
        
        wordConversionService.markdownToDocx(markdown, outputPath);
        
        assertTrue(Files.exists(outputPath));
        assertTrue(Files.size(outputPath) > 0);
    }

    @Test
    @DisplayName("Should handle italic text in markdown")
    void markdownToDocx_shouldHandleItalicText() throws IOException {
        String markdown = "This is *italic* text.";
        Path outputPath = tempDir.resolve("output.docx");
        
        wordConversionService.markdownToDocx(markdown, outputPath);
        
        assertTrue(Files.exists(outputPath));
        assertTrue(Files.size(outputPath) > 0);
    }

    @Test
    @DisplayName("Should handle multiple heading levels")
    void markdownToDocx_shouldHandleMultipleHeadings() throws IOException {
        String markdown = "# H1\n## H2\n### H3\n#### H4\n##### H5\n###### H6";
        Path outputPath = tempDir.resolve("output.docx");
        
        wordConversionService.markdownToDocx(markdown, outputPath);
        
        assertTrue(Files.exists(outputPath));
        assertTrue(Files.size(outputPath) > 0);
    }
}
