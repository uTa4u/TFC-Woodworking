package su.uTa4u.tfcwoodwork;

import com.mojang.logging.LogUtils;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.BlockItemPlacement;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.InteractionManager;
import net.dries007.tfc.util.Metal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import su.uTa4u.tfcwoodwork.blockentities.ModBlockEntities;
import su.uTa4u.tfcwoodwork.blocks.BlockType;
import su.uTa4u.tfcwoodwork.blocks.ModBlocks;
import su.uTa4u.tfcwoodwork.entities.AbstractWoodProjectile;
import su.uTa4u.tfcwoodwork.entities.LogHalfProjectile;
import su.uTa4u.tfcwoodwork.entities.LogQuarterProjectile;

import java.util.Map;
import java.util.Optional;
import java.util.Random;


public class util {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static <T extends Enum<T>> BlockState getStateToPlace(Map<Wood, Map<T, RegistryObject<Block>>> map, Wood wood, T blockTypes) {
        return map.get(wood).get(blockTypes).get().defaultBlockState();
    }

    public static <T extends Enum<T>> Item getItemToDrop(Map<Wood, Map<T, RegistryObject<Block>>> map, Wood wood, T blockTypes) {
        return getStateToPlace(map, wood, blockTypes).getBlock().asItem();
    }

    public static void spawnDropsPrecise(Level level, BlockPos pos, Vec3 offset, ItemStack itemStack) {
        spawnDropsPrecise(level, pos, offset.x, offset.y, offset.z, itemStack);
    }

    public static void spawnDrops(Level level, BlockPos pos, ItemStack itemStack) {
        spawnDropsPrecise(level, pos, 0.5, 0.5, 0.5, itemStack);
    }

    public static void spawnDropsPrecise(Level level, BlockPos pos, double offsetX, double offsetY, double offsetZ, ItemStack itemStack) {
        spawnDropsPrecise(level, pos, offsetX, offsetY, offsetZ, itemStack, 0, 0, 0);
    }

    public static void spawnDropsAbove(Level level, BlockPos pos, ItemStack itemStack) {
        spawnDropsPrecise(level, pos, 0.5, 1.05, 0.5, itemStack);
    }

    public static void spawnDropsPrecise(Level level, BlockPos pos, double offsetX, double offsetY, double offsetZ, ItemStack itemStack, double deltaX, double deltaY, double deltaZ) {
        if (itemStack.getCount() < 1) return;
        level.addFreshEntity(new ItemEntity(level, pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ, itemStack, deltaX, deltaY, deltaZ));
    }

    public static void spawnDropsCardinal(Level level, BlockPos pos, ItemStack itemStack) {
        spawnDropsPrecise(level, pos, 0.5, 0.5, -0.2, itemStack, 0, 0.05, -0.05);
        spawnDropsPrecise(level, pos, 0.5, 0.5,  1.2, itemStack, 0, 0.05, 0.05);
        spawnDropsPrecise(level, pos, -0.2, 0.5, 0.5, itemStack, -0.05, 0.05, 0);
        spawnDropsPrecise(level, pos, 1.2, 0.5, 0.5, itemStack, 0.05, 0.05, 0);
    }

    public static void shootLogHalves(Level level, BlockPos pos, Wood wood, Direction dir) {
        shootChoppedWood(level, pos, wood, BlockType.DEBARKED_HALF, dir);
    }

    public static void shootLogQuarters(Level level, BlockPos pos, Wood wood, Direction dir) {
        shootChoppedWood(level, pos, wood, BlockType.DEBARKED_QUARTER, dir);
    }

    private static void shootChoppedWood(Level level, BlockPos pos, Wood wood, BlockType type, Direction dir) {
        Direction.Axis axis = dir.getAxis();
        double deltaX = 0;
        double deltaY = 0.25;
        double deltaZ = 0;
        double offsetX = 0;
        double offsetY = 0.5;
        double offsetZ = 0;
        if (axis == Direction.Axis.Z) {
            deltaX = 0.75;
            offsetX = 0.1875;
        } else {
            deltaZ = 0.75;
            offsetZ = 0.1875;
        }
        AbstractWoodProjectile projLeft;
        AbstractWoodProjectile projRight;
        BlockState state = getStateToPlace(ModBlocks.WOODS, wood, type);
        if (type == BlockType.DEBARKED_HALF) {
            projLeft = new LogHalfProjectile(pos, state, 0.5 + offsetX, offsetY, 0.5 + offsetZ, level, dir, true);
            projRight = new LogHalfProjectile(pos, state, 0.5 - offsetX, offsetY, 0.5 - offsetZ, level, dir, false);
        } else if (type == BlockType.DEBARKED_QUARTER) {
            projLeft = new LogQuarterProjectile(pos, state, 0.5 + offsetX, offsetY, 0.5 + offsetZ, level, dir, true);
            projRight = new LogQuarterProjectile(pos, state, 0.5 - offsetX, offsetY, 0.5 - offsetZ, level, dir, false);
        } else {
            LOGGER.error("Attempted to shoot non existent projectile, why?");
            return;
        }
        projLeft.shoot(deltaX, deltaY, deltaZ, 0.3f, 0.0f);
        projRight.shoot(-deltaX, deltaY, -deltaZ, 0.3f, 0.0f);

        level.addFreshEntity(projLeft);
        level.addFreshEntity(projRight);

    }

