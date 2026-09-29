package ch.tbz.m450.testing.tools.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.HashSet;
import java.util.List;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Automatisierte Tests der REST-Schnittstelle mit REST Assured.
 *
 * <p>Die Tests sprechen die Schnittstelle ueber echtes HTTP an. Spring Boot startet dafuer Tomcat
 * auf einem freien Port, den sich der Test ueber {@link LocalServerPort} holt. Es muss also nichts
 * von Hand gestartet werden, "mvn test" genuegt.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentApiTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/students";
    }

    // --- Lesen ---------------------------------------------------------------------------------

    @Test
    @DisplayName("T-01: GET liefert 200 und JSON")
    void getReturnsJson() {
        RestAssured.when()
                .get()
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    @DisplayName("T-02: GET liefert die im CommandLineRunner angelegten Studenten")
    void getReturnsSeedData() {
        RestAssured.when()
                .get()
                .then()
                .body("name", hasItems("Jonas", "Patrick", "Yves", "Peter", "Ann"))
                .body("email", hasItem("jonas@tbz.ch"))
                .body("size()", greaterThanOrEqualTo(5));
    }

    @Test
    @DisplayName("T-03: GET haelt den vereinbarten JSON-Vertrag ein")
    void getMatchesJsonSchema() {
        RestAssured.when()
                .get()
                .then()
                .body(matchesJsonSchemaInClasspath("schemas/students-schema.json"));
    }

    @Test
    @DisplayName("T-04: jede id kommt nur einmal vor")
    void idsAreUnique() {
        List<Integer> ids = RestAssured.when().get().then().extract().jsonPath().getList("id");

        assertEquals(ids.size(), new HashSet<>(ids).size(), "doppelte id in der Antwort");
    }

    // --- Schreiben -----------------------------------------------------------------------------

    @Test
    @DisplayName("T-05: POST legt einen Studenten an, GET liefert ihn zurueck")
    void postThenGetRoundtrip() {
        String name = "Roundtrip-" + System.nanoTime();

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{\"name\": \"" + name + "\", \"email\": \"roundtrip@tbz.ch\"}")
                .when()
                .post()
                .then()
                .statusCode(200);

        RestAssured.when()
                .get()
                .then()
                .body("name", hasItem(name));
    }

    @Test
    @DisplayName("T-06: POST erhoeht die Anzahl Datensaetze um genau eins")
    void postAddsExactlyOneRow() {
        int before = RestAssured.when().get().then().extract().jsonPath().getList("id").size();

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{\"name\": \"Neu\", \"email\": \"neu@tbz.ch\"}")
                .when()
                .post()
                .then()
                .statusCode(200);

        RestAssured.when()
                .get()
                .then()
                .body("size()", equalTo(before + 1));
    }

    // --- Negativtests --------------------------------------------------------------------------

    @Test
    @DisplayName("T-07: kaputtes JSON wird mit 400 abgelehnt")
    void malformedJsonIsRejected() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{")
                .when()
                .post()
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("T-08: falscher Content-Type wird mit 415 abgelehnt")
    void wrongContentTypeIsRejected() {
        RestAssured.given()
                .contentType(ContentType.TEXT)
                .body("name=Hans")
                .when()
                .post()
                .then()
                .statusCode(415);
    }

    @Test
    @DisplayName("T-09: nicht implementierte Methode liefert 405")
    void unsupportedMethodIsRejected() {
        RestAssured.when()
                .delete()
                .then()
                .statusCode(405);
    }

    @Test
    @DisplayName("T-10: /students/{id} ist nicht implementiert und liefert 404")
    void singleStudentEndpointDoesNotExist() {
        RestAssured.when()
                .get("/1")
                .then()
                .statusCode(404);
    }

    // --- CORS ----------------------------------------------------------------------------------

    @Test
    @DisplayName("T-11: das Angular-Frontend darf zugreifen (CORS-Preflight)")
    void corsAllowsFrontendOrigin() {
        RestAssured.given()
                .header("Origin", "http://localhost:4200")
                .header("Access-Control-Request-Method", "POST")
                .when()
                .options()
                .then()
                .statusCode(200)
                .header("Access-Control-Allow-Origin", "http://localhost:4200");
    }

    @Test
    @DisplayName("T-12: eine fremde Herkunft wird vom CORS-Preflight abgewiesen")
    void corsRejectsForeignOrigin() {
        RestAssured.given()
                .header("Origin", "http://evil.example.com")
                .header("Access-Control-Request-Method", "POST")
                .when()
                .options()
                .then()
                .statusCode(403);
    }

    // --- Offene Befunde ------------------------------------------------------------------------
    // Diese drei Tests beschreiben das Soll-Verhalten und sind bewusst deaktiviert, weil das
    // Backend sie heute nicht erfuellt. Sie stehen so als ausfuehrbare Fehlermeldung im
    // Testbericht und werden gruen, sobald der jeweilige Befund behoben ist. Siehe UEBUNG-1.md.

    @Test
    @Disabled("B-01: save() macht ein Upsert, eine mitgeschickte id ueberschreibt fremde Daten")
    @DisplayName("B-01: POST mit fremder id darf keinen bestehenden Studenten ueberschreiben")
    void postMustNotOverwriteExistingStudent() {
        var ersterStudent = RestAssured.when().get().then().extract().jsonPath();
        int id = ersterStudent.getInt("[0].id");
        String name = ersterStudent.getString("[0].name");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{\"id\": " + id + ", \"name\": \"Uebernommen\", \"email\": \"boese@tbz.ch\"}")
                .when()
                .post();

        RestAssured.when()
                .get()
                .then()
                .body("find { it.id == " + id + " }.name", equalTo(name));
    }

    @Test
    @Disabled("B-02: keine Bean Validation auf der Entity, leere Werte werden gespeichert")
    @DisplayName("B-02: POST ohne Namen muss mit 400 abgelehnt werden")
    void postWithEmptyNameMustBeRejected() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{\"name\": \"\", \"email\": \"ohne-name@tbz.ch\"}")
                .when()
                .post()
                .then()
                .statusCode(400);
    }

    @Test
    @Disabled("B-03: addStudent() hat Rueckgabetyp void, Spring antwortet darum mit 200 ohne Body")
    @DisplayName("B-03: POST muss 201 und einen Location-Header liefern")
    void postMustReturnCreated() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{\"name\": \"Created\", \"email\": \"created@tbz.ch\"}")
                .when()
                .post()
                .then()
                .statusCode(201)
                .header("Location", notNullValue());
    }
}
