package su.uTa4u.tfcwoodwork.recipes.inworld;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import su.uTa4u.tfcwoodwork.ModConfig;
import su.uTa4u.tfcwoodwork.TFCWoodworking;
import su.uTa4u.tfcwoodwork.blocks.AbstractDebarkedWood;
import su.uTa4u.tfcwoodwork.blocks.BlockType;
import su.uTa4u.tfcwoodwork.blocks.ModBlocks;
import su.uTa4u.tfcwoodwork.entities.AbstractWoodProjectile;
import su.uTa4u.tfcwoodwork.entities.LogHalfProjectile;
import su.uTa4u.tfcwoodwork.entities.LogQuarterProjectile;

import java.util.Optional;

public enum Action implements StringRepresentable {
    DROP_CARDINAL("drop_cardinal") {
        @Override
        public void accept(Level level, BlockPos pos, ItemStack itemStack, Direction playerDir) {
            spawnDropsPrecise(level, pos, 0.5, 0.5, -0.2, itemStack, 0, 0.05, -0.05);
            spawnDropsPrecise(level, pos, 0.5, 0.5, 1.2, itemStack, 0, 0.05, 0.05);
            spawnDropsPrecise(level, pos, -0.2, 0.5, 0.5, itemStack, -0.05, 0.05, 0);
            spawnDropsPrecise(level, pos, 1.2, 0.5, 0.5, itemStack, 0.05, 0.05, 0);
        }
    },
    DROP_CENTER("drop_center") {
        @Override
        public void accept(Level level, BlockPos pos, ItemStack itemStack, Direction playerDir) {
            spawnDropsPrecise(level, pos, 0.5, 0.5, 0.5, itemStack, 0.0, 0.0, 0.0);
        }
    },
    DROP_ABOVE("drop_above") {
        @Override
        public void accept(Level level, BlockPos pos, ItemStack itemStack, Direction playerDir) {
            spawnDropsPrecise(level, pos, 0.5, 1.05, 0.5, itemStack, 0.0, 0.0, 0.0);
        }
    },
    SHOOT_AS_BLOCK("shoot_as_block") {
        @Override
        public void accept(Level level, BlockPos pos, ItemStack itemStack, Direction playerDir) {
            BlockState chopped = level.getBlockState(pos);
            Wood wood = getWoodFromState(chopped);
            if (wood == null) return;
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
            if (ModConfig.LOG_PROJECTILE_VS_ITEM.get()) {
                Item item = itemStack.getItem();
                Block block = Block.byItem(item);
                if (block == Blocks.AIR) {
                    TFCWoodworking.LOGGER.error("Attempted to shoot non existent projectile, why?");
                    return;
                }
                BlockState state = block.defaultBlockState();
                if (item == ModBlocks.WOODS.get(wood).get(BlockType.DEBARKED_HALF).asItem()) {
                    projLeft = new LogHalfProjectile(pos, state, 0.5 + offsetX, offsetY, 0.5 + offsetZ, level, dir, true);
                    projRight = new LogHalfProjectile(pos, state, 0.5 - offsetX, offsetY, 0.5 - offsetZ, level, dir, false);
                } else if (item == ModBlocks.WOODS.get(wood).get(BlockType.DEBARKED_QUARTER).asItem()) {
                    projLeft = new LogQuarterProjectile(pos, state, 0.5 + offsetX, offsetY, 0.5 + offsetZ, level, dir, true);
                    projRight = new LogQuarterProjectile(pos, state, 0.5 - offsetX, offsetY, 0.5 - offsetZ, level, dir, false);
                } else {
                    TFCWoodworking.LOGGER.error("Attempted to shoot non-existent projectile, why?");
                    return;
                }
                ((AbstractWoodProjectile) projLeft).shoot(deltaX, deltaY, deltaZ, 0.3f, 0.0f);
                ((AbstractWoodProjectile) projRight).shoot(-deltaX, deltaY, -deltaZ, 0.3f, 0.0f);
            } else {
                projLeft = new ItemEntity(level, pos.getX() + 0.5 + offsetX, pos.getY() + offsetY, pos.getZ() + 0.5 + offsetZ, itemStack, deltaX / 4, deltaY, deltaZ / 4);
                projRight = new ItemEntity(level, pos.getX() + 0.5 - offsetX, pos.getY() + offsetY, pos.getZ() + 0.5 - offsetZ, itemStack, -deltaX / 4, deltaY, -deltaZ / 4);
            }
            level.addFreshEntity(projLeft);
            level.addFreshEntity(projRight);
        }
    };

    public static final Action[] VALUES = values();
    public static final StreamCodec<ByteBuf, Action> STREAM_CODEC =
            ByteBufCodecs.idMapper((id) -> Action.VALUES[id], Action::ordinal);

    private final String name;

    Action(String name) {
        this.name = name;
    }

    public abstract void accept(Level level, BlockPos pos, ItemStack itemStack, Direction playerDir);

    @Override
    @NotNull
    public String getSerializedName() {
        return this.name;
    }

    private static void spawnDropsPrecise(Level level, BlockPos pos, double offsetX, double offsetY, double offsetZ, ItemStack itemStack, double deltaX, double deltaY, double deltaZ) {
        level.addFreshEntity(new ItemEntity(level, pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ, itemStack, deltaX, deltaY, deltaZ));
    }

    private static Wood getWoodFromState(BlockState state) {
        for (var entry1 : TFCBlocks.WOODS.entrySet()) {
            for (var entry2 : entry1.getValue().entrySet()) {
                if (state.is(entry2.getValue().get())) {
                    return entry1.getKey();
                }
            }
        }

        for (var entry1 : ModBlocks.WOODS.entrySet()) {
            for (var entry2 : entry1.getValue().entrySet()) {
                if (state.is(entry2.getValue().get())) {
                    return entry1.getKey();
                }
            }
        }

        return null;
    }

}
