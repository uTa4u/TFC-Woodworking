package su.uTa4u.tfcwoodwork.blockentities;

import net.dries007.tfc.util.registry.RegistrationHelpers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import su.uTa4u.tfcwoodwork.TFCWoodworking;
import su.uTa4u.tfcwoodwork.blocks.ModBlocks;

import java.util.function.Supplier;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES;

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LogPileExBlockEntity>> LOG_PILE_EX;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BurningLogPileExBlockEntity>> BURNING_LOG_PILE_EX;

    static {
        BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, TFCWoodworking.MOD_ID);

        LOG_PILE_EX = register("log_pile_ex", LogPileExBlockEntity::new, ModBlocks.LOG_PILE_EX);
        BURNING_LOG_PILE_EX = register("burning_log_pile_ex", BurningLogPileExBlockEntity::new, ModBlocks.BURNING_LOG_PILE_EX);
    }

    private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, Supplier<? extends Block> blockSup) {
        return RegistrationHelpers.register(BLOCK_ENTITIES, name, factory, blockSup);
    }
}
