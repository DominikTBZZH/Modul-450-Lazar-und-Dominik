package ch.tbz.mogbattle.service;

import ch.tbz.mogbattle.engine.MogEngine;
import ch.tbz.mogbattle.model.Chad;
import ch.tbz.mogbattle.model.Upgrade;
import ch.tbz.mogbattle.repository.ChadRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Optional;

@Service
public class HypergamyService {

    private final ChadRepository repository;
    private final MogEngine engine;

    public HypergamyService(ChadRepository repository, MogEngine engine) {
        this.repository = repository;
        this.engine = engine;
    }

    public Upgrade findUpgrade(Long chadId) {
        Chad current = load(chadId);
        Chad next = nextHigherThan(current).orElseThrow(() -> new NoUpgradeException(current.getName()));
        return toUpgrade(current, next);
    }

    private Chad load(Long id) {
        return repository.findById(id).orElseThrow(() -> new ChadNotFoundException(id));
    }

    /** Sucht unter allen Chads mit höherem Score denjenigen mit dem kleinsten Score. */
    private Optional<Chad> nextHigherThan(Chad current) {
        int currentScore = engine.mogScore(current);
        return repository.findAll().stream()
                .filter(other -> engine.mogScore(other) > currentScore)
                .min(Comparator.comparingInt(engine::mogScore));
    }

    private Upgrade toUpgrade(Chad current, Chad next) {
        return new Upgrade(current.getName(), engine.mogScore(current), next.getName(), engine.mogScore(next));
    }
}
