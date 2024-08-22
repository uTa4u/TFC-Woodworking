package su.uTa4u.tfcwoodwork.container;

import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.container.BlockEntityContainer;
import net.dries007.tfc.common.container.CallbackSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import su.uTa4u.tfcwoodwork.blockentities.LogPileExBlockEntity;

public class LogPileExContainer extends BlockEntityContainer<LogPileExBlockEntity> {
    public static LogPileExContainer create(LogPileExBlockEntity logPile, Inventory playerInventory, int windowId) {
        return new LogPileExContainer(logPile, playerInventory, windowId).init(playerInventory);
    }

    public LogPileExContainer(LogPileExBlockEntity logPile, Inventory playerInventory, int windowId) {
        super(ModContainerTypes.LOG_PILE.get(), windowId, logPile);
        logPile.onOpen(playerInventory.player);
    }

    @Override
    protected boolean moveStack(ItemStack stack, int slotIndex) {
        return switch (this.typeOf(slotIndex)) {
            case MAIN_INVENTORY, HOTBAR -> !this.moveItemStackTo(stack, 0, LogPileExBlockEntity.SLOTS, false);
            case CONTAINER -> !this.moveItemStackTo(stack, this.containerSlots, this.slots.size(), false);
            default -> throw new IncompatibleClassChangeError();
        };
    }

    @Override
    public void removed(Player player) {
        this.blockEntity.onClose(player);
        super.removed(player);
    }

    @Override
    protected void addContainerSlots() {
        this.blockEntity.getCapability(Capabilities.ITEM).ifPresent((handler) -> {
            int index = 0;
            for (int dy = 0; dy < LogPileExBlockEntity.ROWS; ++dy) {
                for (int dx = 0; dx < LogPileExBlockEntity.COLUMNS; ++dx) {
                    int y = 14 + dy * 18;
                    int x = 53 + dx * 18;
                    this.addSlot(new CallbackSlot(this.blockEntity, handler, index, x, y));
                    ++index;
                }
            }
        });
    }
}
