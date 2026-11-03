package ch.tbz.mogbattle.service;

import ch.tbz.mogbattle.engine.MogEngine;
import ch.tbz.mogbattle.model.Chad;
import ch.tbz.mogbattle.model.Upgrade;
import ch.tbz.mogbattle.repository.ChadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HypergamyServiceTest {

    @Mock
    private ChadRepository repository;

    private HypergamyService service;

    private final Chad andy = chad(1L, "Average Andy", 5, 5, 5, 5, 5);     // 35
    private final Chad max = chad(2L, "Mewing Max", 6, 7, 5, 4, 6);        // 41
    private final Chad tom = chad(3L, "Tall Tom", 9, 5, 4, 5, 5);          // 42
    private final Chad giga = chad(4L, "Gigachad", 10, 10, 9, 8, 9);       // 66

    @BeforeEach
    void setUp() {
        service = new HypergamyService(repository, new MogEngine());
    }

    @Test
    @DisplayName("Upgrade ist der nächsthöhere Score, nicht der höchste")
    void findsNextHigherChad() {
        when(repository.findById(1L)).thenReturn(Optional.of(andy));
        when(repository.findAll()).thenReturn(List.of(giga, tom, andy, max));

        Upgrade upgrade = service.findUpgrade(1L);

        assertThat(upgrade.current()).isEqualTo("Average Andy");
        assertThat(upgrade.currentScore()).isEqualTo(35);
        assertThat(upgrade.upgrade()).isEqualTo("Mewing Max");
        assertThat(upgrade.upgradeScore()).isEqualTo(41);
    }

    @Test
    @DisplayName("Chads mit gleichem Score zählen nicht als Upgrade")
    void sameScoreIsNoUpgrade() {
        Chad twin = chad(5L, "Andy Twin", 5, 5, 5, 5, 5);
        when(repository.findById(1L)).thenReturn(Optional.of(andy));
        when(repository.findAll()).thenReturn(List.of(andy, twin, tom));

        assertThat(service.findUpgrade(1L).upgrade()).isEqualTo("Tall Tom");
    }

    @Test
    @DisplayName("Der Top-Chad hat kein Upgrade")
    void topChadHasNoUpgrade() {
        when(repository.findById(4L)).thenReturn(Optional.of(giga));
        when(repository.findAll()).thenReturn(List.of(giga, tom, andy));

        assertThatThrownBy(() -> service.findUpgrade(4L))
                .isInstanceOf(NoUpgradeException.class)
                .hasMessageContaining("Gigachad");
    }

    @Test
    @DisplayName("Unbekannter Chad wirft ChadNotFoundException")
    void unknownChad() {
        when(repository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findUpgrade(42L)).isInstanceOf(ChadNotFoundException.class);
    }

    private static Chad chad(Long id, String name, int h, int j, int a, int s, int f) {
        Chad chad = new Chad(name, h, j, a, s, f);
        chad.setId(id);
        return chad;
    }
}
