package com.meridian.ball.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.meridian.ball.model.Player;

public class PlayerSerializer extends JsonSerializer<Player> {
    
    @Override
    public void serialize(Player player, JsonGenerator gen, SerializerProvider s) throws IOException, JsonProcessingException {
        gen.writeStartObject();
        gen.writeObjectField("playerId", player.getPlayerId());
        gen.writeObjectField("displayName", player.getDisplayName());
        gen.writeEndObject();
    }
}
