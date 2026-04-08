package com.smartconvert.controller;

import com.smartconvert.dto.ConvertRequest;
import com.smartconvert.dto.ConvertResponse;
import com.smartconvert.service.ConversionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ConvertController {
    
    private static final Logger logger = LoggerFactory.getLogger(ConvertController.class);
    
    @Autowired
    private ConversionService conversionService;
    
    @PostMapping(value = "/convert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ConvertResponse> convertFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("sourceFormat") String sourceFormat,
            @RequestParam("targetFormat") String targetFormat) {
        
        logger.info("Received conversion request: {} -> {}", sourceFormat, targetFormat);
        
        ConvertRequest request = new ConvertRequest();
        request.setFile(file);
        request.setSourceFormat(sourceFormat);
        request.setTargetFormat(targetFormat);
        
        ConvertResponse response = conversionService.convert(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/download/{fileId}/{format}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String fileId,
            @PathVariable String format) {
        
        logger.info("Download request for file: {}.{}", fileId, format);
        
        try {
            Path filePath = conversionService.getFilePath(fileId, format);
            
            if (!filePath.toFile().exists()) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new FileSystemResource(filePath);
            
            String contentType = getContentType(format);
            String fileName = "converted." + format;
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            logger.error("Error downloading file", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }
    
    private String getContentType(String format) {
        return switch (format.toLowerCase()) {
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "pdf" -> "application/pdf";
            case "txt" -> "text/plain";
            case "md" -> "text/markdown";
            default -> "application/octet-stream";
        };
    }
}
