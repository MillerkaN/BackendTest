package Lesson4;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.*;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RestApiImg {

    static String authorizationHeader = "Bearer 6f06593c332a0f2332c7961dae200f1d03fcc5eb";
    static String userName = "natalimiller89";
    static Map<String, String> headersAuth = new HashMap<>();
    static String deleteHash;
    String imageLink = "https://smallivingworld.ru/800/600/https/fthmb.tqn.com/qyghj2M9c7URMNomzZ88Akueo6o=/2250x1500/filters:fill(auto,1)/173985454-56a008c05f9b58eba4ae9019.jpg";
    RequestSpecification requestSpecification = null;
    ResponseSpecification responseSpecification = null;

    @BeforeAll
    static void setUp() {
        headersAuth.put("Authorization", authorizationHeader);
    }

    @BeforeEach
    void beforeTest() {
        requestSpecification = new RequestSpecBuilder()
                .addHeaders(headersAuth)
                .build();
        responseSpecification = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectResponseTime(Matchers.lessThanOrEqualTo(50000L))
                .build();
        }

    @Test
    @DisplayName("Успешное добавление изображения")
    @Order(1)
    void testPostImageUpload() {
        deleteHash = given()
                .spec(requestSpecification)
                .formParam("image", imageLink)
                .formParam("type", "url")
                .expect()
                .spec(responseSpecification)
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Test
    @DisplayName("Успешное удаление изображения")
    @Order(2)
    void testDeleteImageDelete() {
        given()
                .spec(requestSpecification)
                .expect()
                .spec(responseSpecification)
                .when()
                .delete("https://api.imgur.com/3/image/{imageHash}", deleteHash );
    }

}
