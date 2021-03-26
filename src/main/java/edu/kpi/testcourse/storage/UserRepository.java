package edu.kpi.testcourse.storage;

import edu.kpi.testcourse.entities.User;
import javax.annotation.Nullable;

/**
 * Stores user profiles, only the information necessary for signing in.
 */
public interface UserRepository {
  /**
   * Stores the given user in the repository if it does not already exist.
   *
   * @throws RuntimeException if the user already exists.
   */
  void createUser(User user);

  /**
   * Finds a user by email.
   *
   * @return complete information about the user with the given email or null if the user does not
   *          exist.
   */
  @Nullable User findUser(String email);
}
