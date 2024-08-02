package su.uTa4u.tfcwoodwork.blocks;

import net.dries007.tfc.util.registry.RegistryWood;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public enum BlockType {
    DEBARKED_LOG("debarked_log", true, DebarkedLog::new),
    DEBARKED_QUARTER("debarked_quarter", true, DebarkedQuarter::new),
    DEBARKED_HALF("debarked_half", true, DebarkedHalf::new);

    public final String name;
    public final Boolean toItem;
    public final Supplier<Block> sup;

    BlockType(String name, Boolean toItem, Supplier<Block> sup) {
        this.name = name;
        this.toItem = toItem;
        this.sup = sup;
    }


    public String getName(RegistryWood wood) {
        return this.name + "/" + wood.getSerializedName();
    }

}
