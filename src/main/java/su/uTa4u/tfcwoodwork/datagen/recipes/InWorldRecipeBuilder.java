package su.uTa4u.tfcwoodwork.datagen.recipes;

import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.uTa4u.tfcwoodwork.recipes.inworld.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// TODO: add group string
public final class InWorldRecipeBuilder implements RecipeBuilder {
    @NotNull
    private final BlockStatePredicate inputState;
    @NotNull
    private final Tool tool;
    @NotNull
    private final BlockState resultState;
    @NotNull
    private final Optional<SoundInstance> sound;
    @NotNull
    private final List<Pair<ItemStack, Action>> resultItems;

    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public InWorldRecipeBuilder(
            @NotNull BlockStatePredicate inputState,
            @NotNull Tool tool,
            @NotNull BlockState resultState,
            @NotNull Optional<SoundInstance> sound,
            @NotNull List<Pair<ItemStack, Action>> resultItems
    ) {
        this.inputState = inputState;
        this.tool = tool;
        this.resultState = resultState;
        this.sound = sound;
        this.resultItems = resultItems;
    }

    @Override
    @NotNull
    public RecipeBuilder unlockedBy(@NotNull String name, @NotNull Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    @NotNull
    public RecipeBuilder group(@Nullable String group) {
        return this;
    }

    @Override
    @NotNull
    public Item getResult() {
        return !this.resultItems.isEmpty() ? this.resultItems.getFirst().getFirst().getItem() : Items.AIR;
    }

    @Override
    public void save(@NotNull RecipeOutput output, @NotNull ResourceLocation id) {
        Advancement.Builder advancement = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement::addCriterion);
        InWorldRecipe recipe = new InWorldRecipe(this.inputState, this.tool, this.resultState, this.sound, this.resultItems);
        output.accept(id, recipe, null);
    }
}
