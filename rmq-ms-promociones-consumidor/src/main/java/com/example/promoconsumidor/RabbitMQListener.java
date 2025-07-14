package com.example.promoconsumidor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQListener {

    private static final String OUTPUT_DIR = ".";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    @RabbitListener(queues = "promociones")
    public void handleMessage(String message) {
        try {
            File outputDir = new File(OUTPUT_DIR);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
            String filename = timestamp + ".json";
            File outputFile = new File(outputDir, filename);

            try (FileWriter writer = new FileWriter(outputFile)) {
                writer.write(message);
            }

            System.out.println("Message written to file: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error writing message to file: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 