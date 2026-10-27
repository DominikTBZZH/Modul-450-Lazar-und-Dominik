package ch.tbz.recipe.planner.service;

import ch.tbz.recipe.planner.entities.IngredientEntity;
import ch.tbz.recipe.planner.entities.RecipeEntity;
import ch.tbz.recipe.planner.exception.RecipeNotFoundException;
import ch.tbz.recipe.planner.mapper.IngredientEntityMapper;
import ch.tbz.recipe.planner.mapper.RecipeEntityMapper;
import ch.tbz.recipe.planner.repository.RecipeRepository;
import ch.tbz.recipe.planner.domain.Recipe;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RecipeService {

    RecipeEntityMapper mapper;

    IngredientEntityMapper ingredientMapper;

    RecipeRepository repository;

    public RecipeService(RecipeEntityMapper mapper, IngredientEntityMapper ingredientMapper, RecipeRepository repository) {
        this.mapper = mapper;
        this.ingredientMapper = ingredientMapper;
        this.repository = repository;
    }

    public List<Recipe> getRecipes() {
        List<RecipeEntity> recipesEntities = repository.findAll();
        return recipesEntities.stream().map(mapper::entityToDomain).toList();
    }

    public Recipe getRecipeById(UUID recipeId) {
        return mapper.entityToDomain(findRecipeEntity(recipeId));
    }

    public Recipe addRecipe(Recipe recipe) {
        var createdRecipe = repository.save(mapper.domainToEntity(recipe));
        return mapper.entityToDomain(createdRecipe);
    }

    @Transactional
    public Recipe updateRecipe(UUID recipeId, Recipe recipe) {
        RecipeEntity existingRecipe = findRecipeEntity(recipeId);
        existingRecipe.setName(recipe.getName());
        existingRecipe.setDescription(recipe.getDescription());
        existingRecipe.setImageUrl(recipe.getImageUrl());

        // The ingredient list is replaced as a whole: orphanRemoval deletes the old rows,
        // the new ones are inserted with fresh ids.
        var ingredients = recipe.getIngredients() == null
                ? List.<IngredientEntity>of()
                : ingredientMapper.domainsToEntities(recipe.getIngredients());
        ingredients.forEach(ingredient -> ingredient.setId(null));
        existingRecipe.getIngredients().clear();
        existingRecipe.getIngredients().addAll(ingredients);

        return mapper.entityToDomain(repository.save(existingRecipe));
    }

    private RecipeEntity findRecipeEntity(UUID recipeId) {
        return repository.findById(recipeId).orElseThrow(() -> new RecipeNotFoundException(recipeId));
    }
}
