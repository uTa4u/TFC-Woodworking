package su.uTa4u.tfcwoodwork.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DebarkedLog extends AbstractDebarkedWood {
    private static final VoxelShape AABB = Shapes.box(0.0625, 0, 0.0625, 0.9375, 1, 0.9375);

    public DebarkedLog() {
        super();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AABB;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return canSupportRigidBlock(level, pos.below()) || state == level.getBlockState(pos.below());
    }
}
