package com.grupo11.ms_productos.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "external.services")
public class ExternalServicesConfig {
    
    private String userService;
    private String orderService;
    private String paymentService;
    
    // Getters and Setters
    public String getUserService() {
        return userService;
    }
    
    public void setUserService(String userService) {
        this.userService = userService;
    }
    
    public String getOrderService() {
        return orderService;
    }
    
    public void setOrderService(String orderService) {
        this.orderService = orderService;
    }
    
    public String getPaymentService() {
        return paymentService;
    }
    
    public void setPaymentService(String paymentService) {
        this.paymentService = paymentService;
    }
} 