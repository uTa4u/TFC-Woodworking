package su.uTa4u.tfcwoodwork.recipes.inworld;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import su.uTa4u.tfcwoodwork.recipes.ModRecipeSerializers;
import su.uTa4u.tfcwoodwork.recipes.ModRecipeTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record InWorldRecipe(
        @NotNull BlockState inputState,
        @NotNull Tool tool,
        @NotNull BlockState resultState,
        @NotNull Optional<SoundInstance> soundInstance,
        @NotNull List<Pair<ItemStack, Action>> resultItems
) implements Recipe<InWorldRecipeInput> {
    public static final String NAME = "in_world";
    private static final StreamCodec<RegistryFriendlyByteBuf, Pair<ItemStack, Action>> RESULT_ITEMS_STREAM_CODEC = new StreamCodec<>() {
        @Override
        @NotNull
        public Pair<ItemStack, Action> decode(@NotNull RegistryFriendlyByteBuf input) {
            return Pair.of(ItemStack.STREAM_CODEC.decode(input), Action.STREAM_CODEC.decode(input));
        }

        @Override
        public void encode(@NotNull RegistryFriendlyByteBuf output, @NotNull Pair<ItemStack, Action> pair) {
            ItemStack.STREAM_CODEC.encode(output, pair.getFirst());
            Action.STREAM_CODEC.encode(output, pair.getSecond());
        }
    };

    @Override
    @NotNull
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(this.tool.getInputItem());
        return list;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public boolean matches(@NotNull InWorldRecipeInput input, @NotNull Level level) {
        return this.inputState == input.state() && this.tool.getInputItem().test(input.stack());
    }

    @Override
    @NotNull
    public ItemStack getResultItem(@NotNull HolderLookup.Provider provider) {
        return !this.resultItems.isEmpty() ? this.resultItems.getFirst().getFirst() : ItemStack.EMPTY;
    }

    @Override
    @NotNull
    public ItemStack assemble(@NotNull InWorldRecipeInput input, @NotNull HolderLookup.Provider provider) {
        return this.getResultItem(provider).copy();
    }

    @Override
    @NotNull
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.IN_WORLD.get();
    }

    @Override
    @NotNull
    public RecipeType<?> getType() {
        return ModRecipeTypes.IN_WORLD.get();
    }

    public static final class Serializer implements RecipeSerializer<InWorldRecipe> {
        public static final MapCodec<InWorldRecipe> CODEC =
                RecordCodecBuilder.mapCodec((inst) -> inst.group(
                        BlockState.CODEC.fieldOf("inputState").forGetter(InWorldRecipe::inputState),
                        StringRepresentable.fromEnum(Tool::values).fieldOf("requiredTool").forGetter(InWorldRecipe::tool),
                        BlockState.CODEC.fieldOf("resultState").forGetter(InWorldRecipe::resultState),
                        SoundInstance.CODEC.optionalFieldOf("sound").forGetter(InWorldRecipe::soundInstance),
                        Codec.pair(
                                ItemStack.CODEC.fieldOf("itemStack").codec(),
                                StringRepresentable.fromEnum(Action::values).fieldOf("action").codec()
                        ).listOf().optionalFieldOf("resultItems", List.of()).forGetter(InWorldRecipe::resultItems)
                ).apply(inst, InWorldRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, InWorldRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), InWorldRecipe::inputState,
                        Tool.STREAM_CODEC, InWorldRecipe::tool,
                        ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), InWorldRecipe::resultState,
                        SoundInstance.STREAM_CODEC.apply(ByteBufCodecs::optional), InWorldRecipe::soundInstance,
                        ByteBufCodecs.collection(
                                ArrayList::new,
                                RESULT_ITEMS_STREAM_CODEC
                        ), InWorldRecipe::resultItems,
                        InWorldRecipe::new
                );

        @Override
        @NotNull
        public MapCodec<InWorldRecipe> codec() {
            return CODEC;
        }

        @Override
        @NotNull
        public StreamCodec<RegistryFriendlyByteBuf, InWorldRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
