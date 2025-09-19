package su.uTa4u.tfcwoodwork.items;

import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import su.uTa4u.tfcwoodwork.TFCWoodworking;

import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public final class ModItems {
    private ModItems() {
    }

    public static final DeferredRegister<Item> ITEMS;

    public static final Map<Wood, DeferredHolder<Item, Item>> TREE_BARK;
    public static final Map<Wood, DeferredHolder<Item, Item>> TREE_BAST;
    public static final DeferredHolder<Item, Item> SAWDUST;
    public static final DeferredHolder<Item, Item> SAWDUST_SOUP;

    static {
        ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, TFCWoodworking.MOD_ID);
        TREE_BARK = Helpers.mapOf(Wood.class, wood -> registerItem("bark/" + wood.name(), () -> new Item(new Item.Properties())));
        TREE_BAST = Helpers.mapOf(Wood.class, wood -> registerItem("bast/" + wood.name(), () -> new Item(new Item.Properties())));
        SAWDUST = registerItem("sawdust", () -> new Item(new Item.Properties()));
        SAWDUST_SOUP = registerItem("sawdust_soup", () -> new SawdustSoupBowl((new Item.Properties().food(new FoodProperties.Builder().nutrition(0).saturationModifier(1).build()))));
    }

    private static DeferredHolder<Item, Item> registerItem(String name, Supplier<Item> itemSup) {
        return ITEMS.register(name.toLowerCase(Locale.ROOT), itemSup);
    }

    public static Item getBark(Wood wood) {
        return BuiltInRegistries.ITEM.get(TFCWoodworking.getResource("bark/" + wood.name().toLowerCase(Locale.ROOT)));
    }

    public static Item getBast(Wood wood) {
        return BuiltInRegistries.ITEM.get(TFCWoodworking.getResource("bast/" + wood.name().toLowerCase(Locale.ROOT)));
    }
}
