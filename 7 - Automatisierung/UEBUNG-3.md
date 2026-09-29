# Übung 3: Backend unter Last

Lazar & Dominik, Modul 450

Das Backend wird mit k6 belastet. Der Test ist eine JavaScript-Datei.

## Was k6 kann

- **Virtuelle Nutzer (VUs)** – jeder ist eine Schleife, die das Skript immer wieder ausführt
- **Stufen** – hochfahren, halten, runterfahren. Zeigt ab wann es kippt, nicht nur ob
- **Schwellen** – z.B. p95 unter 500ms. Gerissen heisst Fehlercode 99, damit kann eine Pipeline abbrechen
- **Checks** – Prüfungen pro Antwort, z.B. Status 200
- **Kennzahlen** – p95 statt Durchschnitt, der Durchschnitt versteckt die Ausreisser

## Ergebnisse

| Skript | Was | Nutzer | Anfragen/s | p95 | Fehler |
|--------|-----|--------|-----------|-----|--------|
| `lesen.js` | GET, 7 Einträge | 20 | 14'721 | 2,5 ms | 0 |
| `steigerung.js` | GET, Nutzer hochfahren | bis 300 | 16'331 | 25,6 ms | 0 |
| `schreiben.js` | POST | 10 | 12'914 | 2,0 ms | 0 |
| `lesen-voll.js` | GET, 387'455 Einträge | 3 | 1,9 | 1,67 s | 0 |

Ab etwa 16'000 Anfragen/s ist Schluss, mehr Nutzer bringen nur längere Wartezeiten. `GET /students`
gibt immer alle Datensätze zurück, nach dem Schreibtest ist eine Antwort 19 MB gross. Fehler gab es
in keinem Lauf, die Anwendung wird nur langsam.

## Ausführen

Backend starten:

```bash
cd spring-boot-angular-basic-lw2
mvn spring-boot:run
```

Test starten:

```bash
cd lasttest
k6 run lesen.js
```

Nach `schreiben.js` das Backend neu starten, sonst bleiben die Testdaten in der Datenbank.
