# Präsentation MogBattle (10 min)

## 1. Produkt (2 min)
- Satirisches Battle-Spiel mit fiktiven Chads, als Parodie auf Mogging und Hypergamy
- Live: `./mvnw spring-boot:run`, dann http://localhost:8080 öffnen
  - Gigachad vs Average Andy → `MEGAMOGGED`
  - Mewing Max vs Tall Tom → mehrmals ausführen, der Aura-Bonus dreht das Ergebnis
  - Upgrade für Average Andy und 404 für Gigachad

## 2. Testing (2 min)
- Testpyramide: 22 Unit-Tests (Engine, Services), 12 Integrationstests (Controller, H2, Frontend) und 1 Context-Test
- Mocking: `AuraOracle` ist Zufall → Mock macht ihn deterministisch; Repository gemockt → Unit-Tests ohne DB
- Grenzwerte 4/5 und 14/15 mit `@ParameterizedTest`

## 3. Reports und Pipeline (3 min)
- GitHub → Actions → Lauf `mog-battle`: build → test → deploy
- Job-Summary: Testresultate und Line Coverage
- GitHub Pages: Surefire-Report und JaCoCo (~97 % Line Coverage)

## 4. Reflexion (2 min)
- TDD: Commits `tdd red / green / refactor` im Git-Log zeigen (`git log --oneline`)
- Red hat einen Testdaten-Fehler aufgedeckt (Mewing Max)
- Code Reviews: eigene Erfahrungen aus den PRs

## 5. Fazit (1 min)
- Kleiner Scope, dafür alles getestet
- Learning: externe und zufällige Abhängigkeiten hinter ein Interface legen, dann sind sie testbar
