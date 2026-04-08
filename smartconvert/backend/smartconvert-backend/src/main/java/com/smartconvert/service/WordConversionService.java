package com.smartconvert.service;

import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

@Service
public class WordConversionService {

    public String docxToMarkdown(InputStream inputStream) throws IOException {
        StringBuilder markdown = new StringBuilder();
        
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            
            for (XWPFParagraph paragraph : paragraphs) {
                String paragraphText = convertParagraphToMarkdown(paragraph);
                if (!paragraphText.isEmpty()) {
                    markdown.append(paragraphText).append("\n\n");
                }
            }
            
            List<XWPFTable> tables = document.getTables();
            for (XWPFTable table : tables) {
                markdown.append(convertTableToMarkdown(table)).append("\n\n");
            }
        }
        
        return markdown.toString().trim();
    }
    
    private String convertParagraphToMarkdown(XWPFParagraph paragraph) {
        String text = paragraph.getText().trim();
        if (text.isEmpty()) {
            return "";
        }
        
        String style = paragraph.getStyle();
        
        if (style != null) {
            if (style.startsWith("Heading1")) {
                return "# " + text;
            } else if (style.startsWith("Heading2")) {
                return "## " + text;
            } else if (style.startsWith("Heading3")) {
                return "### " + text;
            } else if (style.startsWith("Heading4")) {
                return "#### " + text;
            } else if (style.startsWith("Heading5")) {
                return "##### " + text;
            } else if (style.startsWith("Heading6")) {
                return "###### " + text;
            }
        }
        
        StringBuilder mdText = new StringBuilder();
        for (XWPFRun run : paragraph.getRuns()) {
            String runText = run.getText(0);
            if (runText == null) continue;
            
            if (run.isBold()) {
                mdText.append("**").append(runText).append("**");
            } else if (run.isItalic()) {
                mdText.append("*").append(runText).append("*");
            } else {
                mdText.append(runText);
            }
        }
        
        return mdText.toString();
    }
    
    private String convertTableToMarkdown(XWPFTable table) {
        StringBuilder md = new StringBuilder();
        List<XWPFTableRow> rows = table.getRows();
        
        if (rows.isEmpty()) {
            return "";
        }
        
        for (int i = 0; i < rows.size(); i++) {
            XWPFTableRow row = rows.get(i);
            md.append("| ");
            
            for (XWPFTableCell cell : row.getTableCells()) {
                md.append(cell.getText().trim()).append(" | ");
            }
            md.append("\n");
            
            if (i == 0) {
                md.append("|");
                for (int j = 0; j < row.getTableCells().size(); j++) {
                    md.append(" --- |");
                }
                md.append("\n");
            }
        }
        
        return md.toString().trim();
    }
    
    public Path markdownToDocx(String markdown, Path outputPath) throws IOException {
        try (XWPFDocument document = new XWPFDocument();
             FileOutputStream out = new FileOutputStream(outputPath.toFile())) {
            
            String[] lines = markdown.split("\n");
            
            for (String line : lines) {
                line = line.trim();
                
                if (line.isEmpty()) {
                    continue;
                }
                
                XWPFParagraph paragraph = document.createParagraph();
                XWPFRun run = paragraph.createRun();
                
                if (line.startsWith("###### ")) {
                    paragraph.setStyle("Heading6");
                    run.setText(line.substring(7));
                } else if (line.startsWith("##### ")) {
                    paragraph.setStyle("Heading5");
                    run.setText(line.substring(6));
                } else if (line.startsWith("#### ")) {
                    paragraph.setStyle("Heading4");
                    run.setText(line.substring(5));
                } else if (line.startsWith("### ")) {
                    paragraph.setStyle("Heading3");
                    run.setText(line.substring(4));
                } else if (line.startsWith("## ")) {
                    paragraph.setStyle("Heading2");
                    run.setText(line.substring(3));
                } else if (line.startsWith("# ")) {
                    paragraph.setStyle("Heading1");
                    run.setText(line.substring(2));
                } else if (line.startsWith("**") && line.endsWith("**")) {
                    run.setBold(true);
                    run.setText(line.substring(2, line.length() - 2));
                } else if (line.startsWith("*") && line.endsWith("*")) {
                    run.setItalic(true);
                    run.setText(line.substring(1, line.length() - 1));
                } else {
                    run.setText(line);
                }
            }
            
            document.write(out);
        }
        
        return outputPath;
    }
}
