package su.uTa4u.tfcwoodwork.blockentities;

import net.dries007.tfc.common.blockentities.BurningLogPileBlockEntity;
import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;
import net.dries007.tfc.common.blocks.CharcoalPileBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import su.uTa4u.tfcwoodwork.blocks.ModBlocks;

public class BurningLogPileExBlockEntity extends TickCounterBlockEntity {
    protected long lastUpdateTick;
    private int logs;

    public BurningLogPileExBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BURNING_LOG_PILE.get(), pos, state);
    }

    //TODO: maybe make charcoal faster based on halves and quarters present when ignited
    public static void serverTick(Level level, BlockPos pos, BlockState state, BurningLogPileExBlockEntity entity) {
        if (entity.lastUpdateTick > 0L && entity.getTicksSinceUpdate() > (long) TFCConfig.SERVER.charcoalTicks.get()) {
            entity.createCharcoal();
        }

    }

    //TODO: maybe play with charcoal output based on halves and quarters present when ignited
    private static int getCharcoalAmount(Level level, int logs) {
        return (int) Mth.clamp((double) logs * (0.25D + 0.25D * (double) level.getRandom().nextFloat()), 0.0D, 8.0D);
    }

    private static boolean isPileBlock(BlockState state) {
        return Helpers.isBlock(state, TFCBlocks.CHARCOAL_PILE.get()) || Helpers.isBlock(state, ModBlocks.BURNING_LOG_PILE.get());
    }

    public void loadAdditional(CompoundTag nbt) {
        this.logs = nbt.getInt("logs");
        super.loadAdditional(nbt);
    }

    public void saveAdditional(CompoundTag nbt) {
        nbt.putInt("logs", this.logs);
        super.saveAdditional(nbt);
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

                for (BlockState currentState = this.level.getBlockState(currentPos); currentState.is(ModBlocks.BURNING_LOG_PILE.get()); currentState = this.level.getBlockState(currentPos)) {
                    ++height;
                    int logs = this.level.getBlockEntity(currentPos, ModBlockEntities.BURNING_LOG_PILE.get()).map(BurningLogPileExBlockEntity::getLogs).orElse(0);
                    charcoal += getCharcoalAmount(this.level, logs);
                    currentPos.move(Direction.DOWN);
                }

                currentPos.set(this.worldPosition).move(0, 1 - height, 0);
                BlockState belowState = this.level.getBlockState(currentPos.below());
                int currentAmount;
                int amount;
                if (belowState.is(TFCBlocks.CHARCOAL_PILE.get())) {
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

}
