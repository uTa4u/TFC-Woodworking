package su.uTa4u.tfcwoodwork.recipes;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import su.uTa4u.tfcwoodwork.TFCWoodworking;
import su.uTa4u.tfcwoodwork.recipes.inworld.InWorldRecipe;

import java.util.function.Supplier;

public final class ModRecipeSerializers {
    private ModRecipeSerializers() {
    }

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS;

    public static final Supplier<RecipeSerializer<InWorldRecipe>> IN_WORLD;

    static {
        RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, TFCWoodworking.MOD_ID);

        IN_WORLD = register(InWorldRecipe.NAME, InWorldRecipe.Serializer::new);
    }

    private static <T extends Recipe<?>> Supplier<RecipeSerializer<T>> register(String name, Supplier<RecipeSerializer<T>> sup) {
        return RECIPE_SERIALIZERS.register(name, sup);
    }
}
