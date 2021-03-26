package edu.kpi.testcourse.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Type;

/**
 * JSON serialization tool that uses Jackson as engine.
 */
public class JsonToolJacksonImpl implements JsonTool {

  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public <T> T fromJson(String jsonString, Type type) throws JsonParsingError {
    try {
      return mapper.readValue(jsonString, new TypeReference<T>() {
        @Override
        public Type getType() {
          return type;
        }
      });
    } catch (JsonProcessingException e) {
      throw new JsonParsingError(e);
    }
  }

  @Override
  public <T> T fromJson(String jsonString, Class<T> clazz) {
    try {
      return mapper.readValue(jsonString, clazz);
    } catch (JsonProcessingException e) {
      throw new JsonParsingError(e);
    }
  }

  @Override
  public String toJson(Object obj) {
    try {
      return mapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error during object-JSON serialization", e);
    }
  }
}
