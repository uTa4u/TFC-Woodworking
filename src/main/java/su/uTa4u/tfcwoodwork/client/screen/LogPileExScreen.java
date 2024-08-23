package su.uTa4u.tfcwoodwork.client.screen;

import net.dries007.tfc.client.screen.BlockEntityScreen;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import su.uTa4u.tfcwoodwork.TFCWoodworking;
import su.uTa4u.tfcwoodwork.blockentities.LogPileExBlockEntity;
import su.uTa4u.tfcwoodwork.container.LogPileExContainer;

public class LogPileExScreen extends BlockEntityScreen<LogPileExBlockEntity, LogPileExContainer> {
    private static final ResourceLocation INVENTORY_4x4 = new ResourceLocation(TFCWoodworking.MOD_ID, "textures/gui/large_log_pile.png");

    public LogPileExScreen(LogPileExContainer container, Inventory playerInventory, Component name) {
        super(container, playerInventory, name, INVENTORY_4x4);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
        for (int slotIndex = 0; slotIndex < LogPileExBlockEntity.SLOTS; ++slotIndex) {
            if (this.blockEntity.getSlotStackLimit(slotIndex) <= 0) {
                int finalSlotIndex = slotIndex;
                this.blockEntity.getCapability(Capabilities.ITEM).ifPresent((inv) -> {
                    Slot slot = this.menu.slots.get(finalSlotIndex);
                    renderSlotHighlight(guiGraphics, slot.x, slot.y, 1);
                });
            }
        }
    }
}
