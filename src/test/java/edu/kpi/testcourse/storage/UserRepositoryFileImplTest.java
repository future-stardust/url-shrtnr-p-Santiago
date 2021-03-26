package edu.kpi.testcourse.storage;

import edu.kpi.testcourse.entities.User;
import edu.kpi.testcourse.logic.UrlShortenerConfig;
import edu.kpi.testcourse.serialization.JsonToolJacksonImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserRepositoryFileImplTest {
  UrlShortenerConfig appConfig;
  UserRepository userRepository;

  @BeforeEach
  void setUp() {
    try {
      appConfig = new UrlShortenerConfig(
        Files.createTempDirectory("user-repository-file-test"));
      Files.write(appConfig.storageRoot().resolve("user-repository.json"), "{}".getBytes());
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    userRepository = new UserRepositoryFileImpl(new JsonToolJacksonImpl(), appConfig);
  }

  @AfterEach
  void tearDown() {
    try {
      Files.delete(appConfig.storageRoot().resolve("user-repository.json"));
      Files.delete(appConfig.storageRoot());
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  @Test
  void createsOneUser() {
    // GIVEN
    var email = "user1@example.org";
    var user = new User(email, "hash1");

    // WHEN
    userRepository.createUser(user);

    // THEN
    assertThat(userRepository.findUser(email)).isEqualTo(user);
  }

  @Test
  void serializesOneUser() throws IOException {
    // GIVEN
    var email = "user1@example.org";
    var user = new User(email, "hash1");

    // WHEN
    userRepository.createUser(user);

    // THEN
    assertThat(
      Files.readString(appConfig.storageRoot().resolve("user-repository.json"), StandardCharsets.UTF_8))
      .isEqualTo("{\"user1@example.org\":{\"email\":\"user1@example.org\",\"passwordHash\":\"hash1\"}}");
  }

  @Test
  void deserializesOneUser() {
    // GIVEN
    var email = "user1@example.org";
    var user = new User(email, "hash1");
    userRepository.createUser(user);

    // WHEN
    // The new repository instance must read the data in constructor.
    userRepository = new UserRepositoryFileImpl(new JsonToolJacksonImpl(), appConfig);

    // THEN
    assertThat(userRepository.findUser(email)).isEqualTo(user);
  }

  @Test
  void doesNotCreateDuplicateUser() {
    // GIVEN
    var email = "user1@example.org";
    var user = new User(email, "hash1");
    userRepository.createUser(user);

    // WHEN + THEN
    assertThrows(RuntimeException.class, () -> userRepository.createUser(user));
  }

  @Test
  void findsCorrectUser() {
    // GIVEN
    var email1 = "user1@example.org";
    var user1 = new User(email1, "hash1");
    var email2 = "user2@example.org";
    var user2 = new User(email2, "hash2");

    // WHEN
    userRepository.createUser(user1);
    userRepository.createUser(user2);

    // THEN
    assertThat(userRepository.findUser(email1)).isEqualTo(user1);
    assertThat(userRepository.findUser(email2)).isEqualTo(user2);
  }

  @Test
  void doesNotFindNonexistentUser() {
    // GIVEN + WHEN
    // see setUp().

    // THEN
    assertThat(userRepository.findUser("user1@example.org")).isEqualTo(null);
  }

}
