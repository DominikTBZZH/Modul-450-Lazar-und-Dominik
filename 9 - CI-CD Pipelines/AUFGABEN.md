# CI/CD Pipelines – Recipe Planner

Lazar & Dominik, Modul 450

Aufgabe: [Aufgaben CI/CD](https://gitlab.com/ch-tbz-it/Stud/m450/m450/-/tree/main/Unterlagen/ci-cd-pipeline)
Projekt: [recipe-planner-backend](recipe-planner-backend), Frontend nur zum Verständnis: [recipe-planner-frontend](recipe-planner-frontend)

## Ausgangslage

Backend und Frontend stammen aus dem Zip der Lehrperson. Das Backend ist Spring Boot mit einem
`RecipeController` (3 Endpunkte), einem `RecipeService`, H2 und zwei MapStruct-Mappern für die
Domänenklassen `Recipe` und `Ingredient`. Tests gab es keine.

Damit das Projekt mit unserem JDK (25) baut, haben wir im `pom.xml` folgendes angepasst:

| Was | Vorher | Nachher | Grund |
|-----|--------|---------|-------|
| Java | 20 bzw. 17 | 21 | LTS, gleich wie in der Pipeline |
| Lombok | 1.18.30 | 1.18.48 | alte Version kompiliert unter JDK 25 nicht |
| Byte Buddy | 1.14.10 | 1.17.7 | Mockito/Hibernate laufen sonst nicht auf JDK 25 |

Den Maven Wrapper (`mvnw`) haben wir aus dem Addressbook-Projekt übernommen.
Spring Boot bleibt auf 3.2.1. Mit 3.5 bricht der Start ab, weil Hibernate 6.6 beim Speichern von
Rezepten mit vorgegebener UUID einen `StaleObjectStateException` wirft.

## Starten

Terminal im Ordner `recipe-planner-backend`:

```
.\mvnw.cmd verify            # Tests + Reports
.\mvnw.cmd spring-boot:run   # Server auf http://localhost:8080
```

Frontend (Ordner `recipe-planner-frontend`): `npm install` und `npm start`, läuft auf
http://localhost:3000 und spricht mit dem Backend auf Port 8080.

## Aufgabe 1 – Unit Testing

### Controller (`RecipeControllerTest`, MockMvc)

`@WebMvcTest` lädt nur den Web-Teil. Service, Mapper und Repository sind `@MockBean`. Das Repository
braucht es, weil der `CommandLineRunner` in `RecipePlannerApplication` die Testdaten sonst nicht
anlegen kann und der Kontext nicht startet.

| ID | Request | Erwartet |
|----|---------|----------|
| C-01 | `GET /api/recipes` mit 2 Rezepten | 200, JSON-Liste mit 2 Einträgen, Namen und Zutat stimmen |
| C-02 | `GET /api/recipes` ohne Rezepte | 200, leere Liste |
| C-03 | `GET /api/recipes/recipe/{id}` bekannte ID | 200, alle Felder inkl. Zutaten |
| C-04 | `GET /api/recipes/recipe/{id}` unbekannte ID | 200, leerer Body (so verhält sich der Code) |
| C-05 | `GET /api/recipes/recipe/keine-uuid` | 400, Service wird nicht aufgerufen |
| C-06 | `POST /api/recipes` mit Rezept | 200, gespeichertes Rezept zurück, ArgumentCaptor prüft das übergebene Rezept |
| C-07 | `POST /api/recipes` ohne Body | 400, Service wird nicht aufgerufen |

### Mapper (`IngredientEntityMapperTest`, `RecipeEntityMapperTest`, SoftAssertions)

Die Mapper werden mit `Mappers.getMapper(...)` ohne Spring erzeugt.

| ID | Mapper | Test |
|----|--------|------|
| M-01 | Ingredient | Entity → Domain, alle 5 Felder |
| M-02 | Ingredient | Domain → Entity, auch `comment = null` |
| M-03 | Ingredient | Listen in beide Richtungen (`new SoftAssertions()` + `assertAll()`) |
| M-04 | Ingredient | `null` bleibt `null` (Objekt und Listen) |
| M-05 | Recipe | Entity → Domain inkl. Zutatenliste |
| M-06 | Recipe | Domain → Entity inkl. Zutatenliste |
| M-07 | Recipe | Rezept ohne Zutaten/Beschreibung und `null` |

**Vorteil von SoftAssertions:** Ein normales `assertThat` bricht beim ersten Fehler ab. Danach sieht
man nicht, ob noch weitere Felder falsch sind. SoftAssertions sammeln alle Fehler und melden sie am
Ende gemeinsam (`assertSoftly` bzw. `assertAll()`). Bei einem Mapper mit vielen Feldern sieht man so
nach einem einzigen Lauf alle falsch gemappten Felder.

## Aufgabe 2 – Reports

Im `pom.xml` erzeugen drei Plugins die Reports bei jedem `mvnw verify` automatisch:

| Plugin | Ergebnis |
|--------|----------|
| `maven-surefire-plugin` | XML pro Testklasse in `target/surefire-reports/` |
| `maven-surefire-report-plugin` (`report-only`, Phase `test`) | HTML-Testreport `target/site/surefire-report.html` |
| `jacoco-maven-plugin` 0.8.14 (`prepare-agent` + `report`) | Coverage `target/site/jacoco/index.html` |

## Aufgabe 3 – Pipeline

Weil unser Repo auf GitHub liegt, nutzen wir GitHub Actions statt `.gitlab-ci.yml`. Die Datei liegt im
Repo-Root unter [`.github/workflows/recipe-planner.yml`](../.github/workflows/recipe-planner.yml).

Sie startet bei jedem Push und Pull Request, der das Backend oder den Workflow ändert, und lässt sich
auch von Hand starten. Die Stages aus der Theorie sind drei Jobs, die nacheinander laufen (`needs`):

| Job | Was passiert |
|-----|--------------|
| `build` | JDK 21 (Temurin) mit Maven-Cache, `mvnw compile` |
| `test` | `mvnw verify` → Tests, Surefire- und JaCoCo-Report. Testresultate in der Job-Summary, Reports als Artifact `test-reports-<Nr>` |
| `deploy` | nur auf `main`: Reports auf GitHub Pages veröffentlichen |

Die Reports werden auch erzeugt und hochgeladen, wenn Tests rot sind (`if: always()`).

Wo man den Report pro Lauf sieht:
- **Actions → Lauf → Summary:** Testresultate und Artifact zum Herunterladen
- **GitHub Pages:** https://dominiktbzzh.github.io/Modul-450-Lazar-und-Dominik/ (Report vom letzten Lauf auf `main`)

Pages ist im Repo eingeschaltet (Settings → Pages → Source: **GitHub Actions**).

## Gegenprobe

- `mvnw verify`: 14 Tests, alle grün. Coverage 88 % (Controller und beide Mapper 100 % bzw. 97 %).
  `RecipeService` hat 0 %, weil die Aufgabe nur Controller und Mapper verlangt.
- Mit drei falschen Erwartungen in M-01 meldet der Test `Multiple Failures (3 failures)` und listet
  alle drei Felder auf. Mit einem normalen `assertThat` wäre nur der erste Fehler gekommen.
- Backend gestartet: `GET /api/recipes`, `GET /api/recipes/recipe/{id}` und `POST /api/recipes`
  antworten wie erwartet.
