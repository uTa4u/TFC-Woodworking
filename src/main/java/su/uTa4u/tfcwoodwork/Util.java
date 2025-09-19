package su.uTa4u.tfcwoodwork;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import su.uTa4u.tfcwoodwork.blocks.AbstractDebarkedWood;
import su.uTa4u.tfcwoodwork.blocks.BlockType;
import su.uTa4u.tfcwoodwork.blocks.ModBlocks;
import su.uTa4u.tfcwoodwork.entities.AbstractWoodProjectile;
import su.uTa4u.tfcwoodwork.entities.LogHalfProjectile;
import su.uTa4u.tfcwoodwork.entities.LogQuarterProjectile;

import java.util.Map;
import java.util.Optional;

// TODO: delete this class
public class Util {
    public static <T extends Enum<T>> BlockState getStateToPlace(Map<Wood, Map<T, TFCBlocks.Id<Block>>> map, Wood wood, T blockType) {
        return map.get(wood).get(blockType).get().defaultBlockState();
    }

    public static <T extends Enum<T>> Item getItemToDrop(Map<Wood, Map<T, TFCBlocks.Id<Block>>> map, Wood wood, T blockType) {
        return getStateToPlace(map, wood, blockType).getBlock().asItem();
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

    public static void shootLogHalves(Level level, BlockPos pos, Wood wood, Direction playerDir) {
        shootChoppedWood(level, pos, wood, BlockType.DEBARKED_HALF, playerDir);
    }

    public static void shootLogQuarters(Level level, BlockPos pos, Wood wood, Direction playerDir) {
        shootChoppedWood(level, pos, wood, BlockType.DEBARKED_QUARTER, playerDir);
    }

    private static void shootChoppedWood(Level level, BlockPos pos, Wood wood, BlockType type, Direction playerDir) {
        BlockState chopped = level.getBlockState(pos);
        if (!(chopped.getBlock() instanceof AbstractDebarkedWood)) return;
        double deltaX = 0;
        double deltaY = 0.25;
        double deltaZ = 0;
        double offsetX = 0;
        double offsetY = 0.5;
        double offsetZ = 0;
        Optional<Direction> dirOpt = chopped.getOptionalValue(AbstractDebarkedWood.FACING);
        Direction dir = dirOpt.orElse(playerDir);
        switch (dir) {
            case NORTH, UP, DOWN -> { // handle UP and DOWN too to be safe
                deltaX = 0.75;
                offsetX = 0.1875;
            }
            case SOUTH -> {
                deltaX = -0.75;
                offsetX = -0.1875;
            }
            case WEST -> {
                deltaZ = 0.75;
                offsetZ = 0.1875;
            }
            case EAST -> {
                deltaZ = -0.75;
                offsetZ = -0.1875;
            }
        }
        Entity projLeft;
        Entity projRight;
        BlockState state = getStateToPlace(ModBlocks.WOODS, wood, type);
        if (ModConfig.LOG_PROJECTILE_VS_ITEM.get()) {
            if (type == BlockType.DEBARKED_HALF) {
                projLeft  = new LogHalfProjectile(pos, state, 0.5 + offsetX, offsetY, 0.5 + offsetZ, level, dir, true);
                projRight = new LogHalfProjectile(pos, state, 0.5 - offsetX, offsetY, 0.5 - offsetZ, level, dir, false);
            } else if (type == BlockType.DEBARKED_QUARTER) {
                projLeft  = new LogQuarterProjectile(pos, state, 0.5 + offsetX, offsetY, 0.5 + offsetZ, level, dir, true);
                projRight = new LogQuarterProjectile(pos, state, 0.5 - offsetX, offsetY, 0.5 - offsetZ, level, dir, false);
            } else {
                TFCWoodworking.LOGGER.error("Attempted to shoot non existent projectile, why?");
                return;
            }
            ((AbstractWoodProjectile) projLeft).shoot(deltaX, deltaY, deltaZ, 0.3f, 0.0f);
            ((AbstractWoodProjectile) projRight).shoot(-deltaX, deltaY, -deltaZ, 0.3f, 0.0f);
        } else {
            ItemStack itemStack = new ItemStack(state.getBlock().asItem());
            projLeft  = new ItemEntity(level, pos.getX() + 0.5 + offsetX, pos.getY() + offsetY, pos.getZ() + 0.5 + offsetZ, itemStack,  deltaX / 4, deltaY,  deltaZ / 4);
            projRight = new ItemEntity(level, pos.getX() + 0.5 - offsetX, pos.getY() + offsetY, pos.getZ() + 0.5 - offsetZ, itemStack, -deltaX / 4, deltaY, -deltaZ / 4);
        }
        level.addFreshEntity(projLeft);
        level.addFreshEntity(projRight);
    }

    public static <T extends Enum<T>> Optional<Pair<Wood, T>> getWoodWoodTypePair(Map<Wood, Map<T, TFCBlocks.Id<Block>>> map, BlockState state) {
        for (var entry1 : map.entrySet()) {
            for (var entry2 : entry1.getValue().entrySet()) {
                if (state.is(entry2.getValue().get())) {
                    return Optional.of(new Pair<>(entry1.getKey(), entry2.getKey()));
                }
            }
        }
        return Optional.empty();
    }

    // TODO: Just use Mojang's pair
    public record Pair<K, V>(K key, V value) {
    }

    // https://ru.wikipedia.org/wiki/Лесоматериалы
    // https://ru.wikipedia.org/wiki/Ствол_(ботаника)
    // "Луб" == "Bast"
    // https://en.wikipedia.org/wiki/Cambium
}