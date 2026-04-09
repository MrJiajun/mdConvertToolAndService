package com.smartconvert.controller;

import com.smartconvert.dto.ConvertResponse;
import com.smartconvert.service.ConversionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ConvertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConversionService conversionService;

    @BeforeEach
    void setUp() {
        // Setup mock responses
    }

    @Test
    @DisplayName("Health check should return OK")
    void healthCheck_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }

    @Test
    @DisplayName("Convert endpoint should handle valid request")
    void convert_shouldHandleValidRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Hello World".getBytes()
        );

        ConvertResponse mockResponse = ConvertResponse.success(
                "/api/download/test-id/md",
                "test.md",
                100L
        );

        when(conversionService.convert(any())).thenReturn(mockResponse);

        mockMvc.perform(multipart("/api/convert")
                        .file(file)
                        .param("sourceFormat", "txt")
                        .param("targetFormat", "md"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Convert endpoint should handle error response")
    void convert_shouldHandleError() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Hello World".getBytes()
        );

        ConvertResponse mockResponse = ConvertResponse.error("Conversion failed");

        when(conversionService.convert(any())).thenReturn(mockResponse);

        mockMvc.perform(multipart("/api/convert")
                        .file(file)
                        .param("sourceFormat", "txt")
                        .param("targetFormat", "md"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
