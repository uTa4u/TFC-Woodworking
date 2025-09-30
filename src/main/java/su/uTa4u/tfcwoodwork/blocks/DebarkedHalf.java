package su.uTa4u.tfcwoodwork.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import su.uTa4u.tfcwoodwork.ModTags;

public class DebarkedHalf extends AbstractDebarkedWood {
    private static final VoxelShape AABB_NS = Shapes.box(0.0625, 0, 0.3125, 0.9375, 1, 0.6875);
    private static final VoxelShape AABB_WE = Shapes.box(0.3125, 0, 0.0625, 0.6875, 1, 0.9375);

    public DebarkedHalf() {
        super();
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    @NotNull
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return switch (state.getValue(FACING)) {
            // up and down are unreachable (I think)
            case Direction.NORTH, Direction.SOUTH, Direction.UP, Direction.DOWN -> AABB_NS;
            case Direction.WEST, Direction.EAST -> AABB_WE;
        };
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, BlockPos pos) {
        final var below = pos.below();
        final var stateBelow = level.getBlockState(below);
        return state == stateBelow || canSupportCenter(level, below, Direction.UP);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }
}
