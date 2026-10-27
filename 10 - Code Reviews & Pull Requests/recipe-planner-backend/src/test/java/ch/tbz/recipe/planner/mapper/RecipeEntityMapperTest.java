package ch.tbz.recipe.planner.mapper;

import ch.tbz.recipe.planner.domain.Ingredient;
import ch.tbz.recipe.planner.domain.Recipe;
import ch.tbz.recipe.planner.domain.Unit;
import ch.tbz.recipe.planner.entities.IngredientEntity;
import ch.tbz.recipe.planner.entities.RecipeEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class RecipeEntityMapperTest {

    private final RecipeEntityMapper mapper = Mappers.getMapper(RecipeEntityMapper.class);

    @Test
    @DisplayName("Entity inkl. Zutaten wird in Domain gemappt")
    void entityToDomain_mapsAllFieldsAndIngredients() {
        IngredientEntity tomato = new IngredientEntity(UUID.randomUUID(), "Tomato", "The big ones", Unit.PIECE, 5);
        RecipeEntity entity = new RecipeEntity(UUID.randomUUID(), "Lasagne al Forno", "Im Ofen",
                "https://img/lasagne.jpg", List.of(tomato));

        Recipe recipe = mapper.entityToDomain(entity);

        assertSoftly(softly -> {
            softly.assertThat(recipe.getId()).isEqualTo(entity.getId());
            softly.assertThat(recipe.getName()).isEqualTo("Lasagne al Forno");
            softly.assertThat(recipe.getDescription()).isEqualTo("Im Ofen");
            softly.assertThat(recipe.getImageUrl()).isEqualTo("https://img/lasagne.jpg");
            softly.assertThat(recipe.getIngredients()).hasSize(1);
            Ingredient ingredient = recipe.getIngredients().get(0);
            softly.assertThat(ingredient.getId()).isEqualTo(tomato.getId());
            softly.assertThat(ingredient.getName()).isEqualTo("Tomato");
            softly.assertThat(ingredient.getComment()).isEqualTo("The big ones");
            softly.assertThat(ingredient.getUnit()).isEqualTo(Unit.PIECE);
            softly.assertThat(ingredient.getAmount()).isEqualTo(5);
        });
    }

    @Test
    @DisplayName("Domain inkl. Zutaten wird in Entity gemappt")
    void domainToEntity_mapsAllFieldsAndIngredients() {
        Ingredient rice = new Ingredient(UUID.randomUUID(), "Reis", "Basmati", Unit.KILOGRAMM, 1);
        Recipe recipe = new Recipe(UUID.randomUUID(), "Fried Rice", "Gebraten", "https://img/rice.jpg", List.of(rice));

        RecipeEntity entity = mapper.domainToEntity(recipe);

        assertSoftly(softly -> {
            softly.assertThat(entity.getId()).isEqualTo(recipe.getId());
            softly.assertThat(entity.getName()).isEqualTo("Fried Rice");
            softly.assertThat(entity.getDescription()).isEqualTo("Gebraten");
            softly.assertThat(entity.getImageUrl()).isEqualTo("https://img/rice.jpg");
            softly.assertThat(entity.getIngredients()).hasSize(1);
            IngredientEntity ingredient = entity.getIngredients().get(0);
            softly.assertThat(ingredient.getId()).isEqualTo(rice.getId());
            softly.assertThat(ingredient.getName()).isEqualTo("Reis");
            softly.assertThat(ingredient.getComment()).isEqualTo("Basmati");
            softly.assertThat(ingredient.getUnit()).isEqualTo(Unit.KILOGRAMM);
            softly.assertThat(ingredient.getAmount()).isEqualTo(1);
        });
    }

    @Test
    @DisplayName("Rezept ohne Zutaten und null werden korrekt behandelt")
    void nullValues_areHandled() {
        Recipe withoutIngredients = new Recipe(UUID.randomUUID(), "Wasser", null, null, null);

        RecipeEntity entity = mapper.domainToEntity(withoutIngredients);

        assertSoftly(softly -> {
            softly.assertThat(entity.getName()).isEqualTo("Wasser");
            softly.assertThat(entity.getDescription()).isNull();
            softly.assertThat(entity.getImageUrl()).isNull();
            softly.assertThat(entity.getIngredients()).isNull();
            softly.assertThat(mapper.entityToDomain(null)).isNull();
            softly.assertThat(mapper.domainToEntity(null)).isNull();
        });
    }
}
