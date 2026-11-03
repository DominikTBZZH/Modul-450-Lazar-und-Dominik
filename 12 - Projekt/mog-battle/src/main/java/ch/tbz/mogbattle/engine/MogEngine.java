package ch.tbz.mogbattle.engine;

import ch.tbz.mogbattle.model.BattleResult;
import ch.tbz.mogbattle.model.Chad;
import ch.tbz.mogbattle.model.Verdict;

public class MogEngine {

    public int mogScore(Chad chad) {
        return chad.getHeight() * 2 + chad.getJawline() * 2 + chad.getAura() + chad.getStatus() + chad.getFrame();
    }

    public Verdict verdict(int margin) {
        if (margin < 0) {
            throw new IllegalArgumentException("Abstand darf nicht negativ sein");
        }
        if (margin == 0) {
            return Verdict.TIE;
        }
        if (margin < 5) {
            return Verdict.CLOSE_CALL;
        }
        if (margin < 15) {
            return Verdict.MOGGED;
        }
        return Verdict.MEGAMOGGED;
    }

    public BattleResult battle(Chad a, Chad b, int auraBonusA, int auraBonusB) {
        int scoreA = mogScore(a) + auraBonusA;
        int scoreB = mogScore(b) + auraBonusB;
        if (scoreA == scoreB) {
            return new BattleResult(null, null, 0, Verdict.TIE);
        }
        if (scoreA > scoreB) {
            return new BattleResult(a.getName(), b.getName(), scoreA - scoreB, verdict(scoreA - scoreB));
        }
        return new BattleResult(b.getName(), a.getName(), scoreB - scoreA, verdict(scoreB - scoreA));
    }
}
