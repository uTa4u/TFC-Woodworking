package su.uTa4u.tfcwoodwork.blocks;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.*;
import net.dries007.tfc.common.blocks.devices.BurningLogPileBlock;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;
import net.dries007.tfc.util.Helpers;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import su.uTa4u.tfcwoodwork.blockentities.LogPileExBlockEntity;
import su.uTa4u.tfcwoodwork.blockentities.ModBlockEntities;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_AXIS;

public class LogPileExBlock extends DeviceBlock implements IForgeBlockExtension, EntityBlockExtension {
    public static final EnumProperty<Direction.Axis> AXIS = HORIZONTAL_AXIS;

    public static final IntegerProperty COUNT = TFCBlockStateProperties.COUNT_1_16;

    private static final VoxelShape[][] SHAPES_BY_DIR_BY_COUNT = Util.make(new VoxelShape[2][16], shapes -> {
        double[][] box2ByCount = new double[16][6];
        double[][] box1ByCount = new double[16][6];
        int layer;
        int row;
        for (int i = 0; i < 16; i++) {
            layer = i / 4;
            row = i % 4 + 1;
            box2ByCount[i] = new double[]{0, 4 * layer, 0, 16, 4 * layer + 4, 4 * row};
            box1ByCount[i] = new double[]{0, 0, 0, 16, 4 * layer, 16};
        }

        for (int count = 0; count < 16; count++) {
            VoxelShape box1 = Helpers.rotateShape(Direction.SOUTH, box1ByCount[count][0], box1ByCount[count][1], box1ByCount[count][2], box1ByCount[count][3], box1ByCount[count][4], box1ByCount[count][5]);
            VoxelShape box2 = Helpers.rotateShape(Direction.SOUTH, box2ByCount[count][0], box2ByCount[count][1], box2ByCount[count][2], box2ByCount[count][3], box2ByCount[count][4], box2ByCount[count][5]);
            shapes[0][count] = Shapes.or(box1, box2);
        }

        for (int count = 0; count < 16; count++) {
            VoxelShape box1 = Helpers.rotateShape(Direction.EAST, box1ByCount[count][0], box1ByCount[count][1], box1ByCount[count][2], box1ByCount[count][3], box1ByCount[count][4], box1ByCount[count][5]);
            VoxelShape box2 = Helpers.rotateShape(Direction.EAST, box2ByCount[count][0], box2ByCount[count][1], box2ByCount[count][2], box2ByCount[count][3], box2ByCount[count][4], box2ByCount[count][5]);
            shapes[1][count] = Shapes.or(box1, box2);
        }
    });

    public LogPileExBlock(ExtendedProperties properties) {
        super(properties, InventoryRemoveBehavior.DROP);
        registerDefaultState(getStateDefinition().any().setValue(AXIS, Direction.Axis.X).setValue(COUNT, 1));
    }

    @Override
    protected void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        BurningLogPileBlock.lightLogPile(level, pos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(AXIS, context.getHorizontalDirection().getAxis()).setValue(COUNT, 1);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(AXIS).add(COUNT));
    }

    @Override
    @NotNull
    protected BlockState updateShape(@NotNull BlockState state, @NotNull Direction facing, @NotNull BlockState facingState, LevelAccessor levelAccess, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        if (!levelAccess.isClientSide() && levelAccess instanceof Level level) {
            if ((facing == Direction.DOWN && !facingState.isFaceSturdy(levelAccess, facingPos, Direction.UP)) && !(facingState.getBlock() instanceof LogPileExBlock)) {
                return Blocks.AIR.defaultBlockState();
            }
            if (Helpers.isBlock(facingState, BlockTags.FIRE)) {
                BurningLogPileBlock.lightLogPile(level, currentPos);
            }
        }
        return super.updateShape(state, facing, facingState, levelAccess, currentPos, facingPos);
    }

    @Override
    @NotNull
    protected ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (!player.isShiftKeyDown() && level.getBlockEntity(pos) instanceof LogPileExBlockEntity logPile) {
            if (Helpers.isItem(stack.getItem(), TFCTags.Items.LOG_PILE_LOGS)) {
                insertAndPushUp(stack, state, level, pos, logPile, false);
            } else if (stack.isEmpty()) {
                extractFromTop(level, pos, player, false);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public static void extractFromTop(Level level, BlockPos pos, Player player, boolean all) {
        if (level.getBlockState(pos.above()).is(TFCBlocks.LOG_PILE.get())) {
            extractFromTop(level, pos.above(), player, all);
        } else if (level.getBlockEntity(pos) instanceof LogPileExBlockEntity logPile) {
            for (int i = 0; i < LogPileExBlockEntity.SLOTS; i++) {
                ItemStack slotStack = logPile.getInventory().getStackInSlot(i);
                if (!slotStack.isEmpty()) {
                    ItemHandlerHelper.giveItemToPlayer(player, slotStack.split(1));
                    logPile.setAndUpdateSlots(-1);
                    if (!all) {
                        break;
                    }
                }
            }
        }
    }

    public static void insertAndPushUp(ItemStack stack, BlockState state, Level level, BlockPos pos, LogPileExBlockEntity logPile, boolean all) {
        if (dumbInsert(stack, state, level, pos, logPile, all) && !all) {
            return;
        }
        final BlockPos abovePos = pos.above();
        if (level.getBlockState(abovePos).isAir() && logPile.isFull() && !stack.isEmpty()) {
            level.setBlockAndUpdate(abovePos, ModBlocks.LOG_PILE_EX.get().defaultBlockState().setValue(HORIZONTAL_AXIS, state.getValue(HORIZONTAL_AXIS)));
            if (level.getBlockEntity(abovePos) instanceof LogPileExBlockEntity pileAbove) {
                BlockState stateAbove = level.getBlockState(abovePos);
                if (dumbInsert(stack, stateAbove, level, abovePos, pileAbove, all)) {
                    return;
                } else {
                    level.removeBlock(abovePos, false);
                }
            }
        }
        if (level.getBlockState(abovePos).getBlock() instanceof LogPileExBlock && logPile.isFull() && level.getBlockEntity(abovePos) instanceof LogPileExBlockEntity pileAbove) {
            BlockState stateAbove = level.getBlockState(abovePos);
            LogPileExBlock.insertAndPushUp(stack, stateAbove, level, abovePos, pileAbove, all);
        }
    }

    private static boolean dumbInsert(ItemStack stack, BlockState state, Level level, BlockPos pos, LogPileExBlockEntity logPile, boolean all) {
        if (all) {
            ItemStack insertStack = stack.copy();
            insertStack = logPile.insertItemStack(insertStack);
            if (insertStack.getCount() < stack.getCount()) {
                Helpers.playPlaceSound(null, level, pos, SoundType.WOOD);
                stack.setCount(insertStack.getCount());
                logPile.setAndUpdateSlots(-1);
                return true;
            }
        } else if (logPile.insertItemStack(stack.copyWithCount(1)).isEmpty()) {
            Helpers.playPlaceSound(null, level, pos, state);
            stack.shrink(1);
            logPile.setAndUpdateSlots(-1);
            return true;
        }
        return false;
    }

    @Override
    @NotNull
    public ItemStack getCloneItemStack(@NotNull BlockState state, @NotNull HitResult target, LevelReader level, @NotNull BlockPos pos, @NotNull Player player) {
        return level.getBlockEntity(pos, ModBlockEntities.LOG_PILE_EX.get())
                .map(pile -> {
                    var inv = pile.getInventory();
                    for (int i = 0; i < inv.getSlots(); i++) {
                        final ItemStack stack = inv.getStackInSlot(i);
                        if (!stack.isEmpty()) {
                            return stack.copy();
                        }
                    }
                    return ItemStack.EMPTY;
                }).orElse(ItemStack.EMPTY);
    }

    @Override
    protected boolean canSurvive(@NotNull BlockState state, LevelReader level, BlockPos pos) {
        BlockState belowState = level.getBlockState(pos.below());
        return Block.isFaceFull(belowState.getCollisionShape(level, pos.below()), Direction.UP) || belowState.getBlock() instanceof LogPileExBlock;
    }

    public static VoxelShape getShapeByDirByCount(Direction.Axis axis, int count) {
        if (axis == Direction.Axis.X) {
            return SHAPES_BY_DIR_BY_COUNT[0][count - 1];
        } else {
            return SHAPES_BY_DIR_BY_COUNT[1][count - 1];
        }
    }

    @Override
    @NotNull
    protected VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return getShapeByDirByCount(state.getValue(AXIS), state.getValue(COUNT));
    }

    @Override
    @NotNull
    protected VoxelShape getCollisionShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return getShapeByDirByCount(state.getValue(AXIS), state.getValue(COUNT));
    }

    @Override
    @NotNull
    protected VoxelShape getVisualShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return getShapeByDirByCount(state.getValue(AXIS), state.getValue(COUNT));
    }
}