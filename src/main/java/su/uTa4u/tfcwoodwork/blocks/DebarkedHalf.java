package su.uTa4u.tfcwoodwork.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DebarkedHalf extends AbstractDebarkedWood {
    private static final VoxelShape AABB_NS = Shapes.box(0.0625, 0, 0.3125, 0.9375, 1, 0.6875);
    private static final VoxelShape AABB_WE = Shapes.box(0.3125, 0, 0.0625, 0.6875, 1, 0.9375);

    public DebarkedHalf() {
        super();
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(FACING) == Direction.NORTH || state.getValue(FACING) == Direction.SOUTH) {
            return AABB_NS;
        } else if (state.getValue(FACING) == Direction.WEST || state.getValue(FACING) == Direction.EAST) {
            return AABB_WE;
        } else {
            return AABB_NS; //up and down are unreachable (i think)
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }
}
