package ch.tbz.m450.util;

import ch.tbz.m450.repository.Address;
import ch.tbz.m450.util.AddressComparator.SortDirection;
import ch.tbz.m450.util.AddressComparator.SortField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.List;

import static ch.tbz.m450.AddressTestData.address;
import static ch.tbz.m450.AddressTestData.date;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class AddressComparatorSortFieldTest {

    private Address anna;
    private Address beat;
    private Address carla;
    private List<Address> addresses;

    @BeforeEach
    void setUp() {
        anna = address(3, "Anna", "Züger", "+41 44 300 30 30", date(2025, 6, 1));
        beat = address(1, "Beat", "Meier", "+41 44 100 10 10", date(2023, 2, 14));
        carla = address(2, "Carla", "Ammann", "+41 44 200 20 20", date(2024, 9, 30));
        addresses = new ArrayList<>(List.of(anna, beat, carla));
    }

    @Test
    @DisplayName("Sortiert nach jedem Attribut der Adresse")
    void sortiertNachJedemAttribut() {
        assertThat(sortiertNach(SortField.ID)).containsExactly(beat, carla, anna);
        assertThat(sortiertNach(SortField.FIRSTNAME)).containsExactly(anna, beat, carla);
        assertThat(sortiertNach(SortField.LASTNAME)).containsExactly(carla, beat, anna);
        assertThat(sortiertNach(SortField.PHONENUMBER)).containsExactly(beat, carla, anna);
        assertThat(sortiertNach(SortField.REGISTRATION_DATE)).containsExactly(beat, carla, anna);
    }

    @Test
    @DisplayName("DESC dreht die Reihenfolge um")
    void absteigendDrehtDieReihenfolgeUm() {
        addresses.sort(new AddressComparator(SortDirection.DESC, SortField.FIRSTNAME));

        assertThat(addresses).containsExactly(carla, beat, anna);
    }

    @Test
    @DisplayName("Das zweite Feld entscheidet nur bei Gleichstand im ersten")
    void zweitesFeldEntscheidetBeiGleichstand() {
        Address abtBeat = address(4, "Beat", "Abt", "+41 44 400 40 40", date(2024, 9, 30));
        addresses.add(abtBeat);

        addresses.sort(new AddressComparator(SortField.REGISTRATION_DATE, SortField.LASTNAME));

        assertThat(addresses).containsExactly(beat, abtBeat, carla, anna);
    }

    @ParameterizedTest
    @EnumSource(SortField.class)
    @DisplayName("Jedes Sortierfeld erkennt Gleichheit und ist antisymmetrisch")
    void jedesFeldVerhältSichKorrekt(SortField field) {
        AddressComparator comparator = new AddressComparator(field);

        assertThat(comparator.compare(anna, anna)).isZero();
        assertThat(Integer.signum(comparator.compare(anna, beat)))
                .isEqualTo(-Integer.signum(comparator.compare(beat, anna)));
    }

    @Test
    @DisplayName("Ohne Sortierfeld oder mit null wird der Comparator abgelehnt")
    void ungültigeParameterWerdenAbgelehnt() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new AddressComparator(new SortField[0]));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new AddressComparator(SortField.ID, null));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new AddressComparator((SortDirection) null, SortField.ID));
    }

    private List<Address> sortiertNach(SortField field) {
        List<Address> kopie = new ArrayList<>(addresses);
        kopie.sort(new AddressComparator(field));
        return kopie;
    }
}
