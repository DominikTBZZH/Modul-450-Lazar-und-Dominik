package ch.tbz.m450.service;

import ch.tbz.m450.repository.Address;
import ch.tbz.m450.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static ch.tbz.m450.AddressTestData.named;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

    @Captor
    private ArgumentCaptor<Address> addressCaptor;

    private Address meierAnna;
    private Address zügerBeat;
    private Address ammannCarla;

    @BeforeEach
    void setUp() {
        meierAnna = named(1, "Anna", "Meier");
        zügerBeat = named(2, "Beat", "Züger");
        ammannCarla = named(3, "Carla", "Ammann");
    }

    @Test
    @DisplayName("save() gibt die Adresse unverändert an das Repository weiter")
    void saveReichtAnRepositoryWeiter() {
        when(addressRepository.save(meierAnna)).thenReturn(meierAnna);

        Address saved = addressService.save(meierAnna);

        assertThat(saved).isSameAs(meierAnna);
        verify(addressRepository).save(addressCaptor.capture());
        assertThat(addressCaptor.getValue().getLastname()).isEqualTo("Meier");
    }

    @Test
    @DisplayName("getAll() sortiert die Adressen aus dem Repository")
    void getAllSortiertDieAdressen() {
        when(addressRepository.findAll()).thenReturn(List.of(zügerBeat, meierAnna, ammannCarla));

        List<Address> result = addressService.getAll();

        assertThat(result).extracting(Address::getLastname)
                .containsExactly("Ammann", "Meier", "Züger");
        verify(addressRepository).findAll();
    }

    @Test
    @DisplayName("getAddress() gibt die gefundene Adresse zurück")
    void getAddressLiefertGefundeneAdresse() {
        when(addressRepository.findById(1)).thenReturn(Optional.of(meierAnna));

        assertThat(addressService.getAddress(1)).containsSame(meierAnna);
        verify(addressRepository).findById(1);
    }

    @Test
    @DisplayName("getAddress() gibt ein leeres Optional zurück, wenn es die Id nicht gibt")
    void getAddressLiefertLeeresOptional() {
        when(addressRepository.findById(404)).thenReturn(Optional.empty());

        assertThat(addressService.getAddress(404)).isEmpty();
    }
}
