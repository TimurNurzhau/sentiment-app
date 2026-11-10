package com.example.sentimentapp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SentimentController {

    @GetMapping("/api/sentiment")
    public Map<String, String> analyze(@RequestParam String text) {
        String t = text.toLowerCase();
        String sentiment;
        if (t.contains("good") || t.contains("love") || t.contains("great") || t.contains("excellent")) {
            sentiment = "positive";
        } else if (t.contains("bad") || t.contains("hate") || t.contains("awful") || t.contains("terrible")) {
            sentiment = "negative";
        } else {
            sentiment = "neutral";
        }
        return Map.of("text", text, "sentiment", sentiment);
    }
}