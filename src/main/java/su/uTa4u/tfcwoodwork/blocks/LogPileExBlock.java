package su.uTa4u.tfcwoodwork.blocks;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import su.uTa4u.tfcwoodwork.blockentities.ModBlockEntities;

import java.util.Optional;

public class LogPileExBlock extends DeviceBlock implements IForgeBlockExtension, EntityBlockExtension {
    public static final EnumProperty<Direction.Axis> AXIS;
    private static final ExtendedProperties prop = ExtendedProperties.of(MapColor.WOOD)
            .strength(0.6F)
            .sound(SoundType.WOOD)
            .flammable(60, 30)
            .blockEntity(ModBlockEntities.LOG_PILE);

    static {
        AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    }

    public LogPileExBlock() {
        super(prop, InventoryRemoveBehavior.DROP);
        this.registerDefaultState(this.getStateDefinition().any().setValue(AXIS, Direction.Axis.X));
    }

    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BurningLogPileExBlock.lightLogPile(level, pos);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(AXIS, context.getHorizontalDirection().getAxis());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(AXIS));
    }

    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor levelAccess, BlockPos currentPos, BlockPos facingPos) {
        if (!levelAccess.isClientSide() && levelAccess instanceof Level) {
            Level level = (Level) levelAccess;
            if (Helpers.isBlock(facingState, BlockTags.FIRE)) {
                BurningLogPileExBlock.lightLogPile(level, currentPos);
            }
        }

        return super.updateShape(state, facing, facingState, levelAccess, currentPos, facingPos);
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (!player.isShiftKeyDown()) {
            ItemStack stack = player.getItemInHand(hand);
            level.getBlockEntity(pos, ModBlockEntities.LOG_PILE.get()).ifPresent((logPile) -> {
                if (Helpers.isItem(stack.getItem(), TFCTags.Items.LOG_PILE_LOGS)) {
                    if (!level.isClientSide && Helpers.insertOne(Optional.of(logPile), stack)) {
                        Helpers.playPlaceSound(level, pos, state);
                        stack.shrink(1);
                    }
                } else if (player instanceof ServerPlayer) {
                    ServerPlayer serverPlayer = (ServerPlayer) player;
                    Helpers.openScreen(serverPlayer, logPile, pos);
                }

            });
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        level.getBlockEntity(pos, ModBlockEntities.LOG_PILE.get()).ifPresent((pile) -> {
            pile.getCapability(Capabilities.ITEM).map((cap) -> {
                for (int i = 0; i < cap.getSlots(); ++i) {
                    ItemStack stack = cap.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        return stack.copy();
                    }
                }

                return ItemStack.EMPTY;
            });
        });
        return ItemStack.EMPTY;
    }
}
