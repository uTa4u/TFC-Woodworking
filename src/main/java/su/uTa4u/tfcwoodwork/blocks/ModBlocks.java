package su.uTa4u.tfcwoodwork.blocks;

import net.dries007.tfc.common.blockentities.BurningLogPileBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import su.uTa4u.tfcwoodwork.TFCWoodworking;
import su.uTa4u.tfcwoodwork.blockentities.BurningLogPileExBlockEntity;
import su.uTa4u.tfcwoodwork.blockentities.ModBlockEntities;
import su.uTa4u.tfcwoodwork.items.ModItems;

import java.util.Map;
import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS;

    public static final Map<Wood, Map<BlockType, TFCBlocks.Id<Block>>> WOODS;

    public static final DeferredHolder<Block, Block> LOG_PILE_EX;
    public static final DeferredHolder<Block, Block> BURNING_LOG_PILE_EX;

    static {
        BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, TFCWoodworking.MOD_ID);
        WOODS = Helpers.mapOf(Wood.class, (wood) ->
                Helpers.mapOf(BlockType.class, (type) -> registerBlockWithItem(type.getName(wood), type.sup))
        );
        LOG_PILE_EX = registerBlock("log_pile_ex", () -> new LogPileExBlock(ExtendedProperties.of(MapColor.WOOD).strength(0.6F).sound(SoundType.WOOD).flammable(60, 30).blockEntity(ModBlockEntities.LOG_PILE_EX)));
        BURNING_LOG_PILE_EX = registerBlock("burning_log_pile_ex", () -> new BurningLogPileExBlock(ExtendedProperties.of(MapColor.WOOD).randomTicks().strength(0.6F).sound(SoundType.WOOD).flammableLikeLogs().blockEntity(ModBlockEntities.BURNING_LOG_PILE_EX).serverTicks(BurningLogPileExBlockEntity::serverTick).cloneItem(Items.CHARCOAL).noOcclusion()));
    }

    private static <T extends Block> DeferredHolder<Block, T> registerBlock(String name, Supplier<T> blockSup) {
        return BLOCKS.register(name, blockSup);
    }

    private static <T extends Block> TFCBlocks.Id<T> registerBlockWithItem(String name, Supplier<T> blockSup) {
        DeferredHolder<Block, T> ret = registerBlock(name, blockSup);
        registerBlockItem(name, ret);
        return new TFCBlocks.Id<>(ret);
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredHolder<Block, T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}
