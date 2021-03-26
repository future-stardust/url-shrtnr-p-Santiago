package edu.kpi.testcourse;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kpi.testcourse.logic.Logic;
import edu.kpi.testcourse.logic.UrlShortenerConfig;
import edu.kpi.testcourse.serialization.JsonTool;
import edu.kpi.testcourse.serialization.JsonToolJacksonImpl;
import edu.kpi.testcourse.storage.UrlRepository;
import edu.kpi.testcourse.storage.UrlRepositoryFakeImpl;
import edu.kpi.testcourse.storage.UserRepository;
import edu.kpi.testcourse.storage.UserRepositoryFakeImpl;
import io.micronaut.context.annotation.Factory;
import java.nio.file.Paths;
import javax.inject.Singleton;

/**
 * Factory for all injectable classes in our application.
 */
@Factory
public class BeanFactory {
  private final String baseUrl = "http://localhost:8080";

  @Singleton
  UrlRepository createUrlRepository() {
    return new UrlRepositoryFakeImpl();
  }

  @Singleton
  UserRepository createUserRepository() {
    return new UserRepositoryFakeImpl();
  }

  @Singleton
  Logic createLogic(UserRepository users, UrlRepository urls) {
    return new Logic(users, urls);
  }

  @Singleton
  JsonTool createObjectMapper() {
    return new JsonToolJacksonImpl();
  }

  @Singleton
  UrlShortenerConfig createUrlShortenerConfig() {
    return new UrlShortenerConfig(Paths.get("/home/user/url-shortener-db"));
  }
}
