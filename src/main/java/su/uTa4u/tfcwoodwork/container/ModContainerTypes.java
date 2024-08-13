package su.uTa4u.tfcwoodwork.container;

import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.container.BlockEntityContainer;
import net.dries007.tfc.common.container.BlockEntityContainer.Factory;
import net.dries007.tfc.util.registry.RegistrationHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import su.uTa4u.tfcwoodwork.TFCWoodworking;
import su.uTa4u.tfcwoodwork.blockentities.ModBlockEntities;

import java.util.function.Supplier;

public class ModContainerTypes {
    public static final DeferredRegister<MenuType<?>> CONTAINERS;

    public static final RegistryObject<MenuType<LogPileExContainer>> LOG_PILE;

    static {
        CONTAINERS = DeferredRegister.create(Registries.MENU, TFCWoodworking.MOD_ID);

        LOG_PILE = registerBlock("log_pile", ModBlockEntities.LOG_PILE, LogPileExContainer::create);
    }

    private static <T extends InventoryBlockEntity<?>, C extends BlockEntityContainer<T>> RegistryObject<MenuType<C>> registerBlock(String name, Supplier<BlockEntityType<T>> type, Factory<T, C> factory) {
        return RegistrationHelpers.registerBlockEntityContainer(CONTAINERS, name, type, factory);
    }


}
