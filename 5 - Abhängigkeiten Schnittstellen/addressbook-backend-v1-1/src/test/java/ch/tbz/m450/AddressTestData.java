package ch.tbz.m450;

import ch.tbz.m450.repository.Address;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public final class AddressTestData {

    private AddressTestData() {
    }

    public static Address address(int id, String firstname, String lastname, String phonenumber, Date registrationDate) {
        return new Address(id, firstname, lastname, phonenumber, registrationDate);
    }

    public static Address named(int id, String firstname, String lastname) {
        return address(id, firstname, lastname, "+41 44 000 00 00", date(2024, 1, 1));
    }

    public static Date date(int year, int month, int day) {
        return Date.from(LocalDate.of(year, month, day).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
