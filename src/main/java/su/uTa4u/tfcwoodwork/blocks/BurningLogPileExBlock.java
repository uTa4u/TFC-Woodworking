package su.uTa4u.tfcwoodwork.blocks;

import net.dries007.tfc.common.TFCTags.Blocks;
import net.dries007.tfc.common.blocks.*;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import su.uTa4u.tfcwoodwork.blockentities.BurningLogPileExBlockEntity;
import su.uTa4u.tfcwoodwork.blockentities.LogPileExBlockEntity;

public class BurningLogPileExBlock extends Block implements IForgeBlockExtension, EntityBlockExtension {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final IntegerProperty COUNT = TFCBlockStateProperties.COUNT_1_64;
    private static final int TICK_DELAY = 30;
    private final ExtendedProperties properties;

    public BurningLogPileExBlock(ExtendedProperties properties) {
        super(properties.properties());
        this.properties = properties;
        this.registerDefaultState(this.getStateDefinition().any().setValue(AXIS, Axis.X).setValue(COUNT, 1));
    }

    public static void lightLogPile(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof LogPileExBlockEntity pile) {
            int logs = pile.logCount();
            BlockState state = level.getBlockState(pos);
            pile.clearContent();
            level.setBlockAndUpdate(pos, ModBlocks.BURNING_LOG_PILE_EX.get().defaultBlockState().setValue(AXIS, state.getValue(LogPileExBlock.AXIS)).setValue(COUNT, state.getValue(LogPileExBlock.COUNT)));
            Helpers.playSound(level, pos, SoundEvents.BLAZE_SHOOT);
            if (level.getBlockEntity(pos) instanceof BurningLogPileExBlockEntity burningPile) {
                burningPile.light(logs);
                tryLightNearby(level, pos);
            }
        }
    }

    public static void tryLightLogPile(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof LogPileExBlockEntity) {
            level.scheduleTick(pos, level.getBlockState(pos).getBlock(), TICK_DELAY);
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(AXIS).add(COUNT));
    }

    private static boolean isValidCoverBlock(BlockState offsetState, Level level, BlockPos pos, Direction side) {
        if (Helpers.isBlock(offsetState, Blocks.CHARCOAL_PIT_INSULATION)) {
            return true;
        } else {
            return !offsetState.isFlammable(level, pos, side) && offsetState.isFaceSturdy(level, pos, side);
        }
    }

    private static void tryLightNearby(Level level, BlockPos pos) {
        if (!level.isClientSide()) {
            BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
            for (Direction side : Helpers.DIRECTIONS) {
                cursor.setWithOffset(pos, side);
                BlockState offsetState = level.getBlockState(cursor);
                if (isValidCoverBlock(offsetState, level, cursor, side.getOpposite())) {
                    if (Helpers.isBlock(offsetState, ModBlocks.LOG_PILE_EX.get())) {
                        tryLightLogPile(level, cursor);
                    }
                } else if (offsetState.isAir()) {
                    level.setBlockAndUpdate(cursor, net.minecraft.world.level.block.Blocks.FIRE.defaultBlockState());
                } else if (level.random.nextInt(7) == 0) {
                    level.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.FIRE.defaultBlockState());
                    return;
                }
            }
        }
    }

    @Override
    @NotNull
    public ExtendedProperties getExtendedProperties() {
        return this.properties;
    }

    @Override
    protected void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        tryLightNearby(level, pos);
    }

    @Override
    public void animateTick(@NotNull BlockState state, Level level, BlockPos pos, @NotNull RandomSource rand) {
        if (level.getBlockState(pos.above(2)).canBeReplaced()) {
            double x = (float) pos.getX() + rand.nextFloat();
            double y = (float) pos.getY() + 1.125f;
            double z = (float) pos.getZ() + rand.nextFloat();
            level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.1f + 0.1f * rand.nextFloat(), 0.0);
            if (rand.nextInt(12) == 0) {
                level.playLocalSound(x, y, z, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5f + rand.nextFloat(), rand.nextFloat() * 0.7f + 0.6f, false);
            }

            level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, (0.5f - rand.nextFloat()) / 10.0f, 0.1f + rand.nextFloat() / 8.0f, (0.5f - rand.nextFloat()) / 10.0f);
        }

    }

    @Override
    @NotNull
    protected VoxelShape getVisualShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return LogPileExBlock.getShapeByDirByCount(state.getValue(AXIS), state.getValue(COUNT));
    }

    @Override
    @NotNull
    public RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }
}
