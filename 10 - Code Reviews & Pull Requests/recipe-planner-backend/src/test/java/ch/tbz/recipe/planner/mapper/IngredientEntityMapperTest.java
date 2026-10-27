package ch.tbz.recipe.planner.mapper;

import ch.tbz.recipe.planner.domain.Ingredient;
import ch.tbz.recipe.planner.domain.Unit;
import ch.tbz.recipe.planner.entities.IngredientEntity;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class IngredientEntityMapperTest {

    private final IngredientEntityMapper mapper = Mappers.getMapper(IngredientEntityMapper.class);

    @Test
    @DisplayName("Entity wird vollständig in Domain gemappt")
    void entityToDomain_mapsAllFields() {
        IngredientEntity entity = new IngredientEntity(UUID.randomUUID(), "Tomato", "The big ones", Unit.PIECE, 5);

        Ingredient ingredient = mapper.entityToDomain(entity);

        assertSoftly(softly -> {
            softly.assertThat(ingredient.getId()).isEqualTo(entity.getId());
            softly.assertThat(ingredient.getName()).isEqualTo("Tomato");
            softly.assertThat(ingredient.getComment()).isEqualTo("The big ones");
            softly.assertThat(ingredient.getUnit()).isEqualTo(Unit.PIECE);
            softly.assertThat(ingredient.getAmount()).isEqualTo(5);
        });
    }

    @Test
    @DisplayName("Domain wird vollständig in Entity gemappt")
    void domainToEntity_mapsAllFields() {
        Ingredient ingredient = new Ingredient(UUID.randomUUID(), "Mehl", null, Unit.GRAMM, 500);

        IngredientEntity entity = mapper.domainToEntity(ingredient);

        assertSoftly(softly -> {
            softly.assertThat(entity.getId()).isEqualTo(ingredient.getId());
            softly.assertThat(entity.getName()).isEqualTo("Mehl");
            softly.assertThat(entity.getComment()).isNull();
            softly.assertThat(entity.getUnit()).isEqualTo(Unit.GRAMM);
            softly.assertThat(entity.getAmount()).isEqualTo(500);
        });
    }

    @Test
    @DisplayName("Listen werden in beide Richtungen gemappt")
    void listMapping_bothDirections() {
        List<IngredientEntity> entities = List.of(
                new IngredientEntity(UUID.randomUUID(), "Milch", "fettarm", Unit.DECILITRE, 3),
                new IngredientEntity(UUID.randomUUID(), "Wasser", null, Unit.LITRE, 1));

        List<Ingredient> domains = mapper.entitiesToDomains(entities);
        List<IngredientEntity> back = mapper.domainsToEntities(domains);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(domains).hasSize(2);
        softly.assertThat(domains).extracting(Ingredient::getName).containsExactly("Milch", "Wasser");
        softly.assertThat(domains).extracting(Ingredient::getUnit).containsExactly(Unit.DECILITRE, Unit.LITRE);
        softly.assertThat(back).isEqualTo(entities);
        softly.assertAll();
    }

    @Test
    @DisplayName("null bleibt null")
    void nullInput_returnsNull() {
        assertSoftly(softly -> {
            softly.assertThat(mapper.entityToDomain(null)).isNull();
            softly.assertThat(mapper.domainToEntity(null)).isNull();
            softly.assertThat(mapper.entitiesToDomains(null)).isNull();
            softly.assertThat(mapper.domainsToEntities(null)).isNull();
        });
    }
}
