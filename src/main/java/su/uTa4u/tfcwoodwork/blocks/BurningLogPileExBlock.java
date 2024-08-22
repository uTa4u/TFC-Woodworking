package su.uTa4u.tfcwoodwork.blocks;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.devices.BurningLogPileBlock;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.HitResult;
import su.uTa4u.tfcwoodwork.blockentities.BurningLogPileExBlockEntity;
import su.uTa4u.tfcwoodwork.blockentities.LogPileExBlockEntity;
import su.uTa4u.tfcwoodwork.blockentities.ModBlockEntities;

public class BurningLogPileExBlock extends BaseEntityBlock implements IForgeBlockExtension, EntityBlockExtension {
    private static final int TICK_DELAY = 30;
    private static final ExtendedProperties prop = ExtendedProperties.of(MapColor.WOOD)
            .randomTicks()
            .strength(0.6F)
            .sound(SoundType.WOOD)
            .flammableLikeLogs()
            .blockEntity(ModBlockEntities.BURNING_LOG_PILE)
            .serverTicks(BurningLogPileExBlockEntity::serverTick);

    public static void lightLogPile(Level level, BlockPos pos) {
        BlockEntity blockEntity1 = level.getBlockEntity(pos);
        if (blockEntity1 instanceof LogPileExBlockEntity pile) {
            int logs = pile.logCount();
            pile.clearContent();
            level.setBlockAndUpdate(pos, ModBlocks.BURNING_LOG_PILE.get().defaultBlockState());
            Helpers.playSound(level, pos, SoundEvents.BLAZE_SHOOT);
            BlockEntity blockEntity2 = level.getBlockEntity(pos);
            if (blockEntity2 instanceof BurningLogPileExBlockEntity burningPile) {
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

    private static boolean isValidCoverBlock(BlockState offsetState, Level level, BlockPos pos, Direction side) {
        if (Helpers.isBlock(offsetState, TFCTags.Blocks.CHARCOAL_COVER_WHITELIST)) {
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

    public BurningLogPileExBlock() {
        super(prop.properties());
    }

    public ExtendedProperties getExtendedProperties() {
        return prop;
    }

    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        tryLightNearby(level, pos);
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
        if (level.getBlockState(pos.above(2)).canBeReplaced()) {
            double x = (float)pos.getX() + rand.nextFloat();
            double y = (double)pos.getY() + 1.125D;
            double z = (float)pos.getZ() + rand.nextFloat();
            level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.1F + 0.1F * rand.nextFloat(), 0.0D);
            if (rand.nextInt(12) == 0) {
                level.playLocalSound(x, y, z, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.6F, false);
            }

            level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, (0.5F - rand.nextFloat()) / 10.0F, 0.1F + rand.nextFloat() / 8.0F, (0.5F - rand.nextFloat()) / 10.0F);
        }

    }

    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        return new ItemStack(Items.CHARCOAL);
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

}
