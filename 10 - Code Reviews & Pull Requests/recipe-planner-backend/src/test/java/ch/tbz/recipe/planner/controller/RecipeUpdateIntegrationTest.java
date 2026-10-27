package ch.tbz.recipe.planner.controller;

import ch.tbz.recipe.planner.domain.Ingredient;
import ch.tbz.recipe.planner.domain.Recipe;
import ch.tbz.recipe.planner.domain.Unit;
import ch.tbz.recipe.planner.service.RecipeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RecipeUpdateIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RecipeService service;

    @Test
    void updateRecipe_updatesFieldsAndReplacesIngredients() throws Exception {
        Recipe existing = service.addRecipe(new Recipe(null, "Pancakes", "Old description", "https://example.com/old.jpg",
                List.of(new Ingredient(null, "Flour", "", Unit.GRAMM, 200))));
        UUID oldIngredientId = existing.getIngredients().get(0).getId();

        Recipe changes = new Recipe(null, "Blueberry Pancakes", "New description", "https://example.com/new.jpg",
                List.of(new Ingredient(oldIngredientId, "Flour", "", Unit.GRAMM, 250),
                        new Ingredient(null, "Blueberries", "fresh", Unit.GRAMM, 100)));

        mockMvc.perform(put("/api/recipes/recipe/{id}", existing.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changes)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existing.getId().toString()))
                .andExpect(jsonPath("$.name").value("Blueberry Pancakes"))
                .andExpect(jsonPath("$.ingredients.length()").value(2));

        Recipe stored = service.getRecipeById(existing.getId());
        assertThat(stored.getName()).isEqualTo("Blueberry Pancakes");
        assertThat(stored.getDescription()).isEqualTo("New description");
        assertThat(stored.getImageUrl()).isEqualTo("https://example.com/new.jpg");
        assertThat(stored.getIngredients())
                .extracting(Ingredient::getName, Ingredient::getAmount)
                .containsExactlyInAnyOrder(
                        tuple("Flour", 250),
                        tuple("Blueberries", 100));
    }

    @Test
    void updateRecipe_removesAllIngredients_whenListIsEmpty() throws Exception {
        Recipe existing = service.addRecipe(new Recipe(null, "Toast", "", "",
                List.of(new Ingredient(null, "Bread", "", Unit.PIECE, 2))));

        Recipe changes = new Recipe(null, "Toast", "", "", List.of());

        mockMvc.perform(put("/api/recipes/recipe/{id}", existing.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changes)))
                .andExpect(status().isOk());

        assertThat(service.getRecipeById(existing.getId()).getIngredients()).isEmpty();
    }

    @Test
    void updateRecipe_returnsNotFound_forUnknownId() throws Exception {
        Recipe changes = new Recipe(null, "Ghost", "", "", List.of());

        mockMvc.perform(put("/api/recipes/recipe/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changes)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRecipe_returnsNotFound_forUnknownId() throws Exception {
        mockMvc.perform(get("/api/recipes/recipe/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
