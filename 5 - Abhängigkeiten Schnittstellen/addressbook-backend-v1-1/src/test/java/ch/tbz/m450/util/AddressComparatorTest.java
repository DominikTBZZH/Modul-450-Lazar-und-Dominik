package ch.tbz.m450.util;

import ch.tbz.m450.repository.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ch.tbz.m450.AddressTestData.address;
import static ch.tbz.m450.AddressTestData.date;
import static ch.tbz.m450.AddressTestData.named;
import static org.assertj.core.api.Assertions.assertThat;

class AddressComparatorTest {

    private AddressComparator comparator;
    private Address meierAnna;
    private Address meierZoe;
    private Address zügerAnna;

    @BeforeEach
    void setUp() {
        comparator = new AddressComparator();
        meierAnna = named(1, "Anna", "Meier");
        meierZoe = named(2, "Zoe", "Meier");
        zügerAnna = named(3, "Anna", "Züger");
    }

    @Test
    @DisplayName("Sortiert nach Nachname, dann Vorname, dann Registrierungsdatum")
    void sortiertNachNachnameVornameDatum() {
        Address meierAnna2023 = address(4, "Anna", "Meier", "+41 44 333 33 33", date(2023, 5, 5));
        List<Address> addresses = new ArrayList<>(
                List.of(zügerAnna, meierZoe, meierAnna, meierAnna2023));

        addresses.sort(comparator);

        assertThat(addresses).containsExactly(meierAnna2023, meierAnna, meierZoe, zügerAnna);
    }

    @Test
    @DisplayName("Inhaltlich gleiche Adressen sind gleich")
    void gleicheAdressenSindGleich() {
        assertThat(comparator.compare(meierAnna, named(99, "Anna", "Meier"))).isZero();
    }

    @Test
    @DisplayName("Der Vergleich ist antisymmetrisch, die Vorgabe mit dem festen -1 fällt hier durch")
    void vergleichIstAntisymmetrisch() {
        int hin = comparator.compare(meierAnna, zügerAnna);
        int zurück = comparator.compare(zügerAnna, meierAnna);

        assertThat(Integer.signum(hin)).isEqualTo(-Integer.signum(zurück));
    }

    @Test
    @DisplayName("Fehlende Werte und null-Adressen landen am Ende, statt zu werfen")
    void fehlendeWerteLandenAmEnde() {
        Address ohneNachname = address(5, "Anna", null, "+41 44 444 44 44", date(2024, 1, 1));
        List<Address> addresses = new ArrayList<>(
                Arrays.asList(null, ohneNachname, zügerAnna, meierAnna));

        addresses.sort(comparator);

        assertThat(addresses).containsExactly(meierAnna, zügerAnna, ohneNachname, null);
    }
}
