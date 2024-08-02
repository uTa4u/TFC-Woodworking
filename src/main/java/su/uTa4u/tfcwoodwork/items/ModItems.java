package su.uTa4u.tfcwoodwork.items;

import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.util.Helpers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import su.uTa4u.tfcwoodwork.TFCWoodworking;

import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS;

    public static final RegistryObject<Item> TESTAXE;
    public static final RegistryObject<Item> TESTSAW;
    public static final Map<Wood, RegistryObject<Item>> TREE_BARK;
    public static final Map<Wood, RegistryObject<Item>> TREE_BAST;
    public static final RegistryObject<Item> SAWDUST;


    static {
        ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TFCWoodworking.MOD_ID);
        TESTAXE = registerItem("testaxe", TestAxe::new);
        TESTSAW = registerItem("testsaw", TestSaw::new);
        TREE_BARK = Helpers.mapOfKeys(Wood.class, (wood) -> registerItemFamily("wood/bark/" + wood.name()));
        TREE_BAST = Helpers.mapOfKeys(Wood.class, (wood) -> registerItemFamily("wood/bast/" + wood.name()));
        SAWDUST = registerItem("wood/sawdust", () -> new Item(new Item.Properties()));
    }

    private static RegistryObject<Item> registerItem(String name, Supplier<Item> itemSup) {
        return ITEMS.register(name, itemSup);
    }

    private static RegistryObject<Item> registerItemFamily(String name) {
        return registerItem(name.toLowerCase(Locale.ROOT), () -> new Item(new Item.Properties()));
    }

    public static ResourceLocation getResourceLoc(String path) {
        return new ResourceLocation(TFCWoodworking.MOD_ID, path);
    }

    public static ItemStack getBark(Wood wood) {
        return new ItemStack(ForgeRegistries.ITEMS.getValue(getResourceLoc("wood/bark/" + wood.name().toLowerCase(Locale.ROOT))), 4);
    }

    public static ItemStack getBast(Wood wood) {
        return new ItemStack(ForgeRegistries.ITEMS.getValue(getResourceLoc("wood/bast/" + wood.name().toLowerCase(Locale.ROOT))), 4);
    }
}
