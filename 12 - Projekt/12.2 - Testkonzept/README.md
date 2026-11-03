# 12.2 - Testkonzept MogBattle

Testkonzept für unser Projekt [MogBattle](../mog-battle/README.md).

## 1. Testziel

- Die Spielregeln (MogScore, Verdict, Battle, Hypergamy-Upgrade) rechnen korrekt, auch an den Grenzwerten.
- Die REST-API liefert die richtigen Daten und Statuscodes (200, 201, 400, 404).
- Ungültige Eingaben werden abgelehnt.
- Die Tests sind reproduzierbar, obwohl das Spiel einen Zufall (Aura-Bonus) enthält.

## 2. Testobjekte

| Objekt | Klasse | Testebene |
|---|---|---|
| Spiellogik | `MogEngine` | Unit |
| Battle-Ablauf | `BattleService` | Unit mit Mocks |
| Upgrade-Suche | `HypergamyService` | Unit mit Mocks |
| REST-API Chads und Upgrade | `ChadController` | Integration |
| REST-API Battles | `BattleController` | Integration |
| Weboberfläche ausliefern | `static/` | Integration |

**Nicht getestet:** Klickverhalten im Browser (keine Selenium- oder Playwright-Tests), Performance, Sicherheit. Die Oberfläche haben wir von Hand geprüft.

## 3. Testebenen

| | Unit-Test | Integrationstest |
|---|---|---|
| Was läuft | eine einzelne Klasse | die ganze Spring-App |
| Abhängigkeiten | mit Mockito gemockt | echt, nur `AuraOracle` gemockt |
| Datenbank | keine | H2 In-Memory mit `data.sql` |
| Aufruf | Methode direkt | HTTP-Request über MockMvc |
| Dauer | Millisekunden | ca. 1 Sekunde pro Klasse |
| Findet | Fehler in der Logik | Fehler im Zusammenspiel: Pfade, JSON, Validierung, Statuscodes |
| Klassen | `MogEngineTest`, `BattleServiceTest`, `HypergamyServiceTest` | `ChadControllerIT`, `BattleControllerIT`, `FrontendIT` |

## 4. Testumgebung und Werkzeuge

| Werkzeug | Zweck |
|---|---|
| JUnit 5 | Testframework, `@ParameterizedTest` für Grenzwerte |
| Mockito | Mocks für `AuraOracle` und `ChadRepository` |
| Spring MockMvc | HTTP-Requests in den Integrationstests |
| H2 In-Memory | Testdatenbank, wird bei jedem Start neu mit `data.sql` befüllt |
| JaCoCo | Code Coverage |
| Surefire Report | HTML-Testreport |
| GitHub Actions | führt bei jedem Push und PR alle Tests aus, Deploy der Reports auf Pages |

Testdaten aus `data.sql`:

| id | Name | Height | Jawline | Aura | Status | Frame | Score |
|---|---|---|---|---|---|---|---|
| 1 | Gigachad | 10 | 10 | 9 | 8 | 9 | 66 |
| 2 | Average Andy | 5 | 5 | 5 | 5 | 5 | 35 |
| 3 | Mewing Max | 6 | 7 | 5 | 4 | 6 | 41 |
| 4 | Tall Tom | 9 | 5 | 4 | 5 | 5 | 42 |
| 5 | Looksmaxxing Larry | 7 | 8 | 6 | 6 | 7 | 49 |
| 6 | Sigma Sam | 6 | 6 | 9 | 7 | 8 | 48 |

## 5. Abstrakte Testfälle

Verdict nach Abstand `m` zwischen den beiden Scores:

| ID | Äquivalenzklasse | Bedingung | Erwartetes Verdict |
|---|---|---|---|
| A-01 | Ungültig | `m < 0` | Exception |
| A-02 | Gleichstand | `m == 0` | `TIE` |
| A-03 | Knapp | `1 <= m <= 4` | `CLOSE_CALL` |
| A-04 | Gemogt | `5 <= m <= 14` | `MOGGED` |
| A-05 | Deutlich gemogt | `m >= 15` | `MEGAMOGGED` |

Stats eines Chads:

| ID | Äquivalenzklasse | Bedingung | Erwartet |
|---|---|---|---|
| A-06 | Gültig | `1 <= stat <= 10` | wird gespeichert |
| A-07 | Zu klein | `stat < 1` | 400 Bad Request |
| A-08 | Zu gross | `stat > 10` | 400 Bad Request |
| A-09 | Name fehlt | Name leer | 400 Bad Request |

## 6. Konkrete Testfälle

### Unit-Tests

| ID | Beschreibung | Eingabe | Erwartetes Resultat | Effektives Resultat | Status | Testklasse |
|---|---|---|---|---|---|---|
| U-01 | MogScore berechnen | alle Stats 5 | 35 | 35 | OK | `MogEngineTest` |
| U-02 | MogScore Minimum und Maximum | alle Stats 1 / alle 10 | 7 / 70 | 7 / 70 | OK | `MogEngineTest` |
| U-03 | Verdict Grenzwerte (A-02 bis A-05) | Abstand 0, 1, 4, 5, 14, 15, 63 | TIE, CLOSE_CALL, CLOSE_CALL, MOGGED, MOGGED, MEGAMOGGED, MEGAMOGGED | wie erwartet | OK | `MogEngineTest` |
| U-04 | Negativer Abstand (A-01) | -1 | `IllegalArgumentException` | `IllegalArgumentException` | OK | `MogEngineTest` |
| U-05 | Höherer Score gewinnt | Gigachad vs Average Andy, kein Bonus | Gigachad, Abstand 31, MEGAMOGGED | wie erwartet | OK | `MogEngineTest` |
| U-06 | Reihenfolge egal | Average Andy vs Gigachad | Gigachad gewinnt | Gigachad | OK | `MogEngineTest` |
| U-07 | Aura-Bonus dreht Battle | Mewing Max +3 vs Tall Tom +0 | Mewing Max, Abstand 2, CLOSE_CALL | wie erwartet | OK | `MogEngineTest` |
| U-08 | Gleichstand | zwei gleiche Chads, je +1 | TIE, kein Gewinner | TIE, `null` | OK | `MogEngineTest` |
| U-09 | Battle ohne Bonus | Orakel-Mock gibt 0 | Tall Tom gewinnt | Tall Tom | OK | `BattleServiceTest` |
| U-10 | Bonus vom Orakel wird verwendet | Orakel-Mock gibt Max 3, Tom 0 | Mewing Max gewinnt, Orakel 2x aufgerufen | wie erwartet | OK | `BattleServiceTest` |
| U-11 | Unbekannter Chad | id 99 | `ChadNotFoundException`, Orakel nie aufgerufen | wie erwartet | OK | `BattleServiceTest` |
| U-12 | Selbst-Battle | id 1 vs id 1 | `IllegalArgumentException`, keine DB-Abfrage | wie erwartet | OK | `BattleServiceTest` |
| U-13 | Upgrade ist der nächsthöhere Chad | Average Andy (35) | Mewing Max (41), nicht Gigachad | Mewing Max | OK | `HypergamyServiceTest` |
| U-14 | Gleicher Score ist kein Upgrade | Andy und Zwilling mit 35, Tom mit 42 | Tall Tom | Tall Tom | OK | `HypergamyServiceTest` |
| U-15 | Top-Chad hat kein Upgrade | Gigachad | `NoUpgradeException` | `NoUpgradeException` | OK | `HypergamyServiceTest` |
| U-16 | Unbekannter Chad | id 42 | `ChadNotFoundException` | `ChadNotFoundException` | OK | `HypergamyServiceTest` |

### Integrationstests

| ID | Beschreibung | Eingabe | Erwartetes Resultat | Effektives Resultat | Status | Testklasse |
|---|---|---|---|---|---|---|
| I-01 | Alle Chads laden | `GET /api/chads` | 200, 6 Chads, erster ist Gigachad | wie erwartet | OK | `ChadControllerIT` |
| I-02 | Chad anlegen (A-06) | `POST /api/chads` Rizz Ricardo | 201 mit id, danach 7 Chads | wie erwartet | OK | `ChadControllerIT` |
| I-03 | Ungültige Stats (A-07, A-08) | Height 11, Aura 0 | 400 | 400 | OK | `ChadControllerIT` |
| I-04 | Leerer Name (A-09) | Name `""` | 400 | 400 | OK | `ChadControllerIT` |
| I-05 | Upgrade über API | `GET /api/chads/3/upgrade` | 200, Tall Tom mit 42 | wie erwartet | OK | `ChadControllerIT` |
| I-06 | Kein Upgrade an der Spitze | `GET /api/chads/1/upgrade` | 404, Meldung "Spitze" | wie erwartet | OK | `ChadControllerIT` |
| I-07 | Battle über API | `POST /api/battles` 1 vs 2, Orakel 0 | 200, Gigachad, 31, MEGAMOGGED | wie erwartet | OK | `BattleControllerIT` |
| I-08 | Unbekannte id | 1 vs 999 | 404, "Chad mit id 999 existiert nicht" | wie erwartet | OK | `BattleControllerIT` |
| I-09 | Selbst-Battle | 1 vs 1 | 400 | 400 | OK | `BattleControllerIT` |
| I-10 | Fehlende id | nur `chadAId` | 400 | 400 | OK | `BattleControllerIT` |
| I-11 | Startseite | `GET /index.html` | 200, enthält "MogBattle" und `app.js` | wie erwartet | OK | `FrontendIT` |
| I-12 | JS und CSS | `GET /app.js`, `GET /style.css` | 200 | 200 | OK | `FrontendIT` |
| S-01 | App startet | Spring-Kontext laden | kein Fehler | kein Fehler | OK | `MogBattleApplicationTest` |

## 7. Abschlusskriterien

| Kriterium | Ziel | Stand |
|---|---|---|
| Alle Tests grün | 100 % | 35 von 35 grün |
| Line Coverage | über 80 % | ca. 97 % |
| Branch Coverage | über 80 % | 100 % |
| Pipeline auf `main` | grün | grün, Reports auf GitHub Pages |

Reports: https://dominiktbzzh.github.io/Modul-450-Lazar-und-Dominik/
