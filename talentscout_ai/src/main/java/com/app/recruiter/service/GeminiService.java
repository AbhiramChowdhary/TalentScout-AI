package com.app.recruiter.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private boolean fallbackUsed = false;
    
    @PostConstruct
    public void init() {
        System.out.println("Gemini + Intelligent Fallback Service Loaded");
    }

   
    
    public boolean isFallbackUsed() {
        return fallbackUsed;
    }
    
    
    
    public String askGemini(String prompt) {
    	fallbackUsed = false;
        try {

            String endpoint =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key="
                    + apiKey;

            URL url = new URL(endpoint);

            HttpURLConnection conn =
                    (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String body =
            "{ \"contents\": [{ \"parts\": [{ \"text\": \"" +
            prompt.replace("\"", "\\\"").replace("\n", " ") +
            "\" }] }] }";

            OutputStream os = conn.getOutputStream();
            os.write(body.getBytes("UTF-8"));
            os.flush();
            os.close();

            int code = conn.getResponseCode();

            BufferedReader br;

            if (code >= 200 && code < 300) {
                br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
            } else {
                br = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream()));
            }

            String line;
            StringBuilder response = new StringBuilder();

            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            br.close();

            String raw = response.toString();

            System.out.println("Gemini Raw Response: " + raw);

            // If quota or error -> fallback
            if (raw.contains("\"error\"") ||
                raw.contains("RESOURCE_EXHAUSTED") ||
                raw.contains("429")) {

            	fallbackUsed = true;
            	return fallbackResponse(prompt);
            	
            }

            return extractText(raw);

        } catch (Exception e) {

        	fallbackUsed = true;
        	return fallbackResponse(prompt);
        }
    }

    private String extractText(String json) {

        try {

            int start = json.indexOf("\"text\":\"");

            if (start == -1) {
                start = json.indexOf("\"text\": \"");
            }

            if (start == -1) {
                return fallbackResponse("generic");
            }

            start = json.indexOf("\"", start + 6) + 1;

            StringBuilder output = new StringBuilder();

            boolean escape = false;

            for (int i = start; i < json.length(); i++) {

                char ch = json.charAt(i);

                if (escape) {

                    if (ch == 'n') {
                        output.append('\n');
                    } else {
                        output.append(ch);
                    }

                    escape = false;
                    continue;
                }

                if (ch == '\\') {
                    escape = true;
                    continue;
                }

                if (ch == '"') {
                    break;
                }

                output.append(ch);
            }

            return output.toString().trim();

        } catch (Exception e) {


            fallbackUsed = true;
            return fallbackResponse("generic");
        }
    }

    private String fallbackResponse(String prompt) {

        String p = prompt.toLowerCase();

        // JD Parsing fallback
        if (p.contains("extract skills")) {
            return "Skills: Java, Spring Boot, MySQL | Experience: 3+ years | Role: Backend Developer";
        }

        // Candidate evaluation fallback
        if (p.contains("matchscore")) {

            int score = 50;

            if (p.contains("java")) score += 20;
            if (p.contains("spring")) score += 15;
            if (p.contains("mysql")) score += 10;
            if (p.contains("experience: 4")) score += 10;
            if (p.contains("experience: 5")) score += 15;

            if (score > 95) score = 95;

            return "MatchScore: " + score +
                   "\nReason: Strong fit based on matching technical skills and experience." +
                   "\nOutreach: Hi Candidate, your profile aligns with our opportunity. Interested in discussing further?";
        }

        // Outreach fallback
        if (p.contains("recruiter outreach")) {
            return "Hi Candidate, your profile matches our role. Would you be open to a quick discussion?";
        }

        // Generic fallback
        return "AI-powered response generated by internal recruiter intelligence engine.";
    }



	
}