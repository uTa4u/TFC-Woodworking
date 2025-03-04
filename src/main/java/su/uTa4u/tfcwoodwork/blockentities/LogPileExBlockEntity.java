package su.uTa4u.tfcwoodwork.blockentities;

import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import su.uTa4u.tfcwoodwork.Config;
import su.uTa4u.tfcwoodwork.ModTags;
import su.uTa4u.tfcwoodwork.container.LogPileExContainer;

import javax.annotation.Nullable;
import java.util.Iterator;

public class LogPileExBlockEntity extends InventoryBlockEntity<ItemStackHandler> implements MenuProvider {
    public static final int ROWS = 3;
    public static final int COLUMNS = 4;
    public static final int SLOTS = ROWS * COLUMNS;
    public static final int[] ROW_LIMIT = new int[]{
            Config.logPileLogQuarterCapacity,
            Config.logPileLogHalfCapacity,
            Config.logPileLogCapacity
    };
    private static final int TOTAL_LIMIT = Config.logPileLimit;
    private static final int SLOT_LIMIT = TOTAL_LIMIT / 4;
    private final int[] slotStackLimits = new int[SLOTS];
    private static final Component NAME = Component.translatable("tfc.block_entity.log_pile");
    private int playersUsing = 0;

    public LogPileExBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LOG_PILE.get(), pos, state, defaultInventory(SLOTS), NAME);
        updateSlotStackLimits();
    }

    private static int getRow(int slot) {
        return Math.floorDiv(slot, COLUMNS);
    }

    private void updateSlotStackLimits() {
        int count = 0;
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLUMNS; ++col) {
                count += this.inventory.getStackInSlot(row * COLUMNS + col).getCount() * SLOT_LIMIT / ROW_LIMIT[row];
            }
        }

        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLUMNS; ++col) {
                int slot = row * COLUMNS + col;
                int current = this.inventory.getStackInSlot(slot).getCount() * SLOT_LIMIT / ROW_LIMIT[row];
                slotStackLimits[slot] = Math.floorDiv(Math.min(TOTAL_LIMIT - count + current, SLOT_LIMIT), SLOT_LIMIT / ROW_LIMIT[row]);
            }
        }

    }

    public void setAndUpdateSlots(int slot) {
        super.setAndUpdateSlots(slot);
        if (this.level != null && !this.level.isClientSide() && this.playersUsing == 0 && this.isEmpty()) {
            this.level.setBlockAndUpdate(this.worldPosition, Blocks.AIR.defaultBlockState());
        }
        this.updateSlotStackLimits();
    }

    public void onOpen(Player player) {
        if (!player.isSpectator()) {
            ++this.playersUsing;
        }

    }

    public void onClose(Player player) {
        if (!player.isSpectator()) {
            --this.playersUsing;
            if (this.playersUsing < 0) {
                this.playersUsing = 0;
            }

            this.setAndUpdateSlots(-1);
        }

    }

    public boolean isEmpty() {
        Iterator iter = Helpers.iterate(this.inventory).iterator();

        ItemStack stack;
        do {
            if (!iter.hasNext()) {
                return true;
            }

            stack = (ItemStack) iter.next();
        } while (stack.isEmpty());

        return false;
    }

    public int logCount() {
        int logs = 0;
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLUMNS; ++col) {
                logs += this.inventory.getStackInSlot(row * COLUMNS + col).getCount() * ROW_LIMIT[2 - row] / SLOT_LIMIT;
            }
        }
        return logs;
    }

    public int getSlotStackLimit(int slot) {
        return this.slotStackLimits[slot];
    }

    public boolean isItemValid(int slot, ItemStack stack) {
        return switch (getRow(slot)) {
            case 0 -> Helpers.isItem(stack.getItem(), ModTags.Items.LOGS_QUARTER);
            case 1 -> Helpers.isItem(stack.getItem(), ModTags.Items.LOGS_HALF);
            case 2 -> Helpers.isItem(stack.getItem(), ModTags.Items.LOGS_LOG);
            default -> false;
        };
    }

    @Nullable
    public AbstractContainerMenu createMenu(int windowID, Inventory inv, Player player) {
        return LogPileExContainer.create(this, inv, windowID);
    }

}
