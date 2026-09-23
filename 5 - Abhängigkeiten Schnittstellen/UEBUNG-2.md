# Aufgabe 2

Starten und Testen wie in [UEBUNG-1.md](UEBUNG-1.md).

## Was wir gemacht haben

Der Comparator sortiert jetzt nach jedem Attribut und in beide Richtungen, dafür zwei neue Enums:

```java
public enum SortField { ID, FIRSTNAME, LASTNAME, PHONENUMBER, REGISTRATION_DATE }
public enum SortDirection { ASC, DESC }
```

Aufruf: `new AddressComparator(FIRSTNAME)`, `new AddressComparator(DESC, PHONENUMBER)` oder
`new AddressComparator(REGISTRATION_DATE, LASTNAME)` für zwei Felder nacheinander. Der leere
Konstruktor sortiert wie in Aufgabe 1, der Service bleibt unverändert.

9 Tests in `AddressComparatorSortFieldTest`: alle fünf Felder, `DESC`, zwei Felder kombiniert und
die Fehlerfälle mit `IllegalArgumentException`. Die drei Testadressen ergeben pro Attribut eine
andere Reihenfolge, ein Test kann also nicht zufällig grün sein. Der `@ParameterizedTest` mit
`@EnumSource(SortField.class)` deckt jedes Feld ab, auch ein neues.
