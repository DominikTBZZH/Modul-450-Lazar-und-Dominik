package ch.tbz.mogbattle.engine;

import ch.tbz.mogbattle.model.Chad;

public class MogEngine {

    public int mogScore(Chad chad) {
        return chad.getHeight() * 2 + chad.getJawline() * 2 + chad.getAura() + chad.getStatus() + chad.getFrame();
    }
}
