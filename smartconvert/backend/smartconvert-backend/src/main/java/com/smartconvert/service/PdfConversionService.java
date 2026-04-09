package com.smartconvert.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

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
        // For now, create a simple text-based PDF
        // A more sophisticated implementation would use OpenPDF or iText for rich formatting
        
        try (com.lowagie.text.Document document = new com.lowagie.text.Document()) {
            com.lowagie.text.pdf.PdfWriter.getInstance(document, 
                new FileOutputStream(outputPath.toFile()));
            
            document.open();
            
            String[] lines = markdown.split("\\n");
            
            for (String line : lines) {
                line = line.trim();
                
                if (line.isEmpty()) {
                    document.add(new com.lowagie.text.Paragraph(" "));
                    continue;
                }
                
                // Handle headings
                if (line.startsWith("###### ")) {
                    com.lowagie.text.Paragraph p = new com.lowagie.text.Paragraph(
                        line.substring(7), 
                        new com.lowagie.text.Font(com.lowagie.text.Font.FontFamily.HELVETICA, 10, com.lowagie.text.Font.BOLD)
                    );
                    document.add(p);
                } else if (line.startsWith("##### ")) {
                    com.lowagie.text.Paragraph p = new com.lowagie.text.Paragraph(
                        line.substring(6), 
                        new com.lowagie.text.Font(com.lowagie.text.Font.FontFamily.HELVETICA, 11, com.lowagie.text.Font.BOLD)
                    );
                    document.add(p);
                } else if (line.startsWith("#### ")) {
                    com.lowagie.text.Paragraph p = new com.lowagie.text.Paragraph(
                        line.substring(5), 
                        new com.lowagie.text.Font(com.lowagie.text.Font.FontFamily.HELVETICA, 12, com.lowagie.text.Font.BOLD)
                    );
                    document.add(p);
                } else if (line.startsWith("### ")) {
                    com.lowagie.text.Paragraph p = new com.lowagie.text.Paragraph(
                        line.substring(4), 
                        new com.lowagie.text.Font(com.lowagie.text.Font.FontFamily.HELVETICA, 14, com.lowagie.text.Font.BOLD)
                    );
                    document.add(p);
                } else if (line.startsWith("## ")) {
                    com.lowagie.text.Paragraph p = new com.lowagie.text.Paragraph(
                        line.substring(3), 
                        new com.lowagie.text.Font(com.lowagie.text.Font.FontFamily.HELVETICA, 16, com.lowagie.text.Font.BOLD)
                    );
                    document.add(p);
                } else if (line.startsWith("# ")) {
                    com.lowagie.text.Paragraph p = new com.lowagie.text.Paragraph(
                        line.substring(2), 
                        new com.lowagie.text.Font(com.lowagie.text.Font.FontFamily.HELVETICA, 18, com.lowagie.text.Font.BOLD)
                    );
                    document.add(p);
                } else if (line.startsWith("- ") || line.startsWith("* ")) {
                    com.lowagie.text.Paragraph p = new com.lowagie.text.Paragraph(
                        "• " + line.substring(2), 
                        new com.lowagie.text.Font(com.lowagie.text.Font.FontFamily.HELVETICA, 12)
                    );
                    p.setIndentationLeft(20);
                    document.add(p);
                } else if (line.startsWith("---")) {
                    document.add(new com.lowagie.text.Paragraph("─".repeat(50)));
                } else {
                    // Regular paragraph - strip markdown formatting
                    String cleanText = stripMarkdownFormatting(line);
                    com.lowagie.text.Paragraph p = new com.lowagie.text.Paragraph(
                        cleanText, 
                        new com.lowagie.text.Font(com.lowagie.text.Font.FontFamily.HELVETICA, 12)
                    );
                    document.add(p);
                }
            }
            
            document.close();
        } catch (Exception e) {
            logger.error("Error creating PDF", e);
            throw new IOException("Failed to create PDF: " + e.getMessage(), e);
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
