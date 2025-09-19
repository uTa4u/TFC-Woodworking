package su.uTa4u.tfcwoodwork.recipes.inworld;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

// TODO: Also include a EnumMap<Direction, BlockState> nbours here and in the recipe to easily check for
//  conditions (no blocks around, no blocks above etc.)

// TODO: need to handle the case where we don't care about BlockState for our recipe
//  try using BlockStatePredicate or make my own version

public record InWorldRecipeInput(BlockState state, ItemStack stack) implements RecipeInput {

    @Override
    @NotNull
    public ItemStack getItem(int slot) {
        if (slot != 0) throw new IllegalArgumentException("No item for index " + slot);
        return this.stack();
    }

    @Override
    public int size() {
        return 1;
    }
}
