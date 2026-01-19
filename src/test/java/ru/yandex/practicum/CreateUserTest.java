package ru.yandex.practicum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.models.User;
import ru.yandex.practicum.steps.UserSteps;

import static org.hamcrest.CoreMatchers.is;

public class CreateUserTest {

    private User user;
    private final UserSteps userSteps = new UserSteps();
    private String accessToken;
    private String errorMessage;

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
                .body("success", is(true));
        accessToken = userSteps.extractAccessToken(userSteps.loginUser(user));
    }

    @Test
    @DisplayName("Creation of second same user")
    @Description("Chek that user isn't created if same user was already created")
    public void shouldNotCreateSameUser(){
        userSteps.createUser(user);
        accessToken = userSteps.extractAccessToken(userSteps.loginUser(user));
        errorMessage = userSteps.extractErrorMessage(userSteps.createUser(user));
        Assert.assertEquals("User already exists", errorMessage);
    }

    @Test
    @DisplayName("Creation of user without email")
    @Description("Chek that user isn't created if email field is empty")
    public void shouldNotCreateUserWithoutEmail(){
        user.setEmail("");
        userSteps.createUser(user);
        errorMessage = userSteps.extractErrorMessage(userSteps.createUser(user));
        Assert.assertEquals("Email, password and name are required fields", errorMessage);
    }

    @Test
    @DisplayName("Creation of user without password")
    @Description("Chek that user isn't created if password field is empty")
    public void shouldNotCreateUserWithoutPassword(){
        user.setPassword("");
        userSteps.createUser(user);
        errorMessage = userSteps.extractErrorMessage(userSteps.createUser(user));
        Assert.assertEquals("Email, password and name are required fields", errorMessage);
    }

    @Test
    @DisplayName("Creation of user without name")
    @Description("Chek that user isn't created if name field is empty")
    public void shouldNotCreateUserWithoutName(){
        user.setName("");
        userSteps.createUser(user);
        errorMessage = userSteps.extractErrorMessage(userSteps.createUser(user));
        Assert.assertEquals("Email, password and name are required fields", errorMessage);
    }

    @After
    public void tearDown(){
        if(accessToken != null){
            userSteps.deleteUser(accessToken);
        }
    }
}
