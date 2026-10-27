package ch.tbz.recipe.planner.controller;

import ch.tbz.recipe.planner.domain.Ingredient;
import ch.tbz.recipe.planner.domain.Recipe;
import ch.tbz.recipe.planner.domain.Unit;
import ch.tbz.recipe.planner.mapper.RecipeEntityMapper;
import ch.tbz.recipe.planner.repository.RecipeRepository;
import ch.tbz.recipe.planner.service.RecipeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecipeController.class)
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RecipeService service;

    @MockBean
    private RecipeEntityMapper mapper;

    @MockBean
    private RecipeRepository repository;

    private Recipe lasagne;
    private Recipe pommes;

    @BeforeEach
    void setUp() {
        Ingredient tomato = new Ingredient(UUID.randomUUID(), "Tomato", "The big ones", Unit.PIECE, 5);
        lasagne = new Recipe(UUID.randomUUID(), "Lasagne al Forno", "Im Ofen", "https://img/lasagne.jpg", List.of(tomato));
        pommes = new Recipe(UUID.randomUUID(), "Pommes Frites", "Frittiert", "https://img/pommes.jpg", List.of());
    }

    @Test
    @DisplayName("GET /api/recipes liefert alle Rezepte")
    void getRecipes_returnsAllRecipes() throws Exception {
        when(service.getRecipes()).thenReturn(List.of(lasagne, pommes));

        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Lasagne al Forno"))
                .andExpect(jsonPath("$[0].ingredients[0].unit").value("PIECE"))
                .andExpect(jsonPath("$[1].name").value("Pommes Frites"));

        verify(service).getRecipes();
    }

    @Test
    @DisplayName("GET /api/recipes ohne Rezepte liefert leere Liste")
    void getRecipes_empty_returnsEmptyList() throws Exception {
        when(service.getRecipes()).thenReturn(List.of());

        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/recipes/recipe/{id} liefert das Rezept")
    void getRecipe_existingId_returnsRecipe() throws Exception {
        when(service.getRecipeById(lasagne.getId())).thenReturn(lasagne);

        mockMvc.perform(get("/api/recipes/recipe/{id}", lasagne.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(lasagne.getId().toString()))
                .andExpect(jsonPath("$.name").value("Lasagne al Forno"))
                .andExpect(jsonPath("$.description").value("Im Ofen"))
                .andExpect(jsonPath("$.imageUrl").value("https://img/lasagne.jpg"))
                .andExpect(jsonPath("$.ingredients", hasSize(1)))
                .andExpect(jsonPath("$.ingredients[0].amount").value(5));

        verify(service).getRecipeById(lasagne.getId());
    }

    @Test
    @DisplayName("GET /api/recipes/recipe/{id} mit unbekannter ID liefert 200 ohne Body")
    void getRecipe_unknownId_returnsEmptyBody() throws Exception {
        UUID unknown = UUID.randomUUID();
        when(service.getRecipeById(unknown)).thenReturn(null);

        mockMvc.perform(get("/api/recipes/recipe/{id}", unknown))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("GET /api/recipes/recipe/{id} mit ungültiger UUID liefert 400")
    void getRecipe_invalidUuid_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/recipes/recipe/{id}", "keine-uuid"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("POST /api/recipes speichert das Rezept und gibt es zurück")
    void addRecipe_returnsSavedRecipe() throws Exception {
        when(service.addRecipe(any(Recipe.class))).thenReturn(lasagne);

        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lasagne)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(lasagne.getId().toString()))
                .andExpect(jsonPath("$.name").value("Lasagne al Forno"));

        ArgumentCaptor<Recipe> captor = ArgumentCaptor.forClass(Recipe.class);
        verify(service).addRecipe(captor.capture());
        assertThat(captor.getValue()).isEqualTo(lasagne);
    }

    @Test
    @DisplayName("POST /api/recipes ohne Body liefert 400")
    void addRecipe_withoutBody_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/recipes").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }
}
