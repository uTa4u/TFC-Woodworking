package su.uTa4u.tfcwoodwork.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractDebarkedWood extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final BlockBehaviour.Properties PROPERTIES =
            BlockBehaviour.Properties.of()
                    .strength(7.0f)
                    .sound(SoundType.WOOD)
                    .requiresCorrectToolForDrops()
                    .ignitedByLava()
                    .mapColor(MapColor.WOOD);

    public AbstractDebarkedWood() {
        super(PROPERTIES);
    }

    @Override
    @NotNull
    public BlockState updateShape(BlockState state, @NotNull Direction dir, @NotNull BlockState nbourState, @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos nbourPos) {
        return !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, dir, nbourState, level, pos, nbourPos);
    }
}
