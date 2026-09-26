package com.druv.team_task_board.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class Slack_Notification_Service {


    @Value("${slack.webhook.url}")
    private String webhookUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Async
    public void sendNotification(String message) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = Map.of("text", message);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            restTemplate.postForEntity(webhookUrl, request, String.class);
        } catch (Exception e) {
            // Deliberately swallow errors here — a failed Slack notification
            // should never cause the actual task operation to fail.
            System.err.println("Failed to send Slack notification: " + e.getMessage());
        }
    }
}