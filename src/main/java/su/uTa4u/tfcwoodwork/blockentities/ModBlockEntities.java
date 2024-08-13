package su.uTa4u.tfcwoodwork.blockentities;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import su.uTa4u.tfcwoodwork.TFCWoodworking;
import su.uTa4u.tfcwoodwork.blocks.ModBlocks;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES;
    public static final RegistryObject<BlockEntityType<LogPileExBlockEntity>> LOG_PILE;
    public static final RegistryObject<BlockEntityType<BurningLogPileExBlockEntity>> BURNING_LOG_PILE;

    static {
        BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, TFCWoodworking.MOD_ID);
        LOG_PILE = register("log_pile", LogPileExBlockEntity::new, ModBlocks.LOG_PILE);
        BURNING_LOG_PILE = register("burning_log_pile", BurningLogPileExBlockEntity::new, ModBlocks.BURNING_LOG_PILE);
    }

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, Supplier<? extends Block> block) {
        return BLOCK_ENTITIES.register(name, () -> BlockEntityType.Builder.of(factory, block.get()).build(null));
    }
}
