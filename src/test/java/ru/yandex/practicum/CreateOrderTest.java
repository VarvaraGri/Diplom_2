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
import ru.yandex.practicum.models.Order;
import ru.yandex.practicum.models.User;
import ru.yandex.practicum.steps.OrderSteps;
import ru.yandex.practicum.steps.UserSteps;


import static org.hamcrest.CoreMatchers.*;

public class CreateOrderTest {
    private User user;
    private Order order;
    private String[] ingredientsIds = {"61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa75", "61c0c5a71d1f82001bdaaa70", "61c0c5a71d1f82001bdaaa78"};
    private final UserSteps userSteps = new UserSteps();
    private final OrderSteps orderSteps = new OrderSteps();
    private String accessToken;

    @Before
    public void setUp(){
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        user = new User();
        user.setEmail("test_" + RandomStringUtils.randomAlphabetic(6) + "@example.com");
        user.setPassword(RandomStringUtils.randomAlphabetic(6));
        user.setName(RandomStringUtils.randomAlphabetic(6));
        order = new Order();
    }

    @Test
    @DisplayName("Creation order by authorized user")
    @Description("Chek that order is created if user is authorized and there is at least one ingredient")
    public void shouldCreateOrderFromAuthorizedUser(){
        accessToken = userSteps.extractAccessToken(userSteps.createUser(user));
        order.setIngredients(ingredientsIds);
        orderSteps.createOrder(order, accessToken)
                .then()
                .statusCode(200)
                .body("name", notNullValue())
                .body("order.number", notNullValue())
                .body("success", is(true));
    }

    @Test
    @DisplayName("Creation order by unauthorized user")
    @Description("Chek that order is created if user isn't authorized and there is at least one ingredient")
    public void shouldCreateOrderFromUnauthorizedUser(){
        order.setIngredients(ingredientsIds);
        orderSteps.createOrder(order)
                .then()
                .statusCode(200)
                .body("name", notNullValue())
                .body("order.number", notNullValue())
                .body("success", is(true));
    }

    @Test
    @DisplayName("Fail to create order without ingredients")
    @Description("Chek that order isn't created if there no ingredients")
    public void shouldNotCreateOrderFromUnauthorizedUserWithoutIngredients(){
        String[] zeroIngredients = new String[0];
        order.setIngredients(zeroIngredients);
        orderSteps.createOrder(order)
                .then()
                .statusCode(400)
                .body("success", is(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Fail to create order with mistake in id")
    @Description("Chek that order isn't created if there mistake in ingredient's id")
    public void shouldReturnErrorMessageWithWrongIngredientId(){
        ingredientsIds[0] = "123";
        order.setIngredients(ingredientsIds);
        orderSteps.createOrder(order)
                .then()
                .statusCode(500);
    }

    @After
    public void tearDown(){
        if(accessToken != null){
            userSteps.deleteUser(accessToken);
        }
    }
}
