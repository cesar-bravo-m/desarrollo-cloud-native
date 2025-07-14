package com.grupo11.ms_productos.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.host")
public class HostConfig {
    
    private String baseUrl;
    private String apiGateway;
    private String frontend;
    
    // Getters and Setters
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public String getApiGateway() {
        return apiGateway;
    }
    
    public void setApiGateway(String apiGateway) {
        this.apiGateway = apiGateway;
    }
    
    public String getFrontend() {
        return frontend;
    }
    
    public void setFrontend(String frontend) {
        this.frontend = frontend;
    }
} 