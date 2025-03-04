package su.uTa4u.tfcwoodwork;

import net.dries007.tfc.util.Helpers;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {


    public static class Items {
        // Anything that goes into 3rd row of TFCWoodworking's log pile
        public static final TagKey<Item> LOGS_LOG = TagKey.create(Registries.ITEM, util.identifier("logs_log"));
        // Anything that goes into 2nd row of TFCWoodworking's log pile
        public static final TagKey<Item> LOGS_HALF = TagKey.create(Registries.ITEM, util.identifier("logs_half"));
        // Anything that goes into 1st row of TFCWoodworking's log pile
        public static final TagKey<Item> LOGS_QUARTER = TagKey.create(Registries.ITEM, util.identifier("logs_quarter"));

        //TFC doesn't have TagKeys for these tags, so we create them ourselves
        public static final TagKey<Item> TFC_AXES = TagKey.create(Registries.ITEM, Helpers.identifier("axes"));
        public static final TagKey<Item> TFC_SAWS = TagKey.create(Registries.ITEM, Helpers.identifier("saws"));

    }

    public static class Blocks {
        public static final TagKey<Block> LOGS = TagKey.create(Registries.BLOCK, util.identifier("logs"));
    }
}
