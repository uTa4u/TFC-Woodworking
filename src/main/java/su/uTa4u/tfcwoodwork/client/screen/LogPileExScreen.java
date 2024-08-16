package su.uTa4u.tfcwoodwork.client.screen;

import net.dries007.tfc.client.screen.TFCContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import su.uTa4u.tfcwoodwork.TFCWoodworking;
import su.uTa4u.tfcwoodwork.container.LogPileExContainer;

public class LogPileExScreen extends TFCContainerScreen<LogPileExContainer> {
    private static final ResourceLocation INVENTORY_4x4 = new ResourceLocation(TFCWoodworking.MOD_ID, "textures/gui/large_log_pile.png");

    public LogPileExScreen(LogPileExContainer container, Inventory playerInventory, Component name) {
        super(container, playerInventory, name, INVENTORY_4x4);
    }
}
