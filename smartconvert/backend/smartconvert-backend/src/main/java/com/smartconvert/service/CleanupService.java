package com.smartconvert.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;

@Service
public class CleanupService {
    
    private static final Logger logger = LoggerFactory.getLogger(CleanupService.class);
    
    @Value("${app.upload.dir}")
    private String uploadDir;
    
    @Value("${app.cleanup.max-age:3600000}")
    private long maxAgeMillis;
    
    @Scheduled(fixedDelayString = "${app.cleanup.interval:1800000}")
    public void cleanupOldFiles() {
        logger.info("Starting cleanup of old files");
        
        try {
            Path uploadPath = Paths.get(uploadDir);
            
            if (!Files.exists(uploadPath)) {
                logger.info("Upload directory does not exist, skipping cleanup");
                return;
            }
            
            Instant cutoffTime = Instant.now().minusMillis(maxAgeMillis);
            
            Files.walkFileTree(uploadPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (attrs.lastModifiedTime().toInstant().isBefore(cutoffTime)) {
                        Files.delete(file);
                        logger.debug("Deleted old file: {}", file);
                    }
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (!dir.equals(uploadPath)) {
                        try {
                            Files.delete(dir);
                            logger.debug("Deleted empty directory: {}", dir);
                        } catch (DirectoryNotEmptyException e) {
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
            
            logger.info("Cleanup completed successfully");
            
        } catch (IOException e) {
            logger.error("Error during cleanup", e);
        }
    }
}
