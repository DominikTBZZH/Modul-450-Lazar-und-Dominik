# Aufgabe 1

Projekt: [addressbook-backend-v1-1](addressbook-backend-v1-1)

## Starten

Terminal im Ordner `addressbook-backend-v1-1`:

```
.\mvnw.cmd test              # Tests
.\mvnw.cmd spring-boot:run   # Server auf http://localhost:8080
```

## Was wir gemacht haben

23 Tests, alle grün: Entity (2), Comparator (4), Service (4), Controller (3), Sortierfelder aus
Aufgabe 2 (9), dazu ein Test für den Start der Anwendung.

`@BeforeEach` baut die Testdaten in jeder Klasse neu auf, dazu `@DisplayName`, `@ParameterizedTest`
mit `@EnumSource`, `@Mock`, `@InjectMocks`, `@Captor` und `@SpringBootTest`.

Der Service läuft im Test ohne H2. Statt des Repositories bekommt er einen Mock,
`when(...).thenReturn(...)` gibt vor, was dieser liefert, `verify(...)` und ein ArgumentCaptor
prüfen, was der Service weitergibt.

Der Comparator der Vorgabe gab immer `-1` zurück und sortierte gar nicht. Jetzt sortiert er nach
Nachname, Vorname und Registrierungsdatum, fehlende Werte landen am Ende.

## Gegenprobe

Mit `return -1` fallen 9 der 23 Tests durch.
