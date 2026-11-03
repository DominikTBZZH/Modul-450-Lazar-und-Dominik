package ch.tbz.mogbattle.engine;

import ch.tbz.mogbattle.model.BattleResult;
import ch.tbz.mogbattle.model.Chad;
import ch.tbz.mogbattle.model.Verdict;

public class MogEngine {

    static final int CLOSE_CALL_LIMIT = 5;
    static final int MEGAMOG_LIMIT = 15;

    public int mogScore(Chad chad) {
        return chad.getHeight() * 2
                + chad.getJawline() * 2
                + chad.getAura()
                + chad.getStatus()
                + chad.getFrame();
    }

    public Verdict verdict(int margin) {
        if (margin < 0) {
            throw new IllegalArgumentException("Abstand darf nicht negativ sein");
        }
        if (margin == 0) {
            return Verdict.TIE;
        }
        if (margin < CLOSE_CALL_LIMIT) {
            return Verdict.CLOSE_CALL;
        }
        return margin < MEGAMOG_LIMIT ? Verdict.MOGGED : Verdict.MEGAMOGGED;
    }

    public BattleResult battle(Chad a, Chad b, int auraBonusA, int auraBonusB) {
        int diff = (mogScore(a) + auraBonusA) - (mogScore(b) + auraBonusB);
        if (diff == 0) {
            return new BattleResult(null, null, 0, Verdict.TIE);
        }
        Chad winner = diff > 0 ? a : b;
        Chad loser = diff > 0 ? b : a;
        int margin = Math.abs(diff);
        return new BattleResult(winner.getName(), loser.getName(), margin, verdict(margin));
    }
}
