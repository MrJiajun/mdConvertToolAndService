package com.smartconvert.controller;

import com.smartconvert.dto.ConvertRequest;
import com.smartconvert.dto.ConvertResponse;
import com.smartconvert.service.ConversionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Document Conversion", description = "APIs for converting documents between different formats")
public class ConvertController {
    
    private static final Logger logger = LoggerFactory.getLogger(ConvertController.class);
    
    @Autowired
    private ConversionService conversionService;
    
    @PostMapping(value = "/convert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Convert document", description = "Convert a document from one format to another")
    @ApiResponse(responseCode = "200", description = "Conversion successful")
    @ApiResponse(responseCode = "400", description = "Invalid request or conversion failed")
    public ResponseEntity<ConvertResponse> convertFile(
            @Parameter(description = "File to convert") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Source format (docx, pdf, txt, md)") @RequestParam("sourceFormat") String sourceFormat,
            @Parameter(description = "Target format (docx, pdf, txt, md)") @RequestParam("targetFormat") String targetFormat) {
        
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
    @Operation(summary = "Download converted file", description = "Download a converted file by its ID and format")
    @ApiResponse(responseCode = "200", description = "File downloaded successfully")
    @ApiResponse(responseCode = "404", description = "File not found")
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "File ID") @PathVariable String fileId,
            @Parameter(description = "File format") @PathVariable String format) {
        
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
    @Operation(summary = "Health check", description = "Check if the service is running")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
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
