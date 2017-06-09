package com.meridian.ball.json;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.meridian.ball.model.Game;
import com.meridian.ball.repository.TeamRepository;

public class GameDeserializer extends JsonDeserializer<Game> {
    
    private final DateTimeFormatter formatter;
    
    public GameDeserializer() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss");
    }

    @Override
    public Game deserialize(JsonParser parser, DeserializationContext ctx) throws IOException, JsonProcessingException {
        JsonNode node = parser.readValueAsTree();
        Game game = new Game();
        game.setGameId(node.get("GAME_ID").asText());
        game.setGameCode(node.get("GAMECODE").asText());
        game.setDate(LocalDate.parse(node.get("GAME_DATE_EST").asText(), formatter));
        return game;
    }
}