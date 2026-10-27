# Code Reviews – Recipe Planner (M450)

Dokumentation zu den Aufgaben 1 und 2 (Code Reviews).
Kriterien gemäss Theorie *„Qualität des Codes – ein paar Beispiele“*: Kommentare, Naming, Logik, Typos,
Zeilenlänge, unnötiger Code, Tests, YAGNI/DRY/KISS, SRP, „besser hinterlassen als vorgefunden“.

Projekt: `recipe-planner-backend/` und `recipe-planner-frontend/` in diesem Ordner (Kopie des Stands aus
`9 - CI-CD Pipelines`, damit die PRs nur die Feature-Änderungen zeigen).

> Hinweis zum Setup: Der Ordnername enthält ein `&`. Unter Windows bricht deshalb `npm test` / `npm start`
> (die `.cmd`-Shims interpretieren `&` als Befehlstrenner). Workaround:
> `node node_modules/react-scripts/bin/react-scripts.js test` bzw. `... start`. Backend: `./mvnw verify` funktioniert.

## Übersicht Branches / Pull Requests

| Aufgabe | Branch | Basis | Inhalt | Grösse |
|---|---|---|---|---|
| 1 | `feature/add-recipe` | `main` | Frontend: neues Recipe hinzufügen | +202 / −131 |
| 2 | `feature/edit-recipe-backend` | `main` | Backend: `PUT /api/recipes/recipe/{id}` | +149 / −5 |
| 2 | `feature/edit-recipe-frontend` | `feature/add-recipe` | Frontend: Recipe editieren | +220 / −80 |

Alle PRs liegen im empfohlenen Bereich von 200–400 geänderten Zeilen.
**Merge-Reihenfolge:** `feature/add-recipe` → `feature/edit-recipe-backend` → `feature/edit-recipe-frontend`
(der Frontend-Edit-PR baut auf dem Add-PR auf, weil er dessen Formular wiederverwendet).

### Testergebnisse

| Teil | Tests | Resultat |
|---|---|---|
| Frontend (`AddRecipe.test.js`, `EditRecipe.test.js`) | 9 | ✅ alle grün, `npm run build` ohne Lint-Warnungen |
| Backend (bestehende Tests + neu `RecipeUpdateIntegrationTest`, MockMvc + H2) | 18 (davon 4 neu) | ✅ alle grün (`./mvnw verify`) |
| Manuell End-to-End (Browser) | Recipe editieren, speichern, Browse zeigt neuen Namen; unbekannte ID → 404 | ✅ |

---

## Aufgabe 1 – Recipe hinzufügen (Frontend)

### PR-Beschreibung (zum Kopieren)

> **feat(frontend): add new recipe**
>
> - Formular „Add Recipe“ sendet per `POST /api/recipes` an das Backend (neues Modul `src/apis/recipeApi.js`)
> - Zutaten-Zeilen mit `useFieldArray` von react-hook-form (vorher: eigener State, der nie mit den Inputs verbunden war)
> - Felder passen zum Backend-Modell (`name`, `unit`, `amount` statt `ingredient` / `quantity`)
> - Validierung: Rezeptname Pflicht, Zutat braucht Namen und Menge ≥ 1
> - Fehlermeldung bei Backend-Fehler, nach Erfolg zurück zu „Browse“
> - Aufräumen: ungenutzte Imports in `App.js`, fehlende `key`-Props in `Browse.js`, doppelte `controlId`s
> - Tests: 5 Komponententests für `AddRecipe`
>
> **Testen:** Backend starten, Frontend starten, „Add Recipes“ → Formular ausfüllen → Submit → neues Rezept erscheint in Browse.

### Self-Review nach Theorie-Kriterien

| Kriterium | Beobachtung |
|---|---|
| Logik | Ausgangslage hatte keinen Submit-Handler, Inputs waren nicht verbunden, `<Form>` fehlte → Submit tat nichts. Behoben. |
| Naming | Frontend-Felder `ingredient`/`quantity` passten nicht zum Backend (`name`/`amount`) → vereinheitlicht. Typo „Quanity“ korrigiert. |
| Unnötiger Code | Ungenutzte Imports (`findAllByDisplayValue`, `Container`, `Nav`, `axios`, `watch`, `ingredients`-State) entfernt. |
| DRY | Unit-Liste als Konstante `UNITS` statt 5× `<option>`; Backend-URL zentral in `recipeApi.js`. |
| KISS | `useFieldArray` statt eigener `listId`-Verwaltung und manueller `update/remove`-Funktionen. |
| Kommentare | Nur dort, wo es nicht offensichtlich ist (`UNITS` muss zum Backend-Enum passen). |
| Tests | Happy Path, Validierung Name, Validierung Menge, Zutat entfernen, Backend-Fehler. |
| Offen / Follow-up | Backend-URL ist hart codiert (`localhost:8080`) → besser über `REACT_APP_API_URL`. Route `/new-menues` (Typo) bewusst nicht umbenannt (ausserhalb Scope). |

---

## Aufgabe 2 – Recipe editieren (Backend + Frontend)

### PR-Beschreibung Backend (zum Kopieren)

