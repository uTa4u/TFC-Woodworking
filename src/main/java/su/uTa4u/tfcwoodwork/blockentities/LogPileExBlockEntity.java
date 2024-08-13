package su.uTa4u.tfcwoodwork.blockentities;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.blockentities.LogPileBlockEntity;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import su.uTa4u.tfcwoodwork.container.LogPileExContainer;

import javax.annotation.Nullable;
import java.util.Iterator;

public class LogPileExBlockEntity extends InventoryBlockEntity<ItemStackHandler> implements MenuProvider {
    public static final int SLOTS = 12;
    private static final int LOG_FULL_LIMIT = 4;
    private static final int LOG_HALF_LIMIT = 8;
    private static final int LOG_QUARTER_LIMIT = 16;
    private static final Component NAME = Component.translatable("tfc.block_entity.log_pile");
    private int playersUsing = 0;

    public LogPileExBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LOG_PILE.get(), pos, state, defaultInventory(SLOTS), NAME);
    }

    public void setAndUpdateSlots(int slot) {
        super.setAndUpdateSlots(slot);
        if (this.level != null && !this.level.isClientSide() && this.playersUsing == 0 && this.isEmpty()) {
            this.level.setBlockAndUpdate(this.worldPosition, Blocks.AIR.defaultBlockState());
        }

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
        int count = 0;

        ItemStack stack;
        for (Iterator var2 = Helpers.iterate(this.inventory).iterator(); var2.hasNext(); count += stack.getCount()) {
            stack = (ItemStack) var2.next();
        }

        return count;
    }

    public int getSlotStackLimit(int slot) {
        return switch (slot % 4) {
            case 0 -> LOG_QUARTER_LIMIT;
            case 1 -> LOG_HALF_LIMIT;
            default -> LOG_FULL_LIMIT;
        };
    }

    public boolean isItemValid(int slot, ItemStack stack) {
        return Helpers.isItem(stack.getItem(), TFCTags.Items.LOG_PILE_LOGS);
    }

    @Nullable
    public AbstractContainerMenu createMenu(int windowID, Inventory inv, Player player) {
        return LogPileExContainer.create(this, inv, windowID);
    }

}