    public static <T extends Enum<T>> Optional<Pair<Wood, T>> getWoodWoodTypePair(Map<Wood, Map<T, RegistryObject<Block>>> map, BlockState state) {
        for (Map.Entry<Wood, Map<T, RegistryObject<Block>>> entry1 : map.entrySet()) {
            for (Map.Entry<T, RegistryObject<Block>> entry2 : entry1.getValue().entrySet()) {
                if (state.is(entry2.getValue().get())) {
                    return Optional.of(new Pair<>(entry1.getKey(), entry2.getKey()));
                }
            }
        }
        return Optional.empty();
    }

    public static InteractionResult useTool(TOOL tool, UseOnContext context) {
        if (context.getLevel().isClientSide()) return InteractionResult.PASS;
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;
        BlockPos pos = context.getClickedPos();
        Level level = player.level();
        if (!level.isLoaded(pos)) return InteractionResult.PASS;
        InteractionHand hand = context.getHand();
        ItemStack inHand = context.getItemInHand();

        return useOnEventHandler.useTool(tool, level, player, pos);
    }

    public static void registerLogPileInteraction() {
        LOGGER.info("Registering TFCWW Log Pile Interaction");
        BlockItemPlacement logPilePlacement = new BlockItemPlacement(() -> net.minecraft.world.item.Items.AIR, ModBlocks.LOG_PILE);
        InteractionManager.register(Ingredient.of(TFCTags.Items.LOG_PILE_LOGS), false, (stack, context) -> {
            Player player = context.getPlayer();
            if (player != null && player.mayBuild() && player.isShiftKeyDown()) {
                Level level = context.getLevel();
                Direction direction = context.getClickedFace();
                BlockPos posClicked = context.getClickedPos();
                BlockState stateClicked = level.getBlockState(posClicked);
                BlockPos relativePos = posClicked.relative(direction);
                if (Helpers.isBlock(stateClicked, ModBlocks.LOG_PILE.get())) {
                    return level.getBlockEntity(posClicked, ModBlockEntities.LOG_PILE.get()).flatMap((entity) -> entity.getCapability(Capabilities.ITEM).map((t) -> t)).map((cap) -> {
                        ItemStack insertStack = stack.copy();
                        insertStack = Helpers.insertAllSlots(cap, insertStack);
                        if (insertStack.getCount() < stack.getCount()) {
                            if (!level.isClientSide()) {
                                Helpers.playPlaceSound(level, relativePos, SoundType.WOOD);
                                stack.setCount(insertStack.getCount());
                            }

                            return InteractionResult.SUCCESS;
                        } else {
                            InteractionResult result = logPilePlacement.onItemUse(stack, context);
                            if (result.consumesAction()) {
                                Helpers.insertOne(level, relativePos, ModBlockEntities.LOG_PILE.get(), insertStack);
                            }

                            return result;
                        }
                    }).orElse(InteractionResult.PASS);
                }

                if (level.getBlockState(relativePos.below()).isFaceSturdy(level, relativePos.below(), Direction.UP)) {
                    ItemStack stackBefore = stack.copy();
                    BlockPos actualPlacedPos = (new BlockPlaceContext(context)).getClickedPos();
                    InteractionResult result = logPilePlacement.onItemUse(stack, context);
                    if (result.consumesAction()) {
                        Helpers.insertOne(level, actualPlacedPos, ModBlockEntities.LOG_PILE.get(), stackBefore);
                    }

                    return result;
                }
            }

            return InteractionResult.PASS;
        });
    }

    public static ResourceLocation identifier(String name) {
        return new ResourceLocation(TFCWoodworking.MOD_ID, name);
    }

    public enum TOOL {
        AXE,
        SAW
    }

    public record Pair<K, V>(K key, V value) {
    }

    //https://ru.wikipedia.org/wiki/Лесоматериалы
    //https://ru.wikipedia.org/wiki/Ствол_(ботаника)
    //"Луб" == "Bast"
    // https://en.wikipedia.org/wiki/Cambium
}