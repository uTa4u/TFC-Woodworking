package su.uTa4u.tfcwoodwork.recipes.inworld;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import su.uTa4u.tfcwoodwork.TFCWoodworking;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class BlockStatePredicate implements Predicate<BlockState> {
    /*
    TODO: this would be a nicer format, but it's complicated so not gonna bother rn
    "inputState": {
	    "block": "minecraft:diamond_block",
	    // this is an optional field
	    "acceptableStates": [
	    	{
	    		"facing": "north"
	    	},
	    	{
	    		"facing": "south",
	    		"someotherproperty": "value"
	    	}
	    ]
    }
     */
    public static final MapCodec<BlockStatePredicate> CODEC =
            RecordCodecBuilder.mapCodec((inst) -> inst.group(
                    BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(BlockStatePredicate::getBlock),
                    BlockState.CODEC.listOf().optionalFieldOf("acceptableStates", List.of()).forGetter(BlockStatePredicate::getAcceptableStates)
            ).apply(inst, BlockStatePredicate::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockStatePredicate> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.registry(Registries.BLOCK), BlockStatePredicate::getBlock,
                    ByteBufCodecs.collection(
                            ArrayList::new,
                            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY)
                    ), BlockStatePredicate::getAcceptableStates,
                    BlockStatePredicate::new
            );

    private final Block block;
    private final List<BlockState> acceptableStates;

    private BlockStatePredicate(Block block, List<BlockState> acceptableStates) {
        this.block = block;
        this.acceptableStates = acceptableStates;
    }

    public Block getBlock() {
        return this.block;
    }

    public List<BlockState> getAcceptableStates() {
        return this.acceptableStates;
    }

    @Override
    public boolean test(BlockState thatState) {
        if (!thatState.is(this.block)) return false;

        if (this.acceptableStates.isEmpty()) return true;

        return this.acceptableStates.contains(thatState);
    }

    public <T extends Comparable<T>> BlockStatePredicate where(Property<T> property, T value) {
        final var blockState = this.block.defaultBlockState();
        if (blockState.hasProperty(property)) {
            this.acceptableStates.add(blockState.setValue(property, value));
        } else {
            TFCWoodworking.LOGGER.error("Tried to create BlockState for BlockStatePredicate with invalid property: " + property);
        }
        return this;
    }

    public static BlockStatePredicate forBlock(Block block) {
        return new BlockStatePredicate(block, new ArrayList<>());
    }
}
