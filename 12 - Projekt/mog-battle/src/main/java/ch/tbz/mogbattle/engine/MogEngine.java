package ch.tbz.mogbattle.engine;

import ch.tbz.mogbattle.model.BattleResult;
import ch.tbz.mogbattle.model.Chad;
import ch.tbz.mogbattle.model.Verdict;

public class MogEngine {

    public int mogScore(Chad chad) {
        return chad.getHeight() * 2 + chad.getJawline() * 2 + chad.getAura() + chad.getStatus() + chad.getFrame();
    }

    public Verdict verdict(int margin) {
        throw new UnsupportedOperationException("noch nicht implementiert");
    }

    public BattleResult battle(Chad a, Chad b, int auraBonusA, int auraBonusB) {
        throw new UnsupportedOperationException("noch nicht implementiert");
    }
}
