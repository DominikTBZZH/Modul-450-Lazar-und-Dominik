package ch.tbz.m450.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static ch.tbz.m450.AddressTestData.date;
import static org.assertj.core.api.Assertions.assertThat;

class AddressTest {

    private Address address;
    private Date registrationDate;

    @BeforeEach
    void setUp() {
        registrationDate = date(2024, 3, 15);
        address = new Address();
    }

    @Test
    @DisplayName("Der leere Konstruktor liefert Defaultwerte, die Setter schreiben alle Attribute")
    void leererKonstruktorUndSetter() {
        assertThat(address.getId()).isZero();
        assertThat(address.getLastname()).isNull();

        address.setId(42);
        address.setFirstname("Anna");
        address.setLastname("Meier");
        address.setPhonenumber("+41 44 111 22 33");
        address.setRegistrationDate(registrationDate);

        assertThat(address.getId()).isEqualTo(42);
        assertThat(address.getFirstname()).isEqualTo("Anna");
        assertThat(address.getLastname()).isEqualTo("Meier");
        assertThat(address.getPhonenumber()).isEqualTo("+41 44 111 22 33");
        assertThat(address.getRegistrationDate()).isEqualTo(registrationDate);
    }

    @Test
    @DisplayName("Der Konstruktor mit allen Argumenten übernimmt die Werte in der richtigen Reihenfolge")
    void vollständigerKonstruktorÜbernimmtWerte() {
        Address created = new Address(7, "Beat", "Züger", "+41 79 999 88 77", registrationDate);

        assertThat(created.getId()).isEqualTo(7);
        assertThat(created.getFirstname()).isEqualTo("Beat");
        assertThat(created.getLastname()).isEqualTo("Züger");
        assertThat(created.getPhonenumber()).isEqualTo("+41 79 999 88 77");
        assertThat(created.getRegistrationDate()).isEqualTo(registrationDate);
    }
}
