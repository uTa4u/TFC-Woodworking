package su.uTa4u.tfcwoodwork;

import net.dries007.tfc.common.blocks.wood.Wood;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;
import su.uTa4u.tfcwoodwork.entity.LogHalfProjectile;
import su.uTa4u.tfcwoodwork.entity.ModEntities;

import java.util.Map;
import java.util.Random;


public class util {
    private static final Random rng = new Random();

    public static <T extends Enum<T>> BlockState getStateToPlace(Map<Wood, Map<T, RegistryObject<Block>>> map, Wood wood, T blockTypes) {
        return map.get(wood).get(blockTypes).get().defaultBlockState();
    }

    public static <T extends Enum<T>> Item getItemToDrop(Map<Wood, Map<T, RegistryObject<Block>>> map, Wood wood, T blockTypes) {
        return getStateToPlace(map, wood, blockTypes).getBlock().asItem();
    }

    public static void spawnDrops(Level level, BlockPos pos, ItemStack itemstack) {
        level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.above().getY(), pos.getZ(), itemstack));
    }

    public static void shootChoppedWood(Level level, BlockPos pos, Direction dir) {
        Direction.Axis axis = dir.getAxis();
        double deltaX = 0;
        double deltaY = 0.25;
        double deltaZ = 0;
        double offsetX = 0;
        double offsetY = 0.25;
        double offsetZ = 0;
        if (axis == Direction.Axis.Z) {
            deltaX = 0.25;
            offsetX = 0.25;
        } else {
            deltaZ = 0.25;
            offsetZ = 0.25;
        }
        LogHalfProjectile proj1 = new LogHalfProjectile(pos, 0.5 + offsetX, offsetY, 0.5 + offsetZ, level, dir, true);
        LogHalfProjectile proj2 = new LogHalfProjectile(pos, 0.5 - offsetX, offsetY, 0.5 - offsetZ, level, dir, false);
        proj1.shoot(deltaX, deltaY, deltaZ, 0.2f, 0.0f);
        proj2.shoot(-deltaX, deltaY, -deltaZ, 0.2f, 0.0f);

        level.addFreshEntity(proj1);
        level.addFreshEntity(proj2);

    }

    public static void dropBiDirectional(Level level, BlockPos pos, Direction dir, Item item, int count) {
        Direction.Axis axis = dir.getAxis();
        if (axis == Direction.Axis.Z) {
            Direction left = dir.getCounterClockWise();
            BlockPos leftPos = pos.relative(left);
            double deltaX = 0.5;
            double deltaY = 0.5;
            double deltaZ = 0;
            ItemEntity drop1 = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(),
                    new ItemStack(item, count), deltaX, deltaY, deltaZ);
            ItemEntity drop2 = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(),
                    new ItemStack(item, count), -deltaX, deltaY, deltaZ);
            level.addFreshEntity(drop1);
            level.addFreshEntity(drop2);
        } else if (axis == Direction.Axis.X) {
        }
    }

    public static <T extends Enum<T>> Pair<Wood, T> getWoodWoodTypePair(Map<Wood, Map<T, RegistryObject<Block>>> map, BlockState state) {
        for (Map.Entry<Wood, Map<T, RegistryObject<Block>>> entry1 : map.entrySet()) {
            for (Map.Entry<T, RegistryObject<Block>> entry2 : entry1.getValue().entrySet()) {
                if (state.is(entry2.getValue().get())) {
                    return new Pair<>(entry1.getKey(), entry2.getKey());
                }
            }
        }
        return null;
    }

    public static void damageTool(Player player, ItemStack inHand, InteractionHand hand) {
        if (rng.nextBoolean()) {
            inHand.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
        }
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

        return useOnEventHandler.useTool(tool, level, player, pos, inHand, hand);
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
    //ALL actions should take durability
}