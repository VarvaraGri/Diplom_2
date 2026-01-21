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

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.is;

public class CreateUserTest {

    private User user;
    private final UserSteps userSteps = new UserSteps();
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
    @DisplayName("Creation of user")
    @Description("Chek that user is created if all required fields are filled in")
    public void shouldCreateUniqueUser(){
        userSteps.createUser(user)
                .then()
                .statusCode(SC_OK)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Creation of second same user returns 403")
    @Description("Chek that user isn't created if same user was already created")
    public void shouldReturnCode403CreateSameUser(){
        userSteps.createUser(user);
        accessToken = userSteps.extractAccessToken(userSteps.loginUser(user));
        userSteps.createUser(user)
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("message", is("User already exists"));
    }

    @Test
    @DisplayName("Creation of user without email returns 403")
    @Description("Chek that user isn't created if email field is empty")
    public void shouldReturnCode403CreateUserWithoutEmail(){
        user.setEmail("");
        userSteps.createUser(user)
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("message", is("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Creation of user without password returns 403")
    @Description("Chek that user isn't created if password field is empty")
    public void shouldReturnCode403CreateUserWithoutPassword(){
        user.setPassword("");
        userSteps.createUser(user)
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("message", is("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Creation of user without name returns 403")
    @Description("Chek that user isn't created if name field is empty")
    public void shouldReturnCode403CreateUserWithoutName(){
        user.setName("");
        userSteps.createUser(user)
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("message", is("Email, password and name are required fields"));
    }

    @After
    public void tearDown(){
        accessToken = userSteps.extractAccessToken(userSteps.loginUser(user));
        if(accessToken != null){
            userSteps.deleteUser(accessToken);
        }
    }
}
