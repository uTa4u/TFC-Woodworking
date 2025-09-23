package su.uTa4u.tfcwoodwork.recipes;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;
import su.uTa4u.tfcwoodwork.TFCWoodworking;
import su.uTa4u.tfcwoodwork.recipes.inworld.InWorldRecipe;

import java.util.function.Supplier;

public final class ModRecipeTypes {
    private ModRecipeTypes() {
    }

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES;

    // TODO: add offhand item as an input Ingredient
    public static final Supplier<RecipeType<InWorldRecipe>> IN_WORLD;

    static {
        RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, TFCWoodworking.MOD_ID);

        IN_WORLD = register(InWorldRecipe.NAME);
    }

    private static <T extends Recipe<?>> Supplier<RecipeType<T>> register(String name) {
        return RECIPE_TYPES.register(name, () -> RecipeType.simple(TFCWoodworking.getResource(name)));
    }
}
