package su.uTa4u.tfcwoodwork.compat.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.uTa4u.tfcwoodwork.TFCWoodworking;
import su.uTa4u.tfcwoodwork.recipes.inworld.InWorldRecipe;
import su.uTa4u.tfcwoodwork.recipes.inworld.Tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class InWorldRecipeCategory implements IRecipeCategory<InWorldRecipe> {
    public static final Map<Tool, RecipeType<InWorldRecipe>> RECIPE_TYPES = Helpers.mapOf(Tool.class, (tool) -> RecipeType.create(TFCWoodworking.MOD_ID, InWorldRecipe.NAME + "/" + tool.getSerializedName(), InWorldRecipe.class));

    private final Tool tool;
    private final Component title;
    private final IDrawable icon;
    private final IDrawable background;

    public InWorldRecipeCategory(IGuiHelper helper, Tool tool) {
        this.tool = tool;
        this.title = Component.translatable(TFCWoodworking.MOD_ID + ".jei." + InWorldRecipe.NAME + "." + this.tool.getSerializedName());
        this.icon = helper.createDrawableItemLike(TFCItems.METAL_ITEMS.get(Metal.BLUE_STEEL).get(Metal.ItemType.AXE));
        this.background = helper.createDrawable(TFCWoodworking.getResource("textures/gui/jei/in_world_recipe.png"), 0, 0, this.getWidth(), this.getHeight());
    }

    @Override
    @NotNull
    public RecipeType<InWorldRecipe> getRecipeType() {
        return RECIPE_TYPES.get(this.tool);
    }

    @Override
    @NotNull
    public Component getTitle() {
        return this.title;
    }

    @Override
    @Nullable
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull InWorldRecipe recipe, @NotNull IFocusGroup focuses) {
        // TODO: add sound effect info?
        // TODO: add offhand
        builder.addInputSlot(22, 2).addIngredients(recipe.tool().getInputItem());
        final var inputState = recipe.inputState();
        builder.addInputSlot(12, 40)
                .addRichTooltipCallback((view, tooltip) -> {
                    final var lines = new ArrayList<FormattedText>();
                    for (var blockState : inputState.getAcceptableStates()) {
                        for (var e : blockState.getValues().entrySet()) {
                            lines.add(getPropertyValueName(e));
                        }
                    }
                    if (!lines.isEmpty()) {
                        lines.addFirst(Component.translatable(TFCWoodworking.MOD_ID + ".jei.acceptable_states").append(":"));
                        tooltip.addAll(lines);
                    }
                })
                .addItemLike(inputState.getBlock());

        builder.addOutputSlot(51, 21).addItemLike(recipe.resultState().getBlock());
        final var resultItems = recipe.resultItems();
        final int size = 2;
        for (int i = 0; i < 6; ++i) {
            int x = 71 + 19 * (i % size);
            int y = 2 + 19 * Mth.floorDiv(i, size);
            if (i >= resultItems.size()) break;
            final var pair = resultItems.get(i);
            final var action = pair.getSecond();
            final var itemStack = pair.getFirst();
            builder.addOutputSlot(x, y)
                    .addRichTooltipCallback((view, tooltip) -> tooltip.add(Component.translatable(TFCWoodworking.MOD_ID + ".jei.itemstack_action").append(": ").append(action.getPrettyName())))
                    .addItemStack(itemStack.copyWithCount(itemStack.getCount() * action.getCountFactor()));
        }
    }

    @Override
    public void draw(@NotNull InWorldRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
    }

    @Override
    public int getWidth() {
        return 108;
    }

    @Override
    public int getHeight() {
        return 58;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> Component getPropertyValueName(Map.Entry<Property<?>, Comparable<?>> entry) {
        try {
            final var property = (Property<T>) entry.getKey();
            return Component.literal("  " + property.getName() + " = " + property.getName((T) entry.getValue()));
        } catch (ClassCastException e) {
            return Component.empty();
        }
    }
}
