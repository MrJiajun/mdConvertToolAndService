package com.smartconvert.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class TextConversionService {

    public String textToMarkdown(InputStream inputStream) throws IOException {
        String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        
        StringBuilder markdown = new StringBuilder();
        String[] lines = content.split("\r?\n");
        
        for (String line : lines) {
            line = line.trim();
            
            if (line.isEmpty()) {
                markdown.append("\n");
                continue;
            }
            
            if (line.length() < 80 && !line.endsWith(".") && !line.endsWith("?") && !line.endsWith("!")) {
                if (line.matches("^[A-Z][A-Z\\s]+$")) {
                    markdown.append("# ").append(line).append("\n\n");
                } else if (line.matches("^[A-Z].*") && line.length() < 50) {
                    markdown.append("## ").append(line).append("\n\n");
                } else {
                    markdown.append(line).append("\n\n");
                }
            } else {
                markdown.append(line).append("\n\n");
            }
        }
        
        return markdown.toString().trim();
    }
    
    public Path markdownToText(String markdown, Path outputPath) throws IOException {
        StringBuilder text = new StringBuilder();
        String[] lines = markdown.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            
            if (line.isEmpty()) {
                text.append("\n");
                continue;
            }
            
            if (line.startsWith("# ")) {
                text.append(line.substring(2).toUpperCase()).append("\n");
                text.append("=".repeat(line.length() - 2)).append("\n");
            } else if (line.startsWith("## ")) {
                text.append(line.substring(3)).append("\n");
                text.append("-".repeat(line.length() - 3)).append("\n");
            } else if (line.startsWith("### ")) {
                text.append("  ").append(line.substring(4)).append("\n");
            } else if (line.startsWith("#### ")) {
                text.append("    ").append(line.substring(5)).append("\n");
            } else if (line.startsWith("##### ")) {
                text.append("      ").append(line.substring(6)).append("\n");
            } else if (line.startsWith("###### ")) {
                text.append("        ").append(line.substring(7)).append("\n");
            } else if (line.startsWith("- ") || line.startsWith("* ")) {
                text.append("  * ").append(line.substring(2)).append("\n");
            } else if (line.startsWith("> ")) {
                text.append("  > ").append(line.substring(2)).append("\n");
            } else if (line.startsWith("```")) {
            } else {
                String cleaned = line.replaceAll("\\*\\*(.+?)\\*\\*", "$1");
                cleaned = cleaned.replaceAll("\\*(.+?)\\*", "$1");
                cleaned = cleaned.replaceAll("`(.+?)`", "$1");
                cleaned = cleaned.replaceAll("\\[(.+?)\\]\\((.+?)\\)", "$1 ($2)");
                text.append(cleaned).append("\n");
            }
        }
        
        Files.writeString(outputPath, text.toString(), StandardCharsets.UTF_8);
        
        return outputPath;
    }
}
