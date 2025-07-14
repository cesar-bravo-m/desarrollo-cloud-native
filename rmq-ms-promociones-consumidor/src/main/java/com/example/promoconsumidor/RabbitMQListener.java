package com.example.promoconsumidor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Component
public class RabbitMQListener {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
    
    @Value("${aws.s3.access-point-arn}")
    private String s3AccessPointArn;
    
    @Value("${aws.region}")
    private String awsRegion;
    
    @Value("${aws.accessKeyId}")
    private String accessKeyId;
    
    @Value("${aws.secretKey}")
    private String secretKey;
    
    private final S3Client s3Client;
    
    public RabbitMQListener(@Value("${aws.region}") String region, 
                           @Value("${aws.accessKeyId}") String accessKeyId,
                           @Value("${aws.secretKey}") String secretKey) {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKeyId, secretKey);
        this.s3Client = S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
            .build();
    }

    @RabbitListener(queues = "promociones")
    public void handleMessage(String message) {
        try {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
            String key = "promociones/" + timestamp + ".json";

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3AccessPointArn)
                .key(key)
                .contentType("application/json")
                .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromString(message));

            System.out.println("Message written to S3: " + s3AccessPointArn + "/" + key);
        } catch (S3Exception e) {
            System.err.println("Error writing message to S3: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 