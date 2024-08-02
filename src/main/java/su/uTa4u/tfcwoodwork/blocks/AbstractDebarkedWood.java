package su.uTa4u.tfcwoodwork.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;

public abstract class AbstractDebarkedWood extends Block {
    public static final BooleanProperty CHOPPED = BooleanProperty.create("chopped");
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final BlockBehaviour.Properties prop;

    static {
        prop = BlockBehaviour.Properties.of()
                .strength(7.0F)
                .sound(SoundType.WOOD)
                .requiresCorrectToolForDrops()
                .ignitedByLava()
                .mapColor(MapColor.WOOD);
    }

    public AbstractDebarkedWood() {
        super(prop);
        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(CHOPPED, Boolean.valueOf(false)));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return canSupportRigidBlock(level, pos.below());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState nbourState, LevelAccessor level, BlockPos pos, BlockPos nbourPos) {
        return !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, dir, nbourState, level, pos, nbourPos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
        builder.add(CHOPPED);
    }
}
