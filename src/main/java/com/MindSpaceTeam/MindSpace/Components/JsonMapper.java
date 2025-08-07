package com.MindSpaceTeam.MindSpace.Components;

import com.MindSpaceTeam.MindSpace.Exception.InvalidJsonFormatException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class JsonMapper {

    private final ObjectMapper objectMapper;

    public JsonMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JsonNode toJsonNode(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new InvalidJsonFormatException("Json parsing exception");
        }
    }
}
