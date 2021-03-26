package edu.kpi.testcourse.propertybased;

import edu.kpi.testcourse.logic.Logic;
import edu.kpi.testcourse.storage.UrlRepositoryFakeImpl;
import edu.kpi.testcourse.storage.UserRepositoryFakeImpl;
import org.junit.jupiter.api.Test;

import static org.quicktheories.QuickTheory.qt;
import static org.quicktheories.generators.SourceDSL.strings;

public class LogicTestPropertyBased {

  // Example of property based test
  @Test
  void shouldAuthorizeUser_propertyBased() throws Logic.UserIsAlreadyCreated {
    qt()
      .forAll(
        strings().basicLatinAlphabet().ofLengthBetween(0, 10),
        strings().basicLatinAlphabet().ofLengthBetween(0, 10)
      ).check((email, password) -> {
      // GIVEN
      Logic logic = createLogic();

      try {
        // WHEN
        logic.createNewUser(email, password);
      } catch (Logic.UserIsAlreadyCreated userIsAlreadyCreated) {
        return false;
      }

      // THEN
      return logic.isUserValid(email, password);
    });
  }

  private Logic createLogic() {
    return new Logic(new UserRepositoryFakeImpl(), new UrlRepositoryFakeImpl()
    );
  }

}
