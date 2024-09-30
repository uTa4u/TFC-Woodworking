package su.uTa4u.tfcwoodwork.mixin;

import net.dries007.tfc.common.blockentities.BurningLogPileBlockEntity;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.BurningLogPileBlock;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import su.uTa4u.tfcwoodwork.blockentities.LogPileExBlockEntity;
import su.uTa4u.tfcwoodwork.blocks.ModBlocks;

@Mixin(value = BurningLogPileBlock.class, remap = false)
public abstract class BurningLogPileBlockMixin {

    @Final
    @Shadow
    private static final int TICK_DELAY = 0;

    @Shadow
    private static void tryLightNearby(Level level, BlockPos pos) {}

    @Inject(method = "lightLogPile",
            at = @At(
                    "TAIL"
            )
    )
    private static void lightLogPileEx(Level level, BlockPos pos, CallbackInfo ci) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof LogPileExBlockEntity pile) {
            int logs = pile.logCount();
            pile.clearContent();
            level.setBlockAndUpdate(pos, TFCBlocks.BURNING_LOG_PILE.get().defaultBlockState());
            Helpers.playSound(level, pos, SoundEvents.BLAZE_SHOOT);
            blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof BurningLogPileBlockEntity burningPile) {
                burningPile.light(logs);
                tryLightNearby(level, pos);
            }
        }
    }

    @Inject(method = "tryLightNearby",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/dries007/tfc/util/Helpers;isBlock(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/Block;)Z"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void tryLightNearby(Level level, BlockPos pos, CallbackInfo ci, BlockPos.MutableBlockPos cursor, Direction[] var3, int var4, int var5, Direction side, BlockState offsetState) {
        if (Helpers.isBlock(offsetState, ModBlocks.LOG_PILE.get())) {
            BurningLogPileBlock.tryLightLogPile(level, cursor);
        }
    }

    @Inject(method = "tryLightLogPile",
            at = @At(
                    value = "TAIL"
            )
    )
    private static void tryLightLogPile(Level level, BlockPos pos, CallbackInfo ci) {
        if (level.getBlockEntity(pos) instanceof LogPileExBlockEntity) {
            level.scheduleTick(pos, level.getBlockState(pos).getBlock(), TICK_DELAY);
        }
    }

}
