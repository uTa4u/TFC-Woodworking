package su.uTa4u.tfcwoodwork.blocks;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.BurningLogPileBlock;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import su.uTa4u.tfcwoodwork.blockentities.BurningLogPileExBlockEntity;
import su.uTa4u.tfcwoodwork.blockentities.LogPileExBlockEntity;
import su.uTa4u.tfcwoodwork.blockentities.ModBlockEntities;

public class BurningLogPileExBlock extends BurningLogPileBlock {
    private static final ExtendedProperties prop = ExtendedProperties.of(MapColor.WOOD)
            .randomTicks()
            .strength(0.6F)
            .sound(SoundType.WOOD)
            .flammableLikeLogs()
            .blockEntity(ModBlockEntities.BURNING_LOG_PILE)
            .serverTicks(BurningLogPileExBlockEntity::serverTick);

    public BurningLogPileExBlock() {
        super(prop);
    }

    private static boolean isValidCoverBlock(BlockState offsetState, Level level, BlockPos pos, Direction side) {
        if (Helpers.isBlock(offsetState, TFCTags.Blocks.CHARCOAL_COVER_WHITELIST)) {
            return true;
        } else {
            return !offsetState.isFlammable(level, pos, side) && offsetState.isFaceSturdy(level, pos, side);
        }
    }

    public static void lightLogPile(Level level, BlockPos pos) {
        BlockEntity blockEntity1 = level.getBlockEntity(pos);
        if (blockEntity1 instanceof LogPileExBlockEntity) {
            LogPileExBlockEntity pile = (LogPileExBlockEntity)blockEntity1;
            int logs = pile.logCount();
            pile.clearContent();
            level.setBlockAndUpdate(pos, ModBlocks.BURNING_LOG_PILE.get().defaultBlockState());
            Helpers.playSound(level, pos, SoundEvents.BLAZE_SHOOT);
            BlockEntity blockEntity2 = level.getBlockEntity(pos);
            if (blockEntity2 instanceof BurningLogPileExBlockEntity) {
                BurningLogPileExBlockEntity burningPile = (BurningLogPileExBlockEntity)blockEntity2;
                burningPile.light(logs);
                tryLightNearby(level, pos);
            }
        }
    }

    public static void tryLightLogPile(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof LogPileExBlockEntity) {
            level.scheduleTick(pos, level.getBlockState(pos).getBlock(), 30);
        }
    }

    private static void tryLightNearby(Level level, BlockPos pos) {
        if (!level.isClientSide()) {
            BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
            Direction[] dir = Helpers.DIRECTIONS;
            int len = dir.length;

            for(int s = 0; s < len; ++s) {
                Direction side = dir[s];
                cursor.setWithOffset(pos, side);
                BlockState offsetState = level.getBlockState(cursor);
                if (isValidCoverBlock(offsetState, level, cursor, side.getOpposite())) {
                    if (Helpers.isBlock(offsetState, ModBlocks.LOG_PILE.get())) {
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
}
