package su.uTa4u.tfcwoodwork.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Random;

public class DebarkedQuarter extends AbstractDebarkedWood {
    private static final VoxelShape AABB = Shapes.box(0.3125, 0, 0.3125, 0.6875, 1, 0.6875);
    private static final Random rng = new Random();

    public DebarkedQuarter() { super(); }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AABB;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(CHOPPED, rng.nextBoolean());
    }

}
