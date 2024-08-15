package su.uTa4u.tfcwoodwork.container;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import su.uTa4u.tfcwoodwork.TFCWoodworking;
import su.uTa4u.tfcwoodwork.blockentities.LogPileExBlockEntity;
import su.uTa4u.tfcwoodwork.blockentities.ModBlockEntities;

import static net.dries007.tfc.util.registry.RegistrationHelpers.registerContainer;

public class ModContainerTypes {
    public static final DeferredRegister<MenuType<?>> CONTAINERS;

    public static final RegistryObject<MenuType<LogPileExContainer>> LOG_PILE;

    static {
        CONTAINERS = DeferredRegister.create(Registries.MENU, TFCWoodworking.MOD_ID);

        LOG_PILE = registerContainer(CONTAINERS, "log_pile", (windowId, playerInventory, buffer) -> {
            Level level = playerInventory.player.level();
            BlockPos pos = buffer.readBlockPos();
            LogPileExBlockEntity entity = level.getBlockEntity(pos, ModBlockEntities.LOG_PILE.get()).orElseThrow();
            return LogPileExContainer.create(entity, playerInventory, windowId);
        });
    }

}
