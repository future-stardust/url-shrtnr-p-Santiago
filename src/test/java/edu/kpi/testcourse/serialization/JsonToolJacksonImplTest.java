package edu.kpi.testcourse.serialization;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.HashMap;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JsonToolJacksonImplTest {

  private JsonToolJacksonImpl json;

  @BeforeEach
  void setUp() {
    json = new JsonToolJacksonImpl();
  }

  @Test
  void shouldSerializeDeserializeMap() {
    // GIVEN
    var map = new HashMap<String, String>();
    map.put("a", "b");
    map.put("c", "d");

    // WHEN
    var s = json.toJson(map);
    var result = json.fromJson(s, new TypeReference<HashMap<String, String>>() {}.getType());

    // THEN
    assertThat(result).isEqualTo(map);
  }

  @Test
  void shouldSerializeDeserializeRecord() {
    // GIVEN
    record Clazz(@JsonProperty("a") String a, @JsonProperty("b") int b) {};
    var a = new Clazz("test", 12);

    // WHEN
    var s = json.toJson(a);
    var result = json.fromJson(s, Clazz.class);

    // THEN
    assertThat(result).isEqualTo(a);
  }

  @Test
  void shouldSerializeDeserializeObject() {
    // GIVEN
    var a = new Clazz("test", 12);

    // WHEN
    var s = json.toJson(a);
    var result = json.fromJson(s, Clazz.class);

    // THEN
    assertThat(result).isEqualTo(a);
  }

  static class Clazz {
    public String a;
    public int b;

    public Clazz(@JsonProperty("a") String a, @JsonProperty("b") int b) {
      this.a = a;
      this.b = b;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Clazz clazz = (Clazz) o;
      return b == clazz.b && Objects.equals(a, clazz.a);
    }

    @Override
    public int hashCode() {
      return Objects.hash(a, b);
    }
  }
}
