package su.uTa4u.tfcwoodwork.datagen.recipes;

import com.mojang.datafixers.util.Pair;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.items.TFCItems;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.uTa4u.tfcwoodwork.TFCWoodworking;
import su.uTa4u.tfcwoodwork.blocks.BlockType;
import su.uTa4u.tfcwoodwork.blocks.ModBlocks;
import su.uTa4u.tfcwoodwork.items.ModItems;
import su.uTa4u.tfcwoodwork.recipes.inworld.*;
import su.uTa4u.tfcwoodwork.sounds.ModSounds;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static net.dries007.tfc.common.blocks.wood.Wood.BlockType.*;
import static su.uTa4u.tfcwoodwork.blocks.BlockType.*;

public final class InWorldRecipeProvider extends RecipeProvider {
    public InWorldRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        for (var wood : Wood.VALUES) {
            buildAxeRecipes(output, wood);
            buildSawRecipes(output, wood);
        }
    }

    private static void buildAxeRecipes(@NotNull RecipeOutput output, Wood wood) {
        final var bark = ModItems.getBark(wood);
        final var bast = ModItems.getBast(wood);
        buildRecipe(output, Tool.AXE,
                "strip_log/" + wood.getSerializedName(),
                BlockStatePredicate
                        .forBlock(getBlock(wood, LOG))
                        .where(BlockStateProperties.AXIS, Direction.Axis.Y),
                getState(wood, STRIPPED_LOG),
                new SoundInstance(SoundEvents.AXE_STRIP, 1.0f, 1.0f),
                List.of(
                        Pair.of(
                                new ItemStack(bark, 1),
                                Action.DROP_CARDINAL
                        )
                )
        );
        buildRecipe(output, Tool.AXE,
                "strip_wood/" + wood.getSerializedName(),
                BlockStatePredicate.forBlock(getBlock(wood, WOOD)),
                getState(wood, STRIPPED_WOOD),
                new SoundInstance(SoundEvents.AXE_STRIP, 1.0f, 1.0f),
                List.of(
                        Pair.of(
                                new ItemStack(bark, 1),
                                Action.DROP_CARDINAL
                        ),
                        Pair.of(
                                new ItemStack(bark, 2),
                                Action.DROP_ABOVE
                        )
                )
        );
        buildRecipe(output, Tool.AXE,
                "debark_log/" + wood.getSerializedName(),
                BlockStatePredicate
                        .forBlock(getBlock(wood, STRIPPED_LOG))
                        .where(BlockStateProperties.AXIS, Direction.Axis.Y),
                getState(wood, DEBARKED_LOG),
                new SoundInstance(ModSounds.LOG_CHOP.get(), 0.6f, 0.8f),
                List.of(
                        Pair.of(
                                new ItemStack(bast, 1),
                                Action.DROP_CARDINAL
                        )
                )
        );
        buildRecipe(output, Tool.AXE,
                "debark_wood/" + wood.getSerializedName(),
                BlockStatePredicate.forBlock(getBlock(wood, STRIPPED_WOOD)),
                getState(wood, DEBARKED_LOG),
                new SoundInstance(ModSounds.LOG_CHOP.get(), 0.6f, 0.8f),
                List.of(
                        Pair.of(
                                new ItemStack(bast, 1),
                                Action.DROP_CARDINAL
                        ),
                        Pair.of(
                                new ItemStack(bast, 2),
                                Action.DROP_ABOVE
                        )
                )
        );
        buildRecipe(output, Tool.AXE,
                "chop_log/" + wood.getSerializedName(),
                BlockStatePredicate.forBlock(getBlock(wood, DEBARKED_LOG)),
                Blocks.AIR.defaultBlockState(),
                new SoundInstance(ModSounds.LOG_CHOP.get(), 0.6f, 1.0f),
                List.of(
                        Pair.of(
                                new ItemStack(getItem(wood, DEBARKED_HALF), 1),
                                Action.SHOOT_AS_BLOCK
                        )
                )
        );
        buildRecipe(output, Tool.AXE,
                "chop_log_half/" + wood.getSerializedName(),
                BlockStatePredicate.forBlock(getBlock(wood, DEBARKED_HALF)),
                Blocks.AIR.defaultBlockState(),
                new SoundInstance(ModSounds.LOG_CHOP.get(), 0.6f, 1.0f),
                List.of(
                        Pair.of(
                                new ItemStack(getItem(wood, DEBARKED_QUARTER), 1),
                                Action.SHOOT_AS_BLOCK
                        )
                )
        );

    }

    private static void buildSawRecipes(@NotNull RecipeOutput output, Wood wood) {
        buildRecipe(output, Tool.SAW,
                "log_fence/" + wood.getSerializedName(),
                BlockStatePredicate
                        .forBlock(getBlock(wood, LOG))
                        .where(BlockStateProperties.AXIS, Direction.Axis.Y),
                Blocks.AIR.defaultBlockState(),
                new SoundInstance(ModSounds.LOG_SAWED.get(), 0.6f, 1.0f),
                List.of(
                        Pair.of(
                                new ItemStack(getItem(wood, LOG_FENCE), 4),
                                Action.DROP_CENTER
                        ),
                        Pair.of(
                                new ItemStack(ModItems.SAWDUST.get(), 1),
                                Action.DROP_CENTER
                        )
                )
        );
        buildRecipe(output, Tool.SAW,
                "fence/" + wood.getSerializedName(),
                BlockStatePredicate.forBlock(getBlock(wood, PLANKS)),
                Blocks.AIR.defaultBlockState(),
                new SoundInstance(ModSounds.LOG_SAWED.get(), 0.6f, 1.0f),
                List.of(
                        Pair.of(
                                new ItemStack(getItem(wood, FENCE), 4),
                                Action.DROP_CENTER
                        ),
                        Pair.of(
                                new ItemStack(getItem(wood, STAIRS), 1),
                                Action.DROP_CENTER
                        ),
                        Pair.of(
                                new ItemStack(ModItems.SAWDUST.get(), 1),
                                Action.DROP_CENTER
                        )
                )
        );
        buildRecipe(output, Tool.SAW,
                "trapdoor/" + wood.getSerializedName(),
                BlockStatePredicate.forBlock(getBlock(wood, SLAB)),
                Blocks.AIR.defaultBlockState(),
                new SoundInstance(ModSounds.LOG_SAWED.get(), 0.6f, 1.0f),
                List.of(
                        Pair.of(
                                new ItemStack(getItem(wood, TRAPDOOR), 2),
                                Action.DROP_CENTER
                        ),
                        Pair.of(
                                new ItemStack(ModItems.SAWDUST.get(), 1),
                                Action.DROP_CENTER
                        )
                )
        );
        buildRecipe(output, Tool.SAW,
                "support/" + wood.getSerializedName(),
                BlockStatePredicate.forBlock(getBlock(wood, DEBARKED_HALF)),
                Blocks.AIR.defaultBlockState(),
                new SoundInstance(ModSounds.LOG_SAWED.get(), 0.6f, 1.0f),
                List.of(
                        Pair.of(
                                new ItemStack(TFCItems.SUPPORTS.get(wood), 4),
                                Action.DROP_CENTER
                        ),
                        Pair.of(
                                new ItemStack(ModItems.SAWDUST.get(), 1),
                                Action.DROP_CENTER
                        )
                )
        );
        buildRecipe(output, Tool.SAW,
                "lumber/" + wood.getSerializedName(),
                BlockStatePredicate.forBlock(getBlock(wood, DEBARKED_QUARTER)),
                Blocks.AIR.defaultBlockState(),
                new SoundInstance(ModSounds.LOG_SAWED.get(), 0.6f, 1.0f),
                List.of(
                        Pair.of(
                                new ItemStack(TFCItems.LUMBER.get(wood), 2),
                                Action.DROP_CENTER
                        ),
                        Pair.of(
                                new ItemStack(ModItems.SAWDUST.get(), 1),
                                Action.DROP_CENTER
                        )
                )
        );
    }

    private static void buildRecipe(
            @NotNull RecipeOutput output,
            @NotNull Tool tool,
            @NotNull String recipeName,
            @NotNull BlockStatePredicate inputState,
            @NotNull BlockState resultState,
            @Nullable SoundInstance sound,
            @NotNull List<Pair<ItemStack, Action>> resultItems
    ) {
        final var id = TFCWoodworking.getResource(InWorldRecipe.NAME + "/" + tool.getSerializedName() + "/" + recipeName);
        new InWorldRecipeBuilder(
                inputState,
                tool,
                resultState,
                Optional.ofNullable(sound),
                resultItems
        ).unlockedBy(tool.getUnlockedByName(), has(tool.getToolTagKey())).save(output, id);
    }

    private static Block getBlock(Wood wood, Wood.BlockType type) {
        return TFCBlocks.WOODS.get(wood).get(type).get();
    }

    private static Block getBlock(Wood wood, BlockType type) {
        return ModBlocks.WOODS.get(wood).get(type).get();
    }

    private static BlockState getState(Wood wood, Wood.BlockType type) {
        return getBlock(wood, type).defaultBlockState();
    }

    private static BlockState getState(Wood wood, BlockType type) {
        return getBlock(wood, type).defaultBlockState();
    }

    private static Item getItem(Wood wood, Wood.BlockType type) {
        return getState(wood, type).getBlock().asItem();
    }

    private static Item getItem(Wood wood, BlockType type) {
        return getState(wood, type).getBlock().asItem();
    }
}
