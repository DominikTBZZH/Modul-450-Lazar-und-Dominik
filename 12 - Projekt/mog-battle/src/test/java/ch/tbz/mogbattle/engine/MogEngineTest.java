package ch.tbz.mogbattle.engine;

import ch.tbz.mogbattle.model.Chad;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
}
