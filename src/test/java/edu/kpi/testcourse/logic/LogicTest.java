package edu.kpi.testcourse.logic;

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
    assertThatThrownBy(() -> logic.createNewAlias("ddd@bbb.com", "http://d.com/laaaang_url", "short")).isInstanceOf(AliasAlreadyExist.class);
  }

  @Test
  void generatedAliasIsNotEmpty() {
    // GIVEN
    Logic logic = createLogic();

    // WHEN
    var generatedAlias = logic.createNewAlias("aaa@bbb.com", "http://g.com/loooong_url", "");

    // THEN
    assertThat(generatedAlias).isNotEmpty();
  }

  @Test
  void shouldSaveLinkAccordingToUser() {
    // GIVEN
    Logic logic = createLogic();
    var user1_alias1 = logic.createNewAlias("aaa@bbb.com", "http://g.com/loooong_url", "user1_1");
    var user1_alias2 = logic.createNewAlias("aaa@bbb.com", "http://g.com/loooong_url", "user1_2");
    var user2_alias1 = logic.createNewAlias("zzz@yyy.com", "http://h.com/shooort_url", "user2_1");

    // WHEN
    logic.dataCreation();

    // THEN
    assertThat(logic.allMap.get("aaa@bbb.com").size()).isEqualTo(2);
    assertThat(logic.allMap.get("zzz@yyy.com").size()).isEqualTo(1);
  }

  @Test
  void shouldDeleteUserAlias() {
    // GIVEN
    Logic logic = createLogic();
    var user1_alias1 = logic.createNewAlias("aaa@bbb.com", "https://www.amazon.com/Python-Crash-Course-2nd-Edition/dp/1593279280?ref_=Oct_s9_apbd_obs_hd_bw_b1CMa&pf_rd_r=D34V93AGCPE3PE9GRB4Z&pf_rd_p=19f1f22d-65de-5355-be40-832831e45eb5&pf_rd_s=merchandised-search-10&pf_rd_t=BROWSE&pf_rd_i=285856", "amazon");
    var user2_alias1 = logic.createNewAlias("zzz@yyy.com", "https://youtu.be/s3Ejdx6cIho", "GOD");

    // WHEN
    logic.deleteUrl("aaa@bbb.com", "amazon");
    logic.deleteUrl("aaa@bbb.com", "GOD");

    // THEN
    assertThat(logic.findFullUrl("amazon")).isNull();
    assertThat(logic.findFullUrl("GOD")).isNotNull();
  }

}
