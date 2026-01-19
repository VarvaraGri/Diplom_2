package ru.yandex.practicum.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.practicum.models.User;

import static io.restassured.RestAssured.given;
import static ru.yandex.practicum.steps.Endpoints.*;

public class UserSteps {

    @Step("Send POST request to /api/auth/register")
    public Response createUser(User user){

        return given()
                .header("Content-type", "application/json")
                .baseUri(BASE_URI)
                .body(user)
                .when()
                .post(CREATE_USER_HANDLE);
    }

    @Step("Send POST request to /api/auth/login")
    public Response loginUser(User user){

        return given()
                .header("Content-type", "application/json")
                .baseUri(BASE_URI)
                .body(user)
                .when()
                .post(LOGIN_USER_HANDLE);
    }

    @Step("Extract access token from response from request /api/auth/login")
    public String extractAccessToken(Response response) {
        return response.then().extract().path("accessToken");
    }

    @Step("Extract error message from response")
    public String extractErrorMessage(Response response) {
        return response.then().extract().path("message");
    }

    @Step("Send DELETE request to /api/auth/user")
    public Response deleteUser(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .baseUri(BASE_URI)
                .when()
                .delete(DELETE_USER_HANDLE);
    }
}
