package eu.irrationalcharm.userservice.validations;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;


import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test Validations for Usernames")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UsernameValidatorTest {

    // test<System Under Test>_<Condition or State Change>_>Expected Result>


    private UsernameValidator usernameValidator;

    @BeforeEach
    public void beforeEach() {
        usernameValidator = new UsernameValidator();
    }


    // test<System Under Test>_<Condition or State Change>_<Expected Result>
    @ParameterizedTest
    @Order(1)
    @DisplayName("Test valid usernames")
    @ValueSource(strings = {"irrational", "irrational_charm", "greenprotactiniumuue", "Domi4ikFitz", "alo", "5543"})
    void testUsernameValidator_WhenUsernameValid_ShouldReturnTrue(String username) {

        boolean isValid = usernameValidator.isValid(username, null);
        assertTrue(isValid, () -> String.format("Username %s should return true", username));
    }

    @Order(2)
    @ParameterizedTest
    @DisplayName("Test non valid usernames")
    @ValueSource(strings = {"ir", "carbohydrateworldwide", "Irrational Charm", "Irrational.Charm", "mirkito@gmail.com", "", " ", "...", "    "})
    void testUsernameValidator_WhenUsernameNotValid_ShouldReturnFalse(String username) {
        //Arrange   //Given

        //Act   //When
        boolean isValid = usernameValidator.isValid(username, null);

        //Assert    //Then
        assertFalse(isValid, () -> String.format("Username %s should return false", username));
    }



    @Order(3)
    @DisplayName("Test username as null")
    @NullSource
    @ParameterizedTest
    void testUsernameValidator_WhenUsernameIsNull_ShouldThrowNullPointerException(String username) {

        assertThrows(NullPointerException.class, () -> usernameValidator.isValid(username, null), "On null, UsernameValidator should throw NullPointerException");
    }

}
