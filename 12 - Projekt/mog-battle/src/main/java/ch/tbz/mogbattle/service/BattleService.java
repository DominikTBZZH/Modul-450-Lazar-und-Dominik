package ch.tbz.mogbattle.service;

import ch.tbz.mogbattle.engine.MogEngine;
import ch.tbz.mogbattle.model.BattleResult;
import ch.tbz.mogbattle.model.Chad;
import ch.tbz.mogbattle.oracle.AuraOracle;
import ch.tbz.mogbattle.repository.ChadRepository;
import org.springframework.stereotype.Service;

@Service
public class BattleService {

    private final ChadRepository repository;
    private final AuraOracle auraOracle;
    private final MogEngine engine;

    public BattleService(ChadRepository repository, AuraOracle auraOracle, MogEngine engine) {
        this.repository = repository;
        this.auraOracle = auraOracle;
        this.engine = engine;
    }

    public BattleResult battle(Long chadAId, Long chadBId) {
        if (chadAId.equals(chadBId)) {
            throw new IllegalArgumentException("Ein Chad kann sich nicht selbst moggen");
        }
        Chad a = load(chadAId);
        Chad b = load(chadBId);
        return engine.battle(a, b, auraOracle.auraBonus(a), auraOracle.auraBonus(b));
    }

    private Chad load(Long id) {
        return repository.findById(id).orElseThrow(() -> new ChadNotFoundException(id));
    }
}
