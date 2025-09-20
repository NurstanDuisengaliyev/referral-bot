package com.findreferral.referral_bot.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileService {

    private final WebClient telegramWebClient;
    private final String botToken;

    public FileService(WebClient telegramWebClient,
                       @Value("${bot.token}") String botToken) {
        this.telegramWebClient = telegramWebClient;
        this.botToken = botToken;
    }

    public Path downloadAndSaveCv(String fileId, String fileName) throws Exception {
        // Ask Telegram for file_path
        String filePath = telegramWebClient.get()
                .uri("/bot{token}/getFile?file_id={fileId}", botToken, fileId)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> json.get("result").get("file_path").asText())
                .block();

        // Download file bytes
        byte[] fileBytes = telegramWebClient.get()
                .uri("/file/bot{token}/{filePath}", botToken, filePath)
                .retrieve()
                .bodyToMono(byte[].class)
                .block();

        if (fileBytes == null || fileBytes.length == 0) {
            throw new IllegalStateException("Failed to download file or file is empty");
        }

        // Save locally
        String uploadDir = "uploads/cv/";
        Files.createDirectories(Paths.get(uploadDir));
        Path filePathLocal = Paths.get(uploadDir, fileName);
        Files.write(filePathLocal, fileBytes);

        return filePathLocal;
    }

}
