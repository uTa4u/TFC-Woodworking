package su.uTa4u.tfcwoodwork.client;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import su.uTa4u.tfcwoodwork.TFCWoodworking;
import su.uTa4u.tfcwoodwork.client.render.WoodProjectilefRenderer;
import su.uTa4u.tfcwoodwork.client.screen.LogPileExScreen;
import su.uTa4u.tfcwoodwork.container.ModContainerTypes;
import su.uTa4u.tfcwoodwork.entities.ModEntities;

@Mod.EventBusSubscriber(modid = TFCWoodworking.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void doSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.LOG_HALF_PROJ.get(), WoodProjectilefRenderer::new);
        EntityRenderers.register(ModEntities.LOG_QUARTER_PROJ.get(), WoodProjectilefRenderer::new);

        MenuScreens.register(ModContainerTypes.LOG_PILE.get(), LogPileExScreen::new);
    }
}
