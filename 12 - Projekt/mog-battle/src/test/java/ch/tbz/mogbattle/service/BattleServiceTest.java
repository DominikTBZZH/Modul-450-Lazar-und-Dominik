package ch.tbz.mogbattle.service;

import ch.tbz.mogbattle.engine.MogEngine;
import ch.tbz.mogbattle.model.BattleResult;
import ch.tbz.mogbattle.model.Chad;
import ch.tbz.mogbattle.model.Verdict;
import ch.tbz.mogbattle.oracle.AuraOracle;
import ch.tbz.mogbattle.repository.ChadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BattleServiceTest {

    @Mock
    private ChadRepository repository;

    @Mock
    private AuraOracle auraOracle;

    private BattleService service;

    private final Chad max = new Chad("Mewing Max", 6, 7, 5, 4, 6);  // 41
    private final Chad tom = new Chad("Tall Tom", 9, 5, 4, 5, 5);    // 42

    @BeforeEach
    void setUp() {
        service = new BattleService(repository, auraOracle, new MogEngine());
    }

    @Test
    @DisplayName("Ohne Aura-Bonus gewinnt der höhere Score")
    void battleWithoutBonus() {
        when(repository.findById(1L)).thenReturn(Optional.of(max));
        when(repository.findById(2L)).thenReturn(Optional.of(tom));
        when(auraOracle.auraBonus(any())).thenReturn(0);

        BattleResult result = service.battle(1L, 2L);

        assertThat(result.winner()).isEqualTo("Tall Tom");
        assertThat(result.verdict()).isEqualTo(Verdict.CLOSE_CALL);
    }

    @Test
    @DisplayName("Gemockter Aura-Bonus dreht das Battle")
    void auraBonusFromOracleIsApplied() {
        when(repository.findById(1L)).thenReturn(Optional.of(max));
        when(repository.findById(2L)).thenReturn(Optional.of(tom));
        when(auraOracle.auraBonus(max)).thenReturn(3);
        when(auraOracle.auraBonus(tom)).thenReturn(0);

        BattleResult result = service.battle(1L, 2L);

        assertThat(result.winner()).isEqualTo("Mewing Max");
        verify(auraOracle).auraBonus(max);
        verify(auraOracle).auraBonus(tom);
    }

    @Test
    @DisplayName("Unbekannter Chad wirft ChadNotFoundException, Orakel wird nicht befragt")
    void unknownChad() {
        when(repository.findById(1L)).thenReturn(Optional.of(max));
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.battle(1L, 99L))
                .isInstanceOf(ChadNotFoundException.class)
                .hasMessageContaining("99");
        verifyNoInteractions(auraOracle);
    }

    @Test
    @DisplayName("Selbst-Battle ist nicht erlaubt")
    void selfBattle() {
        assertThatThrownBy(() -> service.battle(1L, 1L)).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(repository, auraOracle);
    }
}
