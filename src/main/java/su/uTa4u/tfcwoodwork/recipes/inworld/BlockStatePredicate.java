package su.uTa4u.tfcwoodwork.recipes.inworld;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import su.uTa4u.tfcwoodwork.TFCWoodworking;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class BlockStatePredicate implements Predicate<BlockState> {
    private final Block block;
    private final List<BlockState> acceptableStates;

    private BlockStatePredicate(Block block) {
        this.block = block;
        this.acceptableStates = new ArrayList<>();
    }

    @Override
    public boolean test(BlockState thatState) {
        if (!thatState.is(this.block)) return false;

        return this.acceptableStates.contains(thatState);
    }

    public <T extends Comparable<T>> BlockStatePredicate where(Property<T> property, T value) {
        final var blockState = this.block.defaultBlockState();
        if (blockState.hasProperty(property)) {
            this.acceptableStates.add(blockState.setValue(property, value));
        } else {
            TFCWoodworking.LOGGER.error("Tried to create BlockState for BlockStatePredicate with invalid property: " + property);
        }
        return this;
    }

    public static BlockStatePredicate forBlock(Block block) {
        return new BlockStatePredicate(block);
    }
}
