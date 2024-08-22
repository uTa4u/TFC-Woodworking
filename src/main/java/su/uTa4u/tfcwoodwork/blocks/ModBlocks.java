package su.uTa4u.tfcwoodwork.blocks;

import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.util.Helpers;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import su.uTa4u.tfcwoodwork.TFCWoodworking;
import su.uTa4u.tfcwoodwork.items.ModItems;

import java.util.Map;
import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS;

    public static final Map<Wood, Map<BlockType, RegistryObject<Block>>> WOODS;
    public static final RegistryObject<Block> LOG_PILE;
    public static final RegistryObject<Block> BURNING_LOG_PILE;

    static {
        BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TFCWoodworking.MOD_ID);
        WOODS = Helpers.mapOfKeys(Wood.class, (wood) ->
                Helpers.mapOfKeys(BlockType.class, (type) ->
                        registerBlockWithItem(type.getName(wood), type.sup)));
        LOG_PILE = registerBlock("log_pile", LogPileExBlock::new);
        BURNING_LOG_PILE = registerBlock("burning_log_pile", BurningLogPileExBlock::new);
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> blockSup) {
        return BLOCKS.register(name, blockSup);
    }

    private static <T extends Block> RegistryObject<T> registerBlockWithItem(String name, Supplier<T> blockSup) {
        RegistryObject<T> toReturn = registerBlock(name, blockSup);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}
