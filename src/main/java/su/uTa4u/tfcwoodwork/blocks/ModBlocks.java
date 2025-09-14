package su.uTa4u.tfcwoodwork.blocks;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import su.uTa4u.tfcwoodwork.TFCWoodworking;
import su.uTa4u.tfcwoodwork.items.ModItems;

import java.util.Map;
import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS;

    public static final Map<Wood, Map<BlockType, TFCBlocks.Id<Block>>> WOODS;

    static {
        BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, TFCWoodworking.MOD_ID);
        WOODS = Helpers.mapOf(Wood.class, (wood) ->
                Helpers.mapOf(BlockType.class, (type) -> registerBlockWithItem(type.getName(wood), type.sup))
        );
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
