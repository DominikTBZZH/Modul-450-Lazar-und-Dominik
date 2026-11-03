package ch.tbz.mogbattle.oracle;

import ch.tbz.mogbattle.model.Chad;

/**
 * Schnittstelle zum "Aura-Orakel": liefert pro Battle einen Tagesform-Bonus von 0 bis 3.
 * In den Tests wird sie gemockt, damit die Resultate deterministisch sind.
 */
public interface AuraOracle {

    int MAX_BONUS = 3;

    int auraBonus(Chad chad);
}
