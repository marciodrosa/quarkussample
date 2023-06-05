package br.com.quarkussample;

import io.quarkus.panache.mock.PanacheMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class PersonResourceTest {

    @InjectMock
    Session session;

    @BeforeEach
    public void setUp() {
        PanacheMock.mock(Person.class);
        Mockito.doNothing().when(session).persist(Mockito.any());
    }

    @Test
    public void testListEndpoint() {
        Person john = new Person();
        john.name = "John";
        Person paul = new Person();
        paul.name = "Paul";
        Mockito.when(Person.listAll()).thenReturn(Arrays.asList(john, paul));
        given()
        .when()
            .get("/people")
        .then()
            .statusCode(200)
            .body("name", hasItems("John", "Paul"));
    }

    @Test
    public void testGetByIdEndpoint() {
        Person john = new Person();
        john.id = 10L;
        john.name = "John";
        Mockito.when(Person.findById(10L)).thenReturn(john);
        given()
        .when()
            .get("/people/10")
        .then()
            .statusCode(200)
            .body("name", is("John"));
    }

    @Test
    public void testGetByIdEndpointShouldReturnNullWhenNotFound() {
        Mockito.when(Person.findById(10)).thenReturn(null);
        given()
        .when()
            .get("/people/10")
        .then()
            .statusCode(204)
            .body(is(""));
    }

    @Test
    public void testCreateEndpoint() throws IOException {
        Person john = new Person();
        john.name = "John";
        john.id = 10L;
        given()
            .contentType("application/json")
            .body(new ObjectMapper().writeValueAsString(john))
        .when()
            .post("/people")
        .then()
            .statusCode(201)
            .header("location", containsString("people/10"));
        Mockito.verify(session, Mockito.times(1)).persist(Mockito.any());
    }

    @Test
    public void testUpdateEndpoint() throws IOException {
        Person john = new Person();
        john.name = "John";
        john.id = 10L;
        Mockito.when(Person.findById(10L)).thenReturn(john);
        given()
            .contentType("application/json")
            .body(new ObjectMapper().writeValueAsString(john))
        .when()
            .put("/people/10")
        .then()
            .statusCode(200);
        Mockito.verify(session, Mockito.times(1)).persist(john);
    }

    @Test
    public void testUpdateEndpointShouldReturn404WhenObjectDoesntExist() throws IOException {
        Person john = new Person();
        john.name = "John";
        john.id = 10L;
        Mockito.when(Person.findById(10L)).thenReturn(null);
        given()
            .contentType("application/json")
            .body(new ObjectMapper().writeValueAsString(john))
        .when()
            .put("/people/10")
        .then()
            .statusCode(404);
        Mockito.verify(session, Mockito.times(0)).persist(Mockito.any());
    }

    @Test
    public void testDeleteEndpointShouldReturn404WhenObjectDoesntExist() {
        Mockito.when(Person.findById(10L)).thenReturn(null);
        given()
        .when()
            .delete("/people/10")
        .then()
            .statusCode(404);
    }

    @Test
    public void testSearchEndpoint() {
        Person john = new Person();
        john.id = 10L;
        john.name = "John";
        Mockito.when(Person.findByName("John")).thenReturn(john);
        given()
        .when()
            .get("/people/search/John")
        .then()
            .statusCode(200)
            .body("name", is("John"));
    }

    @Test
    public void testSearchEndpointShouldReturnNullWhenNotFound() {
        Mockito.when(Person.findByName("John")).thenReturn(null);
        given()
        .when()
            .get("/people/search/John")
        .then()
            .statusCode(204)
            .body(is(""));
    }

    @Test
    public void testCountEndpoint() {
        Mockito.when(Person.count()).thenReturn(10L);
        given()
        .when()
            .get("/people/count")
        .then()
            .statusCode(200)
            .body(is("10"));
    }
}
