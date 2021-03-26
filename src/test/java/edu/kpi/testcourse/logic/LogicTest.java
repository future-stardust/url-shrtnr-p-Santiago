package edu.kpi.testcourse.logic;

import edu.kpi.testcourse.entities.UrlAlias;
import edu.kpi.testcourse.entities.User;
import edu.kpi.testcourse.storage.UrlRepository.AliasAlreadyExist;
import edu.kpi.testcourse.storage.UrlRepositoryFakeImpl;
import edu.kpi.testcourse.storage.UserRepositoryFakeImpl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class LogicTest {

  Logic createLogic() {
    return new Logic(new UserRepositoryFakeImpl(), new UrlRepositoryFakeImpl());
  }

  Logic createLogic(UserRepositoryFakeImpl users) {
    return new Logic(users, new UrlRepositoryFakeImpl());
  }

  Logic createLogic(UrlRepositoryFakeImpl urls) {
    return new Logic(new UserRepositoryFakeImpl(), urls);
  }

  @Test
  void shouldSuccessfullyCreateANewUser() throws Logic.UserIsAlreadyCreated {
    // GIVEN
    UserRepositoryFakeImpl users = new UserRepositoryFakeImpl();
    Logic logic = createLogic(users);

    // WHEN
    logic.createNewUser("aaa@bbb.com", "password");

    // THEN
    assertThat(users.findUser("aaa@bbb.com")).isNotNull();
  }

  @Test
  void shouldNotAllowUserCreationIfEmailIsUsed() {
    // GIVEN
    UserRepositoryFakeImpl users = new UserRepositoryFakeImpl();
    users.createUser(new User("aaa@bbb.com", "hash"));
    Logic logic = createLogic(users);

    assertThatThrownBy(() -> {
      // WHEN
      logic.createNewUser("aaa@bbb.com", "password");
    })
      // THEN
      .isInstanceOf(Logic.UserIsAlreadyCreated.class);
  }

  @Test
  void shouldAuthorizeUser() throws Logic.UserIsAlreadyCreated {
    // GIVEN
    Logic logic = createLogic();

    // WHEN
    logic.createNewUser("aaa@bbb.com", "password");

    // THEN
    assertThat(logic.isUserValid("aaa@bbb.com", "password")).isTrue();
  }

  @Test
  void shouldCreateShortVersionOfUrl() {
    // GIVEN
    UrlRepositoryFakeImpl urls = new UrlRepositoryFakeImpl();
    Logic logic = createLogic(urls);

    // WHEN
    var shortUrl = logic.createNewAlias("aaa@bbb.com", "http://g.com/loooong_url", "short");

    // THEN
    assertThat(shortUrl).isEqualTo("short");
    assertThat(logic.findFullUrl("short")).isEqualTo("http://g.com/loooong_url");
  }

  @Test
  void shouldNotAllowToCreateSameAliasTwice() {
    // GIVEN
    Logic logic = createLogic();

    // WHEN
    var shortUrl = logic.createNewAlias("aaa@bbb.com", "http://g.com/loooong_url", "short");

    // THEN
    assertThatThrownBy(() -> {
      logic.createNewAlias("ddd@bbb.com", "http://d.com/laaaang_url", "short");
    }).isInstanceOf(AliasAlreadyExist.class);
  }
}
