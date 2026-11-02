# 11 - Test Driven Development

Lazar & Dominik, Modul 450

| Aufgabe | Abgabe |
|---------|--------|
| 1 - Setup environment | [Setup](#aufgabe-1-setup-environment) |
| 2 - Methoden implementieren | [FancyApp/](FancyApp/), [MyFancyClass.php](FancyApp/Application/MyFancyClass.php) |
| 3 - Auswerten | [Auswertung](#aufgabe-3-auswerten) |

## Aufgabe 1: Setup environment

PHP und PHPUnit lokal unter Windows, ohne IDE:

```powershell
scoop install php
```

Scoop legt `php.ini` nicht automatisch an, darum laufen `mbstring` und `openssl` erst nach einem
Eintrag in `%USERPROFILE%\scoop\apps\php\current\cli\conf.d\extensions.ini`:

```ini
extension_dir="C:\Users\<user>\scoop\apps\php\current\ext"
extension=mbstring
extension=openssl
```

PHPUnit 11 als Phar (braucht kein Composer):

```powershell
curl.exe -L -o phpunit.phar https://phar.phpunit.de/phpunit-11.phar
```

Tests im Ordner `FancyApp` ausführen:

```powershell
php phpunit.phar --testdox Testcases/Test.php
```

### Anpassung an Test.php

Die Vorlage erbt von `PHPUnit_Framework_TestCase`, das gibt es seit PHPUnit 6 nicht mehr. Wie im
Kommentar der Vorlage vorgesehen erbt `Test` jetzt von `PHPUnit\Framework\TestCase`. Der
`require_once` nutzt `__DIR__`, sonst funktioniert er nur, wenn man im Ordner `Testcases` steht.
An den Testfällen selbst ist nichts verändert.

## Aufgabe 2: Methoden implementieren

Vorher, mit leeren Methoden:

```
FFF                                                                 3 / 3 (100%)

1) Test::testShortString
Failed asserting that null matches expected 'Ich bin Text mit 35 Char Länge (35)'.
2) Test::testCalcAverage
Failed asserting that null matches expected 5.
3) Test::testGetOpposite
Failed asserting that null matches expected '4.5,5,5.5'.

FAILURES!
Tests: 3, Assertions: 5, Failures: 3.
```

Nachher:

```
 ✔ Short string
 ✔ Calc average
 ✔ Get opposite

OK (3 tests, 27 assertions)
```

Jede Methode ist ein eigener Commit, nach jedem Commit ist ein Test mehr grün.

**shortString($text, $length, $ending = '...')**

- Ist `$ending` länger als `$length`, gibt es `false`. Diese Prüfung kommt zuerst, darum gibt
  auch ein leerer Text mit zu langer Endung `false`.
- Passt der Text in `$length`, kommt er unverändert zurück.
- Sonst wird auf `$length - Länge($ending)` gekürzt und `$ending` angehängt, das Resultat ist
  also genau `$length` Zeichen lang.
- Gezählt wird mit `mb_strlen`. Der 35-Zeichen-Text enthält ein `ä` und ist mit `strlen` 36 Byte
  lang - damit würde `shortString($text35Char, 35, ...)` den Text kürzen statt ihn zurückzugeben.

**calcAverage($values)**

- Leeres Array: `0`.
- Ein Wert, der nicht `is_numeric` ist (`'bestanden'`): `false`.
- Sonst `array_sum / count`. Numerische Strings wie `'4.5'` rechnet PHP dabei automatisch um.

**getOpposite($value, $delimiter = ',')**

- Array: `implode` zum String. `5.0` als Float wird dabei zu `5`, als String bleibt es `5.0`.
- String: `explode` zum Array.
- Alles andere (z. B. ein Objekt): `false`. Ein leeres Trennzeichen ergibt ebenfalls `false`, weil
  `explode` damit ab PHP 8 einen `ValueError` wirft.

## Aufgabe 3: Auswerten

Die Tests legen nicht alles fest. An diesen Stellen kann eine andere Gruppe die Tests genauso
bestehen und trotzdem ein anderes Verhalten haben - darauf achten wir beim Vergleich:

| Stelle | Unsere Lösung | Mögliche Abweichung |
|--------|---------------|---------------------|
| Zeichen zählen in `shortString` | `mb_strlen` / `mb_substr` | `strlen` / `substr` zählt Bytes. Der Test mit dem `ä` (35 Zeichen, 36 Byte) erkennt das, eine solche Lösung wäre also nicht grün. |
| Gesamtlänge in `shortString` | Text + Endung = `$length` | Text auf `$length` kürzen und Endung zusätzlich anhängen. Der Test mit dem 130-Zeichen-Text würde das erkennen. |
| Reihenfolge der Prüfungen in `shortString` | Zuerst Endung prüfen | Zuerst prüfen, ob der Text passt - dann wäre der leere Text mit langer Endung nicht `false`, der Test erkennt das. |
| Ungültige Werte in `calcAverage` | `is_numeric` | Cast mit `(float)` und Vergleich, oder `array_filter`. `'1e3'` oder `' 5'` werden je nach Variante unterschiedlich behandelt. |
| Rückgabetyp von `calcAverage` | `int` oder `float` (`0`, `5.0`) | Gerundete Werte, oder immer `float`. `assertEquals` vergleicht nicht typgenau, darum fällt das nicht auf. |
| Leeres Trennzeichen in `getOpposite` | `false` | Ungeprüft: `ValueError`. Wird nicht getestet. |
| Arrays mit Unterarrays in `getOpposite` | `implode` gibt `Array` mit Warnung aus | Vorher prüfen und `false` liefern. Wird nicht getestet. |

Vergleich mit den anderen Gruppen (Text-Compare-Tool, z. B. WinMerge oder `git diff --no-index`):

| Gruppe | Gleich implementiert? | Gleiche Funktion? | Abweichungen |
|--------|-----------------------|-------------------|--------------|
| _offen_ | | | |

**Fazit:** Die Tests beschreiben das Verhalten für die getesteten Fälle eindeutig, aber nicht für
alle Eingaben. Unterschiede zwischen den Gruppen sind deshalb vor allem bei Randfällen zu erwarten,
die kein Test abdeckt. Wer eine Anforderung sicher haben will, muss sie als Test aufschreiben.
