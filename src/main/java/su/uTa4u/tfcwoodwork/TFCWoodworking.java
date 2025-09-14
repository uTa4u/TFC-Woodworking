package su.uTa4u.tfcwoodwork;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;
import su.uTa4u.tfcwoodwork.blocks.ModBlocks;
import su.uTa4u.tfcwoodwork.client.render.WoodProjectilefRenderer;
import su.uTa4u.tfcwoodwork.entities.ModEntities;
import su.uTa4u.tfcwoodwork.items.ModItems;
import su.uTa4u.tfcwoodwork.sounds.ModSounds;

@Mod(TFCWoodworking.MOD_ID)
public class TFCWoodworking {
    public static final String MOD_ID = "tfcwoodwork";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("woodworking", () -> CreativeModeTab.builder()
            .title(Component.translatable("item_group." + MOD_ID + ".woodworking"))
            .icon(() -> new ItemStack(Items.OAK_WOOD))
            .displayItems((p, o) -> ModItems.ITEMS.getEntries().forEach(item -> o.accept(item.value())))
            .build()
    );

    public TFCWoodworking(IEventBus modEventBus, ModContainer modContainer) {
        // TODO: use EventBusSubscriber
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        if (ModList.get().isLoaded("dttfc")) {
            UseOnEventHandler.initDTTFCBlocks();
        }
    }

    @EventBusSubscriber(modid = TFCWoodworking.MOD_ID, value = Dist.CLIENT)
    public static final class ClientEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(ModEntities.LOG_HALF_PROJ.get(), WoodProjectilefRenderer::new);
            EntityRenderers.register(ModEntities.LOG_QUARTER_PROJ.get(), WoodProjectilefRenderer::new);
        }

        private ClientEvents() {
        }
    }

    public static ResourceLocation getResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

}
