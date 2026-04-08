package com.smartconvert.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.multipart.MultipartFile;

public class ConvertRequest {
    
    @NotNull(message = "File is required")
    private MultipartFile file;
    
    @NotBlank(message = "Source format is required")
    @Pattern(regexp = "^(docx|pdf|txt|md)$", message = "Invalid source format")
    private String sourceFormat;
    
    @NotBlank(message = "Target format is required")
    @Pattern(regexp = "^(docx|pdf|txt|md)$", message = "Invalid target format")
    private String targetFormat;
    
    public MultipartFile getFile() {
        return file;
    }
    
    public void setFile(MultipartFile file) {
        this.file = file;
    }
    
    public String getSourceFormat() {
        return sourceFormat;
    }
    
    public void setSourceFormat(String sourceFormat) {
        this.sourceFormat = sourceFormat;
    }
    
    public String getTargetFormat() {
        return targetFormat;
    }
    
    public void setTargetFormat(String targetFormat) {
        this.targetFormat = targetFormat;
    }
}
