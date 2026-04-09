package com.smartconvert.service;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.lowagie.text.Font.*;

@Service
public class PdfConversionService {
    
    private static final Logger logger = LoggerFactory.getLogger(PdfConversionService.class);
    
    /**
     * Convert PDF to Markdown format
     * Extracts text content from PDF and attempts to preserve structure
     */
    public String pdfToMarkdown(InputStream inputStream) throws IOException {
        StringBuilder markdown = new StringBuilder();
        
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            
            int numberOfPages = document.getNumberOfPages();
            
            for (int i = 1; i <= numberOfPages; i++) {
                stripper.setStartPage(i);
                stripper.setEndPage(i);
                
                String pageText = stripper.getText(document);
                
                // Add page separator
                if (i > 1) {
                    markdown.append("\n\n---\n\n");
                }
                
                // Process the page text
                markdown.append(processPageText(pageText));
            }
        }
        
        return markdown.toString().trim();
    }
    
    private String processPageText(String text) {
        StringBuilder result = new StringBuilder();
        String[] lines = text.split("\\r?\\n");
        
        boolean inList = false;
        
        for (String line : lines) {
            line = line.trim();
            
            if (line.isEmpty()) {
                if (inList) {
                    inList = false;
                }
                result.append("\n");
                continue;
            }
            
            // Detect potential headings (short lines, possibly all caps or title case)
            if (line.length() < 60 && !line.endsWith(".")) {
                if (line.matches("^[A-Z][A-Z\\s]+$")) {
                    // All caps - treat as H1
                    result.append("# ").append(capitalizeWords(line)).append("\n\n");
                    continue;
                } else if (line.matches("^[A-Z][a-z]+(\\s+[A-Z]?[a-z]+)*$")) {
                    // Title case - treat as H2
                    result.append("## ").append(line).append("\n\n");
                    continue;
                }
            }
            
            // Detect list items (lines starting with -, *, •, or numbers)
            if (line.matches("^[•\\-*]\\s+.*")) {
                result.append("- ").append(line.substring(2)).append("\n");
                inList = true;
                continue;
            }
            
            if (line.matches("^\\d+[.)]\\s+.*")) {
                String listContent = line.replaceFirst("^\\d+[.)]\\s+", "");
                result.append("1. ").append(listContent).append("\n");
                inList = true;
                continue;
            }
            
            // Regular paragraph
            result.append(line);
            if (!inList) {
                result.append("\n");
            }
        }
        
        return result.toString();
    }
    
    private String capitalizeWords(String text) {
        StringBuilder result = new StringBuilder();
        String[] words = text.toLowerCase().split("\\s+");
        
        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                result.append(" ");
            }
            if (!words[i].isEmpty()) {
                result.append(Character.toUpperCase(words[i].charAt(0)))
                      .append(words[i].substring(1));
            }
        }
        
        return result.toString();
    }
    
    /**
     * Convert Markdown to PDF format
     * Creates a simple PDF with formatted content
     */
    public void markdownToPdf(String markdown, Path outputPath) throws IOException {
        Document document = new Document();
        
        try {
            PdfWriter.getInstance(document, new FileOutputStream(outputPath.toFile()));
            document.open();
            
            Font heading1Font = new Font(HELVETICA, 18, BOLD);
            Font heading2Font = new Font(HELVETICA, 16, BOLD);
            Font heading3Font = new Font(HELVETICA, 14, BOLD);
            Font heading4Font = new Font(HELVETICA, 12, BOLD);
            Font heading5Font = new Font(HELVETICA, 11, BOLD);
            Font heading6Font = new Font(HELVETICA, 10, BOLD);
            Font normalFont = new Font(HELVETICA, 12, NORMAL);
            Font listFont = new Font(HELVETICA, 12, NORMAL);
            
            String[] lines = markdown.split("\\n");
            
            for (String line : lines) {
                line = line.trim();
                
                if (line.isEmpty()) {
                    document.add(new Paragraph(" "));
                    continue;
                }
                
                // Handle headings
                if (line.startsWith("###### ")) {
                    document.add(new Paragraph(line.substring(7), heading6Font));
                } else if (line.startsWith("##### ")) {
                    document.add(new Paragraph(line.substring(6), heading5Font));
                } else if (line.startsWith("#### ")) {
                    document.add(new Paragraph(line.substring(5), heading4Font));
                } else if (line.startsWith("### ")) {
                    document.add(new Paragraph(line.substring(4), heading3Font));
                } else if (line.startsWith("## ")) {
                    document.add(new Paragraph(line.substring(3), heading2Font));
                } else if (line.startsWith("# ")) {
                    document.add(new Paragraph(line.substring(2), heading1Font));
                } else if (line.startsWith("- ") || line.startsWith("* ")) {
                    Paragraph p = new Paragraph("• " + line.substring(2), listFont);
                    p.setIndentationLeft(20);
                    document.add(p);
                } else if (line.startsWith("---")) {
                    document.add(new Paragraph("─".repeat(50)));
                } else {
                    // Regular paragraph - strip markdown formatting
                    String cleanText = stripMarkdownFormatting(line);
                    document.add(new Paragraph(cleanText, normalFont));
                }
            }
            
        } catch (Exception e) {
            logger.error("Error creating PDF", e);
            throw new IOException("Failed to create PDF: " + e.getMessage(), e);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }
    
    private String stripMarkdownFormatting(String text) {
        // Remove bold markers
        String result = text.replaceAll("\\*\\*(.+?)\\*\\*", "$1");
        // Remove italic markers
        result = result.replaceAll("\\*(.+?)\\*", "$1");
        // Remove code markers
        result = result.replaceAll("`(.+?)`", "$1");
        // Remove links, keep text
        result = result.replaceAll("\\[(.+?)\\]\\((.+?)\\)", "$1");
        
        return result;
    }
}
