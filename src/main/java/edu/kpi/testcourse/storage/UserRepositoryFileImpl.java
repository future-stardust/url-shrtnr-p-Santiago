package edu.kpi.testcourse.storage;

import com.google.gson.reflect.TypeToken;
import edu.kpi.testcourse.entities.User;
import edu.kpi.testcourse.logic.UrlShortenerConfig;
import edu.kpi.testcourse.serialization.JsonTool;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import javax.inject.Inject;

/**
 * A file-backed implementation of {@link UserRepository} suitable for use in production.
 */
public class UserRepositoryFileImpl implements UserRepository {
  // User profiles, keyed by email.
  private final Map<String, User> users;

  private final JsonTool jsonTool;
  private final UrlShortenerConfig appConfig;

  /**
   * Creates an instance.
   */
  @Inject
  public UserRepositoryFileImpl(JsonTool jsonTool, UrlShortenerConfig appConfig) {
    this.jsonTool = jsonTool;
    this.appConfig = appConfig;
    this.users = readUsersFromJsonDatabaseFile(jsonTool, makeJsonFilePath(appConfig.storageRoot()));
  }

  @Override
  public synchronized void createUser(User user) {
    if (users.putIfAbsent(user.email(), user) != null) {
      throw new RuntimeException("User already exists");
    }
    writeUsersToJsonDatabaseFile(jsonTool, users, makeJsonFilePath(appConfig.storageRoot()));
  }

  @Override
  public synchronized @Nullable User findUser(String email) {
    return users.get(email);
  }

  private static Path makeJsonFilePath(Path storageRoot) {
    return storageRoot.resolve("user-repository.json");
  }

  private static Map<String, User> readUsersFromJsonDatabaseFile(
      JsonTool jsonTool, Path sourceFilePath
  ) {
    String json;
    try {
      json = Files.readString(sourceFilePath, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    Type type = new TypeToken<HashMap<String, User>>(){}.getType();
    Map<String, User> result = jsonTool.fromJson(json, type);
    if (result == null) {
      throw new RuntimeException("Could not deserialize the user repository");
    }
    return result;
  }

  private static void writeUsersToJsonDatabaseFile(
      JsonTool jsonTool, Map<String, User> users, Path destinationFilePath
  ) {
    String json = jsonTool.toJson(users);
    try {
      Files.write(destinationFilePath, json.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
