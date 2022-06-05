package apitests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class APITests {

    static String loginToken;
    static Integer postID;

    @BeforeTest
    public void loginTest() throws JsonProcessingException {
        // create new LoginPOJO class object named login
        LoginPOJO login = new LoginPOJO();

        // set the login credentials to our login object
        login.setUsernameOrEmail("test51");
        login.setPassword("test51");

        // Convert pojo object to json using Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        String convertedJson = objectMapper.writeValueAsString(login);
        System.out.println("CONVERTED JSON IS: ");
        System.out.println(convertedJson);

        baseURI = "http://training.skillo-bg.com:3100";

        Response response = given()
                .header("Content-Type", "application/json")
                .body(convertedJson)
                .when()
                .post("/users/login");
        response
                .then()
                .statusCode(201);

        // convert the response body json into a string
        String loginResponseBody = response.getBody().asString();

        loginToken = JsonPath.parse(loginResponseBody).read("$.token");

    }

    @Test(dependsOnMethods = "addPost")
    public void likePost() {
        // create an object of ActionsPOJO class and add value for the fields
        ActionsPOJO likePost = new ActionsPOJO();
        likePost.setAction("likePost");

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + loginToken)
                .body(likePost)
                .when()
                .patch("/posts/" + postID)
                .then()
                .body("post.id", equalTo(postID))
                .log()
                .all();

    }

    @Test(dependsOnMethods = "addPost")
    public void commentPost() {
        ActionsPOJO commentPost = new ActionsPOJO();
        commentPost.setContent("My New Comment!");

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + loginToken)
                .body(commentPost)
                .when()
                .post("/posts/"+postID+"/comment")
                .then()
                .body("content", equalTo("My New Comment!"))
                .log()
                .all()
                .statusCode(201);
    }

    @Test()
    public void addPost() {
        ActionsPOJO createPost = new ActionsPOJO();
        createPost.setCaption("My new pic.");
        createPost.setPostStatus("public");
        createPost.setCoverUrl("https://i.imgur.com/zY7dp6C.gif");
        ValidatableResponse validatableResponse = given()
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + loginToken)
                        .body(createPost)
                        .log()
                        .all()
                        .when()
                        .post("/posts")
                        .then()
                        .log()
                        .all()
                        .assertThat().body("caption", equalTo(createPost.getCaption()))
                        .assertThat().statusCode(201);
        postID = validatableResponse.extract().path("id");
        }
    @Test(dependsOnMethods = {"addPost","likePost","commentPost"})
    public void deletePost(){
        ActionsPOJO deletePost = new ActionsPOJO();
        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + loginToken)
                .body(deletePost)
                .when()
                .delete("/posts/" + postID)
                .then()
                .log()
                .all()
                .assertThat().statusCode(200)
                .assertThat().body("msg",equalTo("Post was deleted!"));

    }


}
