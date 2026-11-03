package ch.tbz.mogbattle.web;

import ch.tbz.mogbattle.model.Chad;
import ch.tbz.mogbattle.model.Upgrade;
import ch.tbz.mogbattle.repository.ChadRepository;
import ch.tbz.mogbattle.service.HypergamyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chads")
public class ChadController {

    private final ChadRepository repository;
    private final HypergamyService hypergamyService;

    public ChadController(ChadRepository repository, HypergamyService hypergamyService) {
        this.repository = repository;
        this.hypergamyService = hypergamyService;
    }

    @GetMapping("/{id}/upgrade")
    public Upgrade upgrade(@PathVariable Long id) {
        return hypergamyService.findUpgrade(id);
    }

    @GetMapping
    public List<Chad> all() {
        return repository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Chad create(@Valid @RequestBody Chad chad) {
        chad.setId(null);
        return repository.save(chad);
    }
}
