package com.smartconvert.service;

import com.smartconvert.dto.ConvertRequest;
import com.smartconvert.dto.ConvertResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.UUID;

@Service
public class ConversionService {
    
    private static final Logger logger = LoggerFactory.getLogger(ConversionService.class);
    
    @Autowired
    private WordConversionService wordConversionService;
    
    @Autowired
    private PdfConversionService pdfConversionService;
    
    @Autowired
    private TextConversionService textConversionService;
    
    @Value("${app.upload.dir}")
    private String uploadDir;
    
    public ConvertResponse convert(ConvertRequest request) {
        try {
            MultipartFile file = request.getFile();
            String sourceFormat = request.getSourceFormat().toLowerCase();
            String targetFormat = request.getTargetFormat().toLowerCase();
            
            logger.info("Converting file from {} to {}", sourceFormat, targetFormat);
            
            if (sourceFormat.equals(targetFormat)) {
                return ConvertResponse.error("Source and target formats are the same");
            }
            
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            String fileId = UUID.randomUUID().toString();
            Path inputFile = uploadPath.resolve(fileId + "." + sourceFormat);
            Path outputFile = uploadPath.resolve(fileId + "." + targetFormat);
            
            Files.copy(file.getInputStream(), inputFile, StandardCopyOption.REPLACE_EXISTING);
            
            String intermediateMarkdown = null;
            
            switch (sourceFormat) {
                case "docx":
                    try (InputStream is = Files.newInputStream(inputFile)) {
                        intermediateMarkdown = wordConversionService.docxToMarkdown(is);
                    }
                    break;
                case "pdf":
                    try (InputStream is = Files.newInputStream(inputFile)) {
                        intermediateMarkdown = pdfConversionService.pdfToMarkdown(is);
                    }
                    break;
                case "txt":
                    try (InputStream is = Files.newInputStream(inputFile)) {
                        intermediateMarkdown = textConversionService.textToMarkdown(is);
                    }
                    break;
                case "md":
                    intermediateMarkdown = Files.readString(inputFile);
                    break;
                default:
                    return ConvertResponse.error("Unsupported source format: " + sourceFormat);
            }
            
            switch (targetFormat) {
                case "docx":
                    wordConversionService.markdownToDocx(intermediateMarkdown, outputFile);
                    break;
                case "pdf":
                    pdfConversionService.markdownToPdf(intermediateMarkdown, outputFile);
                    break;
                case "txt":
                    textConversionService.markdownToText(intermediateMarkdown, outputFile);
                    break;
                case "md":
                    Files.writeString(outputFile, intermediateMarkdown);
                    break;
                default:
                    return ConvertResponse.error("Unsupported target format: " + targetFormat);
            }
            
            String downloadUrl = "/api/download/" + fileId + "/" + targetFormat;
            String originalFileName = file.getOriginalFilename();
            String outputFileName = originalFileName != null 
                ? originalFileName.replaceAll("\\.[^.]+$", "") + "." + targetFormat
                : "converted." + targetFormat;
            
            long fileSize = Files.size(outputFile);
            
            logger.info("Conversion successful: {} -> {}", sourceFormat, targetFormat);
            
            return ConvertResponse.success(downloadUrl, outputFileName, fileSize);
            
        } catch (Exception e) {
            logger.error("Conversion failed", e);
            return ConvertResponse.error("Conversion failed: " + e.getMessage());
        }
    }
    
    public Path getFilePath(String fileId, String format) {
        return Paths.get(uploadDir).resolve(fileId + "." + format);
    }
}
