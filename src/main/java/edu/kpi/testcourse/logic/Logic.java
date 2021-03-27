package edu.kpi.testcourse.logic;


import edu.kpi.testcourse.entities.UrlAlias;
import edu.kpi.testcourse.entities.User;
import edu.kpi.testcourse.storage.UrlRepository;
import edu.kpi.testcourse.storage.UrlRepository.AliasAlreadyExist;
import edu.kpi.testcourse.storage.UserRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Business logic of the URL shortener application.
 */
public class Logic {
  /**
   * Create variable.
   */
  private final UserRepository users;
  private final UrlRepository urls;
  private final HashUtils hashUtils;
  public Map<String, Map<String, String>> allMap = new HashMap<>();

  /**
   * Creates an instance.
   */
  public Logic(UserRepository users, UrlRepository urls) {
    this.users = users;
    this.urls = urls;
    this.hashUtils = new HashUtils();
  }

  /**
   * Create a new user.
   *
   * @param email users email
   * @param password users password
   * @throws UserIsAlreadyCreated is thrown if user is already created
   */
  public void createNewUser(String email, String password) throws UserIsAlreadyCreated {
    if (users.findUser(email) != null) {
      throw new UserIsAlreadyCreated();
    } else {
      users.createUser(new User(email, hashUtils.generateHash(password)));
    }
  }

  /**
   * Gives an answer if user is registered and password is correct.
   *
   * @param email a users email
   * @param password a users password
   * @return if user is registered and password is correct
   */
  public boolean isUserValid(String email, String password) {
    User user = users.findUser(email);
    if (user == null) {
      return false;
    }

    return hashUtils.validatePassword(password, user.passwordHash());
  }

  /**
   * Create a new URL alias (shortened version).
   *
   * @param email an email of a user that creates the alias
   * @param url a full URL
   * @param alias a proposed alias
   *
   * @return a shortened URL
   */
  public String createNewAlias(String email, String url, String alias) throws AliasAlreadyExist {
    String finalAlias;
    Map<String, String> allUrl = new HashMap<>();

    if (alias == null || alias.isEmpty()) {
      String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
      Random random = new Random();
      StringBuilder stringBuilder = new StringBuilder(5);
      for (int i = 0; i < 5; i++) {
        stringBuilder.append(alphabet.charAt(random.nextInt(alphabet.length())));
      }
      finalAlias = stringBuilder.toString();
    } else {
      finalAlias = alias;
    }

    allUrl.put(finalAlias, url);
    if (!allMap.containsKey(email)) {
      allMap.put(email, allUrl);
    } else {
      allMap.get(email).put(finalAlias, url);
    }
    urls.createUrlAlias(new UrlAlias(finalAlias, url, email));

    return finalAlias;
  }

  /**
   * Get full URL by alias.
   *
   * @param alias a short URL alias
   * @return a full URL
   */
  public String findFullUrl(String alias) {
    UrlAlias urlAlias = urls.findUrlAlias(alias);

    if (urlAlias != null) {
      return urlAlias.destinationUrl();
    }

    return null;
  }

  /**
   * Get URL's data.
   * The first String parameter of Map contains a user email
   * and the second parameter contains one more Map with Strings alias and url
   *
   * @return {email:{alias:url}}
   */
  public Map<String, Map<String, String>> dataCreation() {
    Map<String, Map<String, String>> data = new HashMap<>();
    Map<String, Map<String, String>> fullUrl = allMap;
    Map<String, Map<String, String>> answer = new HashMap<>();

    for (Map.Entry<String, Map<String, String>> entry : fullUrl.entrySet()) {
      String email = entry.getKey();
      Map<String, String> aliasUrl = entry.getValue();
      if (!answer.containsKey(email)) {
        answer.put(email, aliasUrl);
      } else {
        answer.get(email).put(aliasUrl.entrySet().toArray()[0].toString(),
          aliasUrl.entrySet().toArray()[1].toString());
      }
      for (Map.Entry<String, Map<String, String>> entry2 : answer.entrySet()) {
        String emailData = entry2.getKey();
        Map<String, String> aliasUrlData = entry.getValue();
        if (!data.containsKey(emailData)) {
          data.put(emailData, aliasUrlData);
        }
      }
    }
    return data;
  }

  /**
   * Get All Aliases For User.
   *
   * @param email an email of a user that we get
   * @return Aliases by user email
   */
  public List<UrlAlias> getUserAliases(String email) {
    return urls.getAllAliasesForUser(email);
  }

  /**
   * Delete URL's by alias.
   *
   * @param email an email of a user whose URL we delete
   * @param alias a short URL alias that we delete
   * @return a response
   */
  public boolean deleteUrl(String email, String alias) {
    List<UrlAlias> userAliases = urls.getAllAliasesForUser(email);
    for (UrlAlias userAlias : userAliases) {
      if (userAlias.alias().equals(alias)) {
        urls.deleteUrlAlias(email, alias);
        return true;
      }
    }
    return false;
  }

  /**
   * Error for situation when we are trying to register already registered user.
   */
  public static class UserIsAlreadyCreated extends Throwable {
    public UserIsAlreadyCreated() {
      super("User with such email is already created");
    }
  }

}
