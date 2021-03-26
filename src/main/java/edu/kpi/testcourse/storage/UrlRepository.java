package edu.kpi.testcourse.storage;

import edu.kpi.testcourse.entities.UrlAlias;
import edu.kpi.testcourse.entities.User;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Stores shortened URLs.
 */
public interface UrlRepository {
  /**
   * Stores the given URL alias in the repository if it does not already exist.
   *
   * @param urlAlias an pair of full and shortened URLs
   * @throws AliasAlreadyExist if the repository already contains a URL alias with this short name.
   */
  void createUrlAlias(UrlAlias urlAlias) throws AliasAlreadyExist;

  /**
   * Returns complete information about the URL alias with the given short name.
   */
  @Nullable UrlAlias findUrlAlias(String alias);

  /**
   * Deletes the URL alias with the given short name.
   *
   * @throws RuntimeException if the repository does not contain a URL alias with this short name.
   */
  void deleteUrlAlias(String email, String alias) throws PermissionDenied;

  /**
   * Finds all URLs that belong to the user with the given email.
   */
  List<UrlAlias> getAllAliasesForUser(String userEmail);

  /**
   * Error for a case when we try to create a shortened URL that is already exist.
   */
  class AliasAlreadyExist extends IllegalStateException {
    public AliasAlreadyExist() {
      super("Storage already contains an alias");
    }
  }

  /**
   * Error for a case when a user tries to delete an alias that is not created by her/him.
   */
  class PermissionDenied extends SecurityException {
    public PermissionDenied() {
      super("This operation is not allowed for the current user");
    }
  }
}
