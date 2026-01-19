package ru.yandex.practicum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.models.User;
import ru.yandex.practicum.steps.UserSteps;

import static org.hamcrest.CoreMatchers.is;

public class LoginUserTest {
    private User user;
    private UserSteps userSteps = new UserSteps();
    private String accessToken;

    @Before
    public void setUp(){
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        user = new User();
        user.setEmail("test_" + RandomStringUtils.randomAlphabetic(6) + "@example.com");
        user.setPassword(RandomStringUtils.randomAlphabetic(6));
        user.setName(RandomStringUtils.randomAlphabetic(6));
    }

    @Test
    @DisplayName("Logging in of existing user")
    @Description("Chek that user is logged in if user exists")
    public void shouldLoginExistingUser(){
        accessToken = userSteps.extractAccessToken(userSteps.createUser(user));
        userSteps.loginUser(user)
                .then()
                .body("success", is(true));
    }

    @Test
    @DisplayName("Logging in of user with wrong login")
    @Description("Chek that user isn't logged in if login(email) field is empty")
    public void shouldNotLoginUserWithWrongLogin(){
        accessToken = userSteps.extractAccessToken(userSteps.createUser(user));
        user.setEmail("test_" + RandomStringUtils.randomAlphabetic(6) + "@example.com");
        userSteps.loginUser(user)
                .then()
                .body("success", is(false));
    }

    @Test
    @DisplayName("Logging in of user with wrong password")
    @Description("Chek that user isn't logged in if password field is empty")
    public void shouldNotLoginUserWithWrongPassword(){
        accessToken = userSteps.extractAccessToken(userSteps.createUser(user));
        user.setPassword(RandomStringUtils.randomAlphabetic(6));
        userSteps.loginUser(user)
                .then()
                .body("success", is(false));
    }

    @Test
    @DisplayName("Logging in of user with wrong login and password")
    @Description("Chek that user isn't logged in if login(email) and password field is empty")
    public void shouldNotLoginUserWithWrongLoginAndPassword(){
        accessToken = userSteps.extractAccessToken(userSteps.createUser(user));
        user.setEmail("test_" + RandomStringUtils.randomAlphabetic(6) + "@example.com");
        user.setPassword(RandomStringUtils.randomAlphabetic(6));
        userSteps.loginUser(user)
                .then()
                .body("success", is(false));
    }

    @After
    public void tearDown(){
        if(accessToken != null){
            userSteps.deleteUser(accessToken);
        }
    }
}
