package ch.tbz.mogbattle.model;

/**
 * Ergebnis der Hypergamy-Suche: der nächstbessere Chad über dem aktuellen.
 */
public record Upgrade(String current, int currentScore, String upgrade, int upgradeScore) {
}
