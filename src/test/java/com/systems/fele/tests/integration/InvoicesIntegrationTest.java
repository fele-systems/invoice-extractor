package com.systems.fele.tests.integration;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import com.systems.fele.users.dto.UserDto;
import com.systems.fele.users.dto.UserRegisterRequest;
import com.systems.fele.users.service.AppUserService;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@DirtiesContext
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class InvoicesIntegrationTest {
    @LocalServerPort
    int port;

    @Autowired
    AppUserService appUserService;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    static final String TEST_USER_USERNAME = "tonisho.kyouko@weebmail.com";
    static final String TEST_USER_PASSWORD = "aStringPassword";
    
    @Test
    void User_should_see_invoices_after_adding() {

        RestAssured.given()
            .auth()
                .basic("admin@fele-systems.com", "12345678")
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(new UserRegisterRequest(
                "Toshino",
                "Kyouko",
                TEST_USER_USERNAME,
                TEST_USER_PASSWORD,
                false
            ))
            .when()
            .post("/rest/api/users/register")
            .then()
            .log().all()
            .statusCode(200)
            .extract() 
            .as(UserDto.class);
        
        var authedReqSpec = RestAssured.given()
            .auth()
                .basic(TEST_USER_USERNAME, TEST_USER_PASSWORD)
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON);

        authedReqSpec.when()
            .get("/rest/api/invoices")
            .then()
            .log().all()
            .statusCode(200)
            .body("$", Matchers.hasSize(0));
        
        authedReqSpec.with().body("""
                { "dueDate": "2020-01-05", "expenses": [] }
                """.strip())
            .when()
            .post("/rest/api/invoices")
            .then()
            .statusCode(200);

        authedReqSpec.when()
            .get("/rest/api/invoices")
            .then()
            .statusCode(200)
            .body("$", Matchers.hasSize(1));
    }

}
