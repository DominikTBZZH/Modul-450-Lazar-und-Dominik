package ch.tbz.mogbattle.engine;

import ch.tbz.mogbattle.model.BattleResult;
import ch.tbz.mogbattle.model.Chad;
import ch.tbz.mogbattle.model.Verdict;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MogEngineTest {

    private final MogEngine engine = new MogEngine();

    @Test
    @DisplayName("Height und Jawline zählen doppelt")
    void mogScoreWeightsHeightAndJawlineDouble() {
        Chad chad = new Chad("Andy", 5, 5, 5, 5, 5);

        assertThat(engine.mogScore(chad)).isEqualTo(35);
    }

    @Test
    @DisplayName("Minimaler Chad hat Score 7, maximaler 70")
    void mogScoreBounds() {
        assertThat(engine.mogScore(new Chad("Min", 1, 1, 1, 1, 1))).isEqualTo(7);
        assertThat(engine.mogScore(new Chad("Max", 10, 10, 10, 10, 10))).isEqualTo(70);
    }

    @ParameterizedTest(name = "Abstand {0} ergibt {1}")
    @CsvSource({
            "0, TIE",
            "1, CLOSE_CALL",
            "4, CLOSE_CALL",
            "5, MOGGED",
            "14, MOGGED",
            "15, MEGAMOGGED",
            "63, MEGAMOGGED"
    })
    @DisplayName("Verdict-Schwellen inkl. Grenzwerte")
    void verdictThresholds(int margin, Verdict expected) {
        assertThat(engine.verdict(margin)).isEqualTo(expected);
    }

    @Test
    @DisplayName("Negativer Abstand ist ungültig")
    void verdictRejectsNegativeMargin() {
        assertThatThrownBy(() -> engine.verdict(-1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Der Chad mit höherem Score mogt den anderen")
    void battleHigherScoreWins() {
        Chad giga = new Chad("Gigachad", 10, 10, 9, 8, 9);
        Chad andy = new Chad("Average Andy", 5, 5, 5, 5, 5);

        BattleResult result = engine.battle(giga, andy, 0, 0);

        assertThat(result.winner()).isEqualTo("Gigachad");
        assertThat(result.loser()).isEqualTo("Average Andy");
        assertThat(result.margin()).isEqualTo(31);
        assertThat(result.verdict()).isEqualTo(Verdict.MEGAMOGGED);
    }

    @Test
    @DisplayName("Reihenfolge der Chads spielt keine Rolle")
    void battleIsSymmetric() {
        Chad giga = new Chad("Gigachad", 10, 10, 9, 8, 9);
        Chad andy = new Chad("Average Andy", 5, 5, 5, 5, 5);

        BattleResult result = engine.battle(andy, giga, 0, 0);

        assertThat(result.winner()).isEqualTo("Gigachad");
    }

    @Test
    @DisplayName("Aura-Bonus kann ein knappes Battle drehen")
    void auraBonusCanFlipBattle() {
        Chad max = new Chad("Mewing Max", 6, 8, 5, 4, 6);   // 41
        Chad tom = new Chad("Tall Tom", 9, 5, 4, 5, 5);     // 42

        BattleResult result = engine.battle(max, tom, 3, 0);

        assertThat(result.winner()).isEqualTo("Mewing Max");
        assertThat(result.margin()).isEqualTo(2);
        assertThat(result.verdict()).isEqualTo(Verdict.CLOSE_CALL);
    }

    @Test
    @DisplayName("Gleicher Score ergibt TIE ohne Gewinner")
    void battleTie() {
        Chad a = new Chad("A", 5, 5, 5, 5, 5);
        Chad b = new Chad("B", 5, 5, 5, 5, 5);

        BattleResult result = engine.battle(a, b, 1, 1);

        assertThat(result.winner()).isNull();
        assertThat(result.loser()).isNull();
        assertThat(result.verdict()).isEqualTo(Verdict.TIE);
    }
}
