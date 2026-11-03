package ch.tbz.mogbattle.service;

import ch.tbz.mogbattle.engine.MogEngine;
import ch.tbz.mogbattle.model.Upgrade;
import ch.tbz.mogbattle.repository.ChadRepository;
import org.springframework.stereotype.Service;

@Service
public class HypergamyService {

    private final ChadRepository repository;
    private final MogEngine engine;

    public HypergamyService(ChadRepository repository, MogEngine engine) {
        this.repository = repository;
        this.engine = engine;
    }

    public Upgrade findUpgrade(Long chadId) {
        throw new UnsupportedOperationException("noch nicht implementiert");
    }
}
