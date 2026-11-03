package ch.tbz.mogbattle.web;

import ch.tbz.mogbattle.model.Chad;
import ch.tbz.mogbattle.repository.ChadRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chads")
public class ChadController {

    private final ChadRepository repository;

    public ChadController(ChadRepository repository) {
        this.repository = repository;
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
