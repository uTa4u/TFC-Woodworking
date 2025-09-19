package su.uTa4u.tfcwoodwork.recipes.inworld;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.common.TFCTags;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public enum Tool implements StringRepresentable {
    AXE("axe", ItemTags.AXES),
    SAW("saw", TFCTags.Items.TOOLS_SAW);

    public static final Tool[] VALUES = values();
    public static final StreamCodec<ByteBuf, Tool> STREAM_CODEC =
            ByteBufCodecs.idMapper((id) -> Tool.VALUES[id], Tool::ordinal);

    private final String name;
    private final TagKey<Item> toolTagKey;
    private final Ingredient inputItem;
    private final String unlockedByName;

    Tool(String name, TagKey<Item> toolTagKey) {
        this.name = name;
        this.toolTagKey = toolTagKey;
        this.inputItem = Ingredient.of(this.toolTagKey);
        this.unlockedByName = "has_" + this.name;
    }

    @Override
    @NotNull
    public String getSerializedName() {
        return this.name;
    }

    public TagKey<Item> getToolTagKey() {
        return this.toolTagKey;
    }

    public Ingredient getInputItem() {
        return this.inputItem;
    }

    public String getUnlockedByName() {
        return this.unlockedByName;
    }
}
