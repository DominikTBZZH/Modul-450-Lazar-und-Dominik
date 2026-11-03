package ch.tbz.mogbattle.oracle;

import ch.tbz.mogbattle.model.Chad;
import org.springframework.stereotype.Component;

import java.util.random.RandomGenerator;

@Component
public class RandomAuraOracle implements AuraOracle {

    private final RandomGenerator random = RandomGenerator.getDefault();

    @Override
    public int auraBonus(Chad chad) {
        return random.nextInt(MAX_BONUS + 1);
    }
}
