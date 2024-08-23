package su.uTa4u.tfcwoodwork;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import su.uTa4u.tfcwoodwork.blockentities.ModBlockEntities;
import su.uTa4u.tfcwoodwork.blocks.ModBlocks;
import su.uTa4u.tfcwoodwork.container.ModContainerTypes;
import su.uTa4u.tfcwoodwork.entities.ModEntities;
import su.uTa4u.tfcwoodwork.items.ModItems;
import su.uTa4u.tfcwoodwork.sounds.ModSounds;

@Mod(TFCWoodworking.MOD_ID)
public class TFCWoodworking {
    public static final String MOD_ID = "tfcwoodwork";

    //private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final RegistryObject<CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("woodworking", () -> CreativeModeTab.builder()
            .title(Component.translatable("item_group." + MOD_ID + ".woodworking"))
            .icon(() -> new ItemStack(Items.OAK_WOOD))
            .displayItems((p, o) -> {
                for (RegistryObject<Item> entry : ModItems.ITEMS.getEntries()) {
                    o.accept(entry.get());
                }
            })
            .build()
    );

    public TFCWoodworking() {
        util.registerLogPileInteraction();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModContainerTypes.CONTAINERS.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(useOnEventHandler.class);

        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        if (ModList.get().isLoaded("dttfc")) {
            useOnEventHandler.initDTTFCBlocks();
        }
    }

}
