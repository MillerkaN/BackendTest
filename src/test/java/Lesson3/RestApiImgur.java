package Lesson3;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RestApiImgur {

    static String authorizationHeader = "Bearer 0b1dda5512f1572ad44c54b080b7825491d10038";
    static String userName = "natalimiller89";
    static Map<String, String> headersAuth = new HashMap<>();
    String imageId = "D7ZORkg";
    String commentText = "Tro-lo-lo!";
    static int commentId;

    @BeforeAll
    static void setUp() {
        headersAuth.put("Authorization", authorizationHeader);
    }

    @Test
    @DisplayName("Проверка статус-кода и проверка userName")
    @Order(1)
    void getAccountInfoVerifyUrlTest() {
        String url = given()
                .headers(headersAuth)
                .log()
                .all()
                .when()
                .get("https://api.imgur.com/3/account/{<username>}", userName)
                .prettyPeek()
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response()
                .jsonPath()
                .getString("data.url");
        assertThat(url, equalTo(userName));
    }

    @Test
    @DisplayName("Успешное создание комментария")
    @Order(2)
    void testPostCommentCreation() {
        commentId = given()
                .headers(headersAuth)
                .log()
                .all()
                .formParam("image_id", imageId)
                .formParam("comment", commentText)
                .expect()
                .statusCode(200)
                .body("success", is(true))
                .body("data.id", Matchers.notNullValue())
                .log()
                .all()
                .when()
                .post("https://api.imgur.com/3/comment")
                .prettyPeek()
                .jsonPath()
                .getInt("data.id");
        assertThat(commentId, Matchers.greaterThan(0));
    }

    @Test
    @DisplayName("Успешное чтение комментария")
    @Order(3)
    void testGetComment() {
        given()
                .headers(headersAuth)
                .log()
                .all()
                .expect()
                .statusCode(200)
                .body("data.id", is(commentId))
                .body("data.image_id", is(imageId))
                .body("data.comment", is(commentText))
                .when()
                .get("https://api.imgur.com/3/comment/{commentId}", commentId)
                .prettyPeek();
    }

    @Test
    @DisplayName("Успешное удаление комментария")
    @Order(4)
    void testDeleteComment() {
        given()
                .headers(headersAuth)
                .log()
                .all()
                .expect()
                .statusCode(200)
                .when()
                .delete("https://api.imgur.com/3/comment/{commentId}", commentId)
                .prettyPeek();
    }



}

