package ch.tbz.mogbattle.model;

/**
 * Ergebnis eines Battles. Bei einem TIE ist winner und loser null.
 */
public record BattleResult(String winner, String loser, int margin, Verdict verdict) {
}
