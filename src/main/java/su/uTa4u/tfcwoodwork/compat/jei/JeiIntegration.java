package su.uTa4u.tfcwoodwork.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import su.uTa4u.tfcwoodwork.TFCWoodworking;
import su.uTa4u.tfcwoodwork.recipes.ModRecipeTypes;
import su.uTa4u.tfcwoodwork.recipes.inworld.Tool;

import java.util.Objects;

@JeiPlugin
public final class JeiIntegration implements IModPlugin {
    private static final ResourceLocation UID = TFCWoodworking.getResource("jei");

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registry) {
        for (var tool : Tool.VALUES) {
            registry.addRecipes(InWorldRecipeCategory.RECIPE_TYPES.get(tool),
                    Objects.requireNonNull(Minecraft.getInstance().level)
                            .getRecipeManager()
                            .getAllRecipesFor(ModRecipeTypes.IN_WORLD.get())
                            .stream()
                            .map(RecipeHolder::value)
                            .filter((recipe) -> recipe.tool() == tool)
                            .toList()
            );
        }
    }

    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registry) {
        for (var tool : Tool.VALUES) {
            registry.addRecipeCategories(new InWorldRecipeCategory(registry.getJeiHelpers().getGuiHelper(), tool));
        }
    }

    @Override
    @NotNull
    public ResourceLocation getPluginUid() {
        return UID;
    }
}
