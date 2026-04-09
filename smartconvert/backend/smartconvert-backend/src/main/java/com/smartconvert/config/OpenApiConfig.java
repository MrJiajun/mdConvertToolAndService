package com.smartconvert.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI smartConvertOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SmartConvert API")
                        .description("Multi-format Document Conversion Tool API")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("SmartConvert Team")
                                .email("support@smartconvert.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
