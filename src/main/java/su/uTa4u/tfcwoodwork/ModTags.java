package su.uTa4u.tfcwoodwork;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {

    public static class Items {
        public static final TagKey<Item> LOGS_HALF = TagKey.create(Registries.ITEM, new ResourceLocation(TFCWoodworking.MOD_ID, "logs_half"));
        public static final TagKey<Item> LOGS_QUARTER = TagKey.create(Registries.ITEM, new ResourceLocation(TFCWoodworking.MOD_ID, "logs_quarter"));
    }
}
