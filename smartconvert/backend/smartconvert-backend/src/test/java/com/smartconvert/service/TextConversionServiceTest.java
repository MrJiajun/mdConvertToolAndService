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

class TextConversionServiceTest {

    private TextConversionService textConversionService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        textConversionService = new TextConversionService();
    }

    @Test
    @DisplayName("Should convert plain text to markdown with title detection")
    void textToMarkdown_shouldConvertPlainText() throws IOException {
        String inputText = "INTRODUCTION\n\nThis is a sample paragraph.\n\nCONCLUSION\n\nThis is the end.";
        
        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputText.getBytes());
        
        String result = textConversionService.textToMarkdown(inputStream);
        
        assertNotNull(result);
        assertTrue(result.contains("# "));
        assertTrue(result.contains("Introduction"));
    }

    @Test
    @DisplayName("Should handle empty text")
    void textToMarkdown_shouldHandleEmptyText() throws IOException {
        String inputText = "";
        
        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputText.getBytes());
        
        String result = textConversionService.textToMarkdown(inputStream);
        
        assertNotNull(result);
        assertEquals("", result);
    }

    @Test
    @DisplayName("Should convert markdown to text file")
    void markdownToText_shouldConvertMarkdown() throws IOException {
        String markdown = "# Main Title\n\nThis is a paragraph.\n\n## Subtitle\n\nAnother paragraph.";
        Path outputPath = tempDir.resolve("output.txt");
        
        textConversionService.markdownToText(markdown, outputPath);
        
        assertTrue(Files.exists(outputPath));
        String content = Files.readString(outputPath);
        assertTrue(content.contains("MAIN TITLE"));
        assertTrue(content.contains("Subtitle"));
    }

    @Test
    @DisplayName("Should strip markdown formatting in text conversion")
    void markdownToText_shouldStripFormatting() throws IOException {
        String markdown = "This is **bold** and *italic* text.";
        Path outputPath = tempDir.resolve("output.txt");
        
        textConversionService.markdownToText(markdown, outputPath);
        
        String content = Files.readString(outputPath);
        assertTrue(content.contains("bold"));
        assertTrue(content.contains("italic"));
        assertFalse(content.contains("**"));
        assertFalse(content.contains("*"));
    }

    @Test
    @DisplayName("Should handle list items in markdown")
    void markdownToText_shouldHandleListItems() throws IOException {
        String markdown = "- Item 1\n- Item 2\n- Item 3";
        Path outputPath = tempDir.resolve("output.txt");
        
        textConversionService.markdownToText(markdown, outputPath);
        
        String content = Files.readString(outputPath);
        assertTrue(content.contains("Item 1"));
        assertTrue(content.contains("Item 2"));
        assertTrue(content.contains("Item 3"));
    }
}
