package ch.tbz.mogbattle.service;

import ch.tbz.mogbattle.engine.MogEngine;
import ch.tbz.mogbattle.model.Chad;
import ch.tbz.mogbattle.model.Upgrade;
import ch.tbz.mogbattle.repository.ChadRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
public class HypergamyService {

    private final ChadRepository repository;
    private final MogEngine engine;

    public HypergamyService(ChadRepository repository, MogEngine engine) {
        this.repository = repository;
        this.engine = engine;
    }

    public Upgrade findUpgrade(Long chadId) {
        Chad current = repository.findById(chadId).orElseThrow(() -> new ChadNotFoundException(chadId));
        int currentScore = engine.mogScore(current);

        return repository.findAll().stream()
                .filter(other -> engine.mogScore(other) > currentScore)
                .min(Comparator.comparingInt(engine::mogScore))
                .map(next -> new Upgrade(current.getName(), currentScore, next.getName(), engine.mogScore(next)))
                .orElseThrow(() -> new NoUpgradeException(current.getName()));
    }
}
