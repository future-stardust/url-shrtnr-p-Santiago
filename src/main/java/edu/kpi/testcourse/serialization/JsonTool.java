package edu.kpi.testcourse.serialization;

import java.lang.reflect.Type;

/**
 * Wrapper for JSON serialization/deserialization tool.
 */
public interface JsonTool {

  /**
   * Create an object (with generics in type) from JSON string.
   *
   * <p>Example:
   * <pre>
   * json.fromJson(s, new TypeReference&#60;HashMap&#60;String, String&#62;&#62;() {}.getType());
   * </pre>
   *
   * @param jsonString an object in JSON form
   * @param type an object type. To get type you could use Jackson TypeReference or GSON TypeToken.
   * @return a deserialized object
   */
  <T> T fromJson(String jsonString, Type type);

  /**
   * Create an object from JSON string.
   *
   * @param jsonString an object in JSON form
   * @param clazz a class of deserialized object
   * @return a deserialized object
   */
  <T> T fromJson(String jsonString, Class<T> clazz);

  /**
   * Serialize object into JSON form.
   *
   * @param obj an object for serialization
   * @return JSON string
   */
  String toJson(Object obj);

  /**
   * JSON parsing error.
   */
  class JsonParsingError extends IllegalArgumentException {
    public JsonParsingError(Throwable e) {
      super("Error during JSON parsing", e);
    }
  }
}