> **feat(backend): edit existing recipe**
>
> - Neuer Endpoint `PUT /api/recipes/recipe/{recipeId}` (gleiches Pfadschema wie das bestehende `GET`)
> - Aktualisiert Name, Beschreibung, Bild-URL; die Zutatenliste wird als Ganzes ersetzt (`orphanRemoval` löscht alte Zeilen)
> - Unbekannte ID → `404 Not Found` (`RecipeNotFoundException`), auch für `GET` (vorher `200` mit leerem Body)
> - Fix: `RecipeRepository` hatte `Long` als ID-Typ, die Entity verwendet `UUID`
> - Tests: 4 neue Integrationstests (`RecipeUpdateIntegrationTest`: Update, Zutaten leeren, 404 bei PUT und GET)
> - Bestehender Test `getRecipe_unknownId_returnsEmptyBody` angepasst: erwartet jetzt 404 statt 200 mit leerem Body

### PR-Beschreibung Frontend (zum Kopieren)

> **feat(frontend): edit existing recipe** – *basiert auf `feature/add-recipe`*
>
> - Formular aus `AddRecipe` in wiederverwendbare Komponente `RecipeForm` extrahiert
> - Neue Seite `EditRecipe` unter `/recipes/:recipeId/edit`: lädt das Rezept, füllt das Formular vor, speichert per `PUT`
> - Button „Edit Details“ auf jeder Rezept-Karte verlinkt auf die Edit-Seite
> - Fehlermeldungen beim Laden und Speichern
> - Tests: 4 Komponententests für `EditRecipe`

### Self-Review nach Theorie-Kriterien

| Kriterium | Beobachtung |
|---|---|
| DRY / SRP | `RecipeForm` ist die einzige Formular-Implementierung; `AddRecipe`/`EditRecipe` liefern nur Titel, Startwerte und `onSave`. |
| Logik | Im Service wird die Entity geladen und aktualisiert statt blind `save()` mit neuer Entity – so entstehen keine Duplikate und eine falsche ID gibt 404. Die ID aus dem Pfad gewinnt, eine ID im Body wird ignoriert. |
| Logik (Trade-off) | Zutaten bekommen bei jedem Edit neue IDs (Liste wird ersetzt). Einfach (KISS) und korrekt, solange niemand die Zutaten-IDs referenziert. Diskussionspunkt. |
| Robustheit | Backend liefert `null` für leere Felder → `EditRecipe` entfernt `null`-Werte, damit die Inputs kontrolliert bleiben. `ingredients: null` im Request wird wie eine leere Liste behandelt. |
| Scope | Die 404-Änderung beim `GET` ist streng genommen ein Nebenschauplatz – aber nötig, damit die Edit-Seite einen Fehler anzeigen kann. Diskussionspunkt. |
| Tests | Backend: echte Integration mit H2 (kein Mocking des Repositories). Frontend: API-Modul gemockt. |
| Offen / Follow-up | Keine serverseitige Validierung (`@Valid`, `@NotBlank`) – Name darf im Backend leer sein. Kein Optimistic Locking (`@Version`) bei gleichzeitigem Editieren. Globale CSS-Regeln in `RecipeForm.css` (`body`, `button`) wirken auf die ganze App (bestehend). Test-Setup (`MemoryRouter` + Routes) ist in beiden Testdateien dupliziert. |

---

## Review des Partners

Pro PR des Partners ausfüllen (Kommentare direkt im PR schreiben, hier zusammenfassen).
Leitlinien: respektvoll und konstruktiv, Begründung immer mitliefern, Stilfragen als Vorschlag formulieren
(„Dies wäre auch noch eine Möglichkeit …“), Dinge ausserhalb des Scopes als Follow-up-Ticket statt im PR.

| PR | Kriterium | Datei:Zeile | Kommentar | Schwere (Blocker / Vorschlag / Nit) | Erledigt |
|---|---|---|---|---|---|
| | | | | | |

---

## Vergleich und Erkenntnisse (nach der gemeinsamen Diskussion ergänzen)

**Unterschiede zwischen den beiden Implementierungen**

- Formular-State: `useFieldArray` vs. eigener State? →
- Wiederverwendung Add/Edit: gemeinsame Komponente oder zwei Formulare? →
- Backend-Update: Entity laden und ändern vs. `save()` mit neuer Entity? Zutaten ersetzen vs. einzeln mergen? →
- Fehlerbehandlung (404, Fehlermeldung im UI)? →
- Tests vorhanden / welche Fälle? →

**Was haben die Reviews gefunden, das wir selbst übersehen hatten?**

-

**Erkenntnisse**

- Gleiches Feature, unabhängig umgesetzt, führt zu sehr unterschiedlichen Lösungen – das Review zwingt dazu, die eigene Variante zu begründen.
- Kleine PRs (< 400 Zeilen) mit klarer Beschreibung und Testanleitung lassen sich deutlich schneller und gründlicher reviewen.
- Abhängige PRs (Frontend-Edit baut auf Frontend-Add auf) müssen in der Beschreibung und in der Merge-Reihenfolge klar kommuniziert werden.
- Viele Findings betreffen nicht das Feature selbst, sondern bestehenden Code (Typos, ungenutzte Imports, falscher ID-Typ). Diese sauber vom Feature trennen bzw. als Follow-up erfassen.
- Tests im PR machen das Review leichter: der Reviewer sieht sofort, welche Fälle abgedeckt sind und welche fehlen.
-
