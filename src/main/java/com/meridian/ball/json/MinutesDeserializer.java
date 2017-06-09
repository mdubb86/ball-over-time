package com.meridian.ball.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class MinutesDeserializer extends JsonDeserializer<Double> {
    
    @Override
    public Double deserialize(JsonParser parser, DeserializationContext ctx) throws IOException, JsonProcessingException {
        JsonNode node = parser.readValueAsTree();
        String s = node.asText();
        int idx = s.indexOf(":");
        if (idx == -1) {
            return null;
        }
        int minutes = Integer.parseInt(s.substring(0, idx));
        int seconds = Integer.parseInt(s.substring(idx + 1));
        double partialMinutes = seconds / 60.0;
        return minutes + partialMinutes;
    }
}