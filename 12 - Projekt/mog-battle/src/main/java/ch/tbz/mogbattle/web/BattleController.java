package ch.tbz.mogbattle.web;

import ch.tbz.mogbattle.model.BattleResult;
import ch.tbz.mogbattle.service.BattleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/battles")
public class BattleController {

    public record BattleRequest(@NotNull Long chadAId, @NotNull Long chadBId) {
    }

    private final BattleService battleService;

    public BattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    @PostMapping
    public BattleResult battle(@Valid @RequestBody BattleRequest request) {
        return battleService.battle(request.chadAId(), request.chadBId());
    }
}
