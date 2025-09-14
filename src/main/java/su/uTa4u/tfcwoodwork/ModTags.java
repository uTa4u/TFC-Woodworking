package su.uTa4u.tfcwoodwork;

import net.dries007.tfc.util.Helpers;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class ModTags {
    private ModTags() {
    }

    public static final class Items {
        public static final TagKey<Item> LOGS_LOG = TagKey.create(Registries.ITEM, TFCWoodworking.getResource("logs_log"));
        public static final TagKey<Item> LOGS_HALF = TagKey.create(Registries.ITEM, TFCWoodworking.getResource("logs_half"));
        public static final TagKey<Item> LOGS_QUARTER = TagKey.create(Registries.ITEM, TFCWoodworking.getResource("logs_quarter"));

        public static final TagKey<Item> BARK = TagKey.create(Registries.ITEM, TFCWoodworking.getResource("bark"));
        public static final TagKey<Item> BAST = TagKey.create(Registries.ITEM, TFCWoodworking.getResource("bast"));
    }

    public static final class Blocks {
        public static final TagKey<Block> LOGS_LOG = TagKey.create(Registries.BLOCK, TFCWoodworking.getResource("logs_log"));
        public static final TagKey<Block> LOGS_HALF = TagKey.create(Registries.BLOCK, TFCWoodworking.getResource("logs_half"));
        public static final TagKey<Block> LOGS_QUARTER = TagKey.create(Registries.BLOCK, TFCWoodworking.getResource("logs_quarter"));

        public static final TagKey<Block> LOGS = TagKey.create(Registries.BLOCK, TFCWoodworking.getResource("logs"));
    }
}
