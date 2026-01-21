package ru.yandex.practicum.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.practicum.models.Order;

import static io.restassured.RestAssured.given;
import static ru.yandex.practicum.steps.Endpoints.*;

public class OrderSteps {

    @Step("Send POST request to /api/orders with authorization token")
    public Response createOrder(Order order, String accessToken){

        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .baseUri(BASE_URI)
                .body(order)
                .when()
                .post(CREATE_ORDER_HANDLE);
    }

    @Step("Send POST request to /api/orders without authorization token")
    public Response createOrder(Order order) {

        return given()
                .header("Content-type", "application/json")
                .baseUri(BASE_URI)
                .body(order)
                .when()
                .post(CREATE_ORDER_HANDLE);
    }
}
