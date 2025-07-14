package com.grupo11.ms_productos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.grupo11.ms_productos.config.ExternalServicesConfig;
import com.grupo11.ms_productos.config.HostConfig;

@Service
public class ExternalServiceClient {
    
    @Autowired
    private HostConfig hostConfig;
    
    @Autowired
    private ExternalServicesConfig externalServicesConfig;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    /**
     * Example method showing how to use configurable URLs instead of hardcoded localhost
     */
    public String callUserService(Long userId) {
        // Instead of: "http://localhost:8081/user/" + userId
        String url = externalServicesConfig.getUserService() + "/user/" + userId;
        return restTemplate.getForObject(url, String.class);
    }
    
    /**
     * Example method showing how to build URLs using the base URL configuration
     */
    public String getProductUrl(Long productId) {
        // Instead of: "http://localhost:8083/producto/" + productId
        return hostConfig.getBaseUrl() + "/producto/" + productId;
    }
    
    /**
     * Example method showing how to call external payment service
     */
    public String processPayment(String paymentData) {
        // Instead of: "http://localhost:8084/payment/process"
        String url = externalServicesConfig.getPaymentService() + "/payment/process";
        return restTemplate.postForObject(url, paymentData, String.class);
    }
    
    /**
     * Example method showing how to get frontend URLs for redirects or links
     */
    public String getFrontendProductUrl(Long productId) {
        // Instead of: "http://localhost:3000/products/" + productId
        return hostConfig.getFrontend() + "/products/" + productId;
    }
} 