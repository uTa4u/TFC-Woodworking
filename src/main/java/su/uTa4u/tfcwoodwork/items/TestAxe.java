package su.uTa4u.tfcwoodwork.items;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;
import su.uTa4u.tfcwoodwork.util;

public class TestAxe extends Item {
    private static final Properties prop = new Item.Properties().durability(128).rarity(Rarity.EPIC);

    public TestAxe() {
        super(prop);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        return util.useTool(util.TOOL.AXE, context);
    }
}
