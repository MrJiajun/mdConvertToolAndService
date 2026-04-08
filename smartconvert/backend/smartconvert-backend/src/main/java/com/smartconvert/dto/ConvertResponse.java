package com.smartconvert.dto;

public class ConvertResponse {
    
    private boolean success;
    private String message;
    private String downloadUrl;
    private String fileName;
    private long fileSize;
    
    public ConvertResponse() {}
    
    public ConvertResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public static ConvertResponse success(String downloadUrl, String fileName, long fileSize) {
        ConvertResponse response = new ConvertResponse();
        response.success = true;
        response.message = "Conversion successful";
        response.downloadUrl = downloadUrl;
        response.fileName = fileName;
        response.fileSize = fileSize;
        return response;
    }
    
    public static ConvertResponse error(String message) {
        ConvertResponse response = new ConvertResponse();
        response.success = false;
        response.message = message;
        return response;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getDownloadUrl() {
        return downloadUrl;
    }
    
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
