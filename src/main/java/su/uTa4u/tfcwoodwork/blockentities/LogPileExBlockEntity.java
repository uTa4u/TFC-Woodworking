package su.uTa4u.tfcwoodwork.blockentities;

import net.dries007.tfc.common.TFCTags.Items;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import su.uTa4u.tfcwoodwork.ModTags;
import su.uTa4u.tfcwoodwork.blocks.LogPileExBlock;

import java.util.function.Consumer;
import java.util.stream.IntStream;

public class LogPileExBlockEntity extends InventoryBlockEntity<ItemStackHandler> {
    public static final int SLOTS = 16;
    private boolean needsLogDispersion = true;

    private final int[] stackLimitBySlot = IntStream.range(0, SLOTS).map(i -> 1).toArray();

    public LogPileExBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LOG_PILE_EX.get(), pos, state, defaultInventory(SLOTS));
    }

    public void setAndUpdateSlots(int slot) {
        super.setAndUpdateSlots(slot);
        if (this.level != null && !this.level.isClientSide()) {
            this.suckLogsFromAbove();
            if (this.isEmpty()) {
                this.level.setBlockAndUpdate(this.worldPosition, Blocks.AIR.defaultBlockState());
            } else {
                this.level.setBlockAndUpdate(this.worldPosition, this.getBlockState().setValue(LogPileExBlock.COUNT, this.logCount()));
            }
        }
    }

    public boolean isEmpty() {
        for (ItemStack stack : Helpers.iterate(this.inventory)) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean isFull() {
        int limit = 0;
        for (int i : this.stackLimitBySlot) {
            limit += i;
        }
        int count = 0;
        for (ItemStack stack : Helpers.iterate(this.inventory)) {
            if (!stack.isEmpty()) {
                count += stack.getCount();
            }
        }
        return count == limit;
    }

    public int logCount() {
        int count = 0;
        for (ItemStack stack : Helpers.iterate(this.inventory)) {
            if (!stack.isEmpty()) {
                if (stack.is(ModTags.Items.LOGS_HALF)) {
                    count += stack.getCount() * 2;
                } else if (stack.is(ModTags.Items.LOGS_QUARTER)) {
                    count += stack.getCount();
                } else {
                    count += stack.getCount() * 4;
                }
            }
        }
        return Mth.clamp(Math.ceilDiv(count, 4), 1, 16);
    }

    private void suckLogsFromAbove() {
        if (level != null && !level.isClientSide()) {
            if (level.getBlockEntity(this.getBlockPos().above()) instanceof LogPileExBlockEntity logPileAbove && !logPileAbove.isEmpty()) {
                var aboveInventory = logPileAbove.getInventory();
                for (int i = 0; i < SLOTS; ++i) {
                    ItemStack stack = aboveInventory.getStackInSlot(i);
                    if (stack.isEmpty()) continue;
                    for (int j = 0; j < SLOTS; ++j) {
                        if (i == j) continue;
                        if (stack.isEmpty()) break;
                        if (this.inventory.getStackInSlot(j).isEmpty()) {
                            this.stackLimitBySlot[j] = getStackLimitForItem(stack);
                        }
                        stack = this.inventory.insertItem(j, stack, false);
                    }
                }
                logPileAbove.setAndUpdateSlots(-1);
            }
        }
    }

    private void disperseLogsToNewSlots() {
        for (int i = 0; i < SLOTS; ++i) {
            ItemStack stack = this.inventory.getStackInSlot(i);
            if (stack.getCount() <= this.stackLimitBySlot[i]) continue;
            for (int j = 0; j < SLOTS; ++j) {
                if (i == j) continue;
                if (stack.isEmpty()) break;
                ItemStack moveToStack = this.inventory.getStackInSlot(j);
                int count;
                if (moveToStack.isEmpty()) {
                    this.stackLimitBySlot[j] = getStackLimitForItem(stack);
                    count = stackLimitBySlot[j];
                } else {
                    count = stackLimitBySlot[j] - moveToStack.getCount();
                }
                if (count > 0) {
                    this.inventory.setStackInSlot(j, stack.split(count));
                }
            }
        }
    }

    protected void onLoadAdditional() {
        if (this.needsLogDispersion) {
            this.disperseLogsToNewSlots();
            this.needsLogDispersion = false;
        }
    }

    public int getSlotStackLimit(int slot) {
        return this.stackLimitBySlot[slot];
    }

    private void setStackLimitBySlot(int slot, int limit) {
        this.stackLimitBySlot[slot] = limit;
    }

    public boolean isItemValid(int slot, ItemStack stack) {
        return Helpers.isItem(stack.getItem(), Items.LOG_PILE_LOGS);
    }

    public ItemStack insertItemStack(ItemStack stack) {
        for (int slot = 0; slot < this.inventory.getSlots(); ++slot) {
            if (this.inventory.getStackInSlot(slot).isEmpty()) {
                this.setStackLimitBySlot(slot, getStackLimitForItem(stack));
            }
            stack = this.inventory.insertItem(slot, stack, false);
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }
        return stack;
    }

    public static int getStackLimitForItem(ItemStack stack) {
        if (stack.is(ModTags.Items.LOGS_HALF)) {
            return 2;
        } else if (stack.is(ModTags.Items.LOGS_QUARTER)) {
            return 4;
        } else {
            return 1;
        }
    }

}
