package su.uTa4u.tfcwoodwork.container;

import net.dries007.tfc.client.screen.TFCContainerScreen;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.container.BlockEntityContainer;
import net.dries007.tfc.common.container.CallbackSlot;
import net.dries007.tfc.common.container.TFCContainerTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.uTa4u.tfcwoodwork.blockentities.LogPileExBlockEntity;

public class LogPileExContainer extends BlockEntityContainer<LogPileExBlockEntity> {
    public LogPileExContainer(LogPileExBlockEntity logPile, Inventory playerInventory, int windowId) {
        super(ModContainerTypes.LOG_PILE.get(), windowId, logPile);
        logPile.onOpen(playerInventory.player);
    }

    public static @NotNull LogPileExContainer create(LogPileExBlockEntity logPile, Inventory playerInventory, int windowId) {
        return new LogPileExContainer(logPile, playerInventory, windowId).init(playerInventory);
    }

    protected boolean moveStack(ItemStack stack, int slotIndex) {
        boolean bool;
        switch (this.typeOf(slotIndex)) {
            case MAIN_INVENTORY:
            case HOTBAR:
                bool = !this.moveItemStackTo(stack, 0, LogPileExBlockEntity.SLOTS, false);
                break;
            case CONTAINER:
                bool = !this.moveItemStackTo(stack, this.containerSlots, this.slots.size(), false);
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return bool;
    }

    public void removed(Player player) {
        this.blockEntity.onClose(player);
        super.removed(player);
    }

    protected void addContainerSlots() {
        this.blockEntity.getCapability(Capabilities.ITEM).ifPresent((handler) -> {
            int index = 0;
            for (int dy = 0; dy < 3; ++dy) {
                for (int dx = 0; dx < 4; ++dx) {
                    int y = 22 + dy * 18;
                    int x = 52 + dy * 18;
                    this.addSlot(new CallbackSlot(this.blockEntity, handler, index, x, y));
                    ++index;
                }
            }
        });
    }
}
