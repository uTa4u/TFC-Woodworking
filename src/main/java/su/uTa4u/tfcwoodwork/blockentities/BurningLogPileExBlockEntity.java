package su.uTa4u.tfcwoodwork.blockentities;

import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;
import net.dries007.tfc.common.blocks.CharcoalPileBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import su.uTa4u.tfcwoodwork.blocks.ModBlocks;

public class BurningLogPileExBlockEntity extends TickCounterBlockEntity {
    private int logs;

    public static void serverTick(Level level, BlockPos pos, BlockState state, BurningLogPileExBlockEntity entity) {
        if (entity.lastUpdateTick > 0L && entity.getTicksSinceUpdate() > (long) TFCConfig.SERVER.charcoalTicks.get()) {
            entity.createCharcoal();
        }
    }

    public BurningLogPileExBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BURNING_LOG_PILE_EX.get(), pos, state);
    }

    public void loadAdditional(CompoundTag nbt, @NotNull HolderLookup.Provider provider) {
        this.logs = nbt.getInt("logs");
        super.loadAdditional(nbt, provider);
    }

    public void saveAdditional(CompoundTag nbt, @NotNull HolderLookup.Provider provider) {
        nbt.putInt("logs", this.logs);
        super.saveAdditional(nbt, provider);
    }

    public void light(int logs) {
        this.logs = logs;
        this.resetCounter();
        this.markForSync();
    }

    public int getLogs() {
        return this.logs;
    }

    private void createCharcoal() {
        if (this.level != null) {
            if (!isPileBlock(this.level.getBlockState(this.worldPosition.above()))) {
                int charcoal = getCharcoalAmount(this.level, this.logs);
                int height = 1;
                BlockPos.MutableBlockPos currentPos = this.worldPosition.mutable().move(Direction.DOWN);

                for (BlockState currentState = this.level.getBlockState(currentPos); Helpers.isBlock(currentState, ModBlocks.BURNING_LOG_PILE_EX.get()); currentState = this.level.getBlockState(currentPos)) {
                    ++height;
                    int logs = this.level.getBlockEntity(currentPos, ModBlockEntities.BURNING_LOG_PILE_EX.get()).map(BurningLogPileExBlockEntity::getLogs).orElse(0);
                    charcoal += getCharcoalAmount(this.level, logs);
                    currentPos.move(Direction.DOWN);
                }

                currentPos.set(this.worldPosition).move(0, 1 - height, 0);
                BlockState belowState = this.level.getBlockState(currentPos.below());
                int currentAmount;
                int amount;
                if (Helpers.isBlock(belowState, TFCBlocks.CHARCOAL_PILE.get())) {
                    currentAmount = belowState.getValue(CharcoalPileBlock.LAYERS);
                    amount = Mth.clamp(charcoal, 0, 8 - currentAmount);
                    if (amount > 0) {
                        charcoal -= amount;
                        this.level.setBlockAndUpdate(currentPos.below(), belowState.setValue(CharcoalPileBlock.LAYERS, currentAmount + amount));
                    }
                }

                for (currentAmount = 0; currentAmount < height; ++currentAmount) {
                    if (charcoal > 0) {
                        amount = Mth.clamp(charcoal, 0, 8);
                        charcoal -= amount;
                        this.level.setBlockAndUpdate(currentPos, TFCBlocks.CHARCOAL_PILE.get().defaultBlockState().setValue(CharcoalPileBlock.LAYERS, amount));
                    } else {
                        this.level.setBlockAndUpdate(currentPos, Blocks.AIR.defaultBlockState());
                    }
                    currentPos.move(Direction.UP);
                }
            }
        }
    }

    private static int getCharcoalAmount(Level level, int logs) {
        return (int) Math.clamp(logs * (0.25f + 0.25f * level.getRandom().nextFloat()), 0.0f, 8.0f);
    }

    private static boolean isPileBlock(BlockState state) {
        return Helpers.isBlock(state, TFCBlocks.CHARCOAL_PILE.get()) || Helpers.isBlock(state, ModBlocks.BURNING_LOG_PILE_EX.get());
    }
}
