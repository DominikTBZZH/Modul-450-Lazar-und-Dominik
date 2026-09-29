# Übung 2: Angular-Oberfläche testen

Lazar & Dominik, Modul 450

Das GUI wird mit Playwright in einem echten Browser getestet.

## Ausführen

Backend starten:

```bash
cd spring-boot-angular-basic-lw2
mvn spring-boot:run
```

Frontend starten:

```bash
cd spring-boot-angular-basic-lw2/src/main/js/my-app
npm start
```

Tests starten:

```bash
cd e2e
npm test
```

Ergebnis: 9 Tests, 6 grün, 3 rot. Die 3 roten sind so gewollt.
