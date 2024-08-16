package su.uTa4u.tfcwoodwork;

import com.mojang.logging.LogUtils;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.RockCategory;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import su.uTa4u.tfcwoodwork.blocks.BlockType;
import su.uTa4u.tfcwoodwork.blocks.ModBlocks;
import su.uTa4u.tfcwoodwork.items.ModItems;
import su.uTa4u.tfcwoodwork.sounds.ModSounds;

import java.util.Map;
import java.util.Optional;

public class useOnEventHandler {
    private static final int TOOL_COOLDOWN = 10;

    @SubscribeEvent
    public static void useOn(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();
        BlockState state = level.getBlockState(pos);
        ItemStack inMainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (player.getCooldowns().isOnCooldown(inMainHand.getItem())) {
            event.setUseItem(Event.Result.DENY);
            return;
        }
        if (!isValidBlock(state)) return;

        InteractionHand hand = event.getHand();
        if (isValidAxe(inMainHand)) {
            if (checkFourDirections(level, pos)) {
                InteractionResult result = useTool(util.TOOL.AXE, level, player, pos);
                if (result == InteractionResult.sidedSuccess(level.isClientSide)) {
                    player.swing(InteractionHand.MAIN_HAND, true);
                    util.damageTool(player, inMainHand, InteractionHand.MAIN_HAND);
                    setCooldownForAxes(player);
                    event.setCanceled(true);
                }
            } else {
                if (hand == InteractionHand.MAIN_HAND) event.setUseItem(Event.Result.DENY);
            }
        } else if (isValidSaw(inMainHand)) {
            if (checkFourDirections(level, pos)) {
                InteractionResult result = useTool(util.TOOL.SAW, level, player, pos);
                if (result == InteractionResult.sidedSuccess(level.isClientSide)) {
                    player.swing(InteractionHand.MAIN_HAND, true);
                    util.damageTool(player, inMainHand, InteractionHand.MAIN_HAND);
                    setCooldownForSaws(player);
                    event.setCanceled(true);
                }
            } else {
                if (hand == InteractionHand.MAIN_HAND) event.setUseItem(Event.Result.DENY);
            }
        }
    }

    private static void setCooldownForAxes(Player player) {
        ItemCooldowns cds = player.getCooldowns();
        Helpers.allItems(ModTags.Items.TFC_AXES).forEach((axe) -> cds.addCooldown(axe.asItem(), TOOL_COOLDOWN));
    }

    private static void setCooldownForSaws(Player player) {
        ItemCooldowns cds = player.getCooldowns();
        Helpers.allItems(ModTags.Items.TFC_SAWS).forEach((saw) -> cds.addCooldown(saw.asItem(), TOOL_COOLDOWN));
    }

    private static boolean checkFourDirections(Level level, BlockPos pos) {
        for (int i = 0; i < 4; ++i) {
            Direction dir = Direction.from2DDataValue(i);
            BlockPos nbour = pos.relative(dir);
            if (level.getBlockState(nbour).isFaceSturdy(level, nbour, dir.getOpposite(), SupportType.FULL)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidSaw(ItemStack inHand) {
        return Helpers.isItem(inHand, ModTags.Items.TFC_SAWS);
    }

    private static boolean isValidAxe(ItemStack inHand) {
        return Helpers.isItem(inHand, ModTags.Items.TFC_AXES);
    }

    private static boolean isValidBlock(BlockState state) {
        return Helpers.isBlock(state, BlockTags.LOGS) || Helpers.isBlock(state, ModTags.Blocks.LOGS);
    }

    //TODO: more in-world recipes for wooden things

    //TODO: if alive tree was debarked it should die after some time and fall, more debarked blocks = faster death
    //TODO: make bark/bast pileable
    public static InteractionResult useTool(util.TOOL tool, Level level, Player player, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Direction dir = player.getDirection();
        return util.getWoodWoodTypePair(TFCBlocks.WOODS, state).map((pair1) -> {
            BlockState newState;
            Item toDrop;
            switch (pair1.value()) {
                case LOG -> {
                    newState = util.getStateToPlace(TFCBlocks.WOODS, pair1.key(), Wood.BlockType.STRIPPED_LOG);
                    toDrop = ModItems.getBark(pair1.key());
                }
                case WOOD -> {
                    newState = util.getStateToPlace(TFCBlocks.WOODS, pair1.key(), Wood.BlockType.STRIPPED_WOOD);
                    toDrop = ModItems.getBark(pair1.key());
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(toDrop, 2), 0, 0.05, 0));
                }
                case STRIPPED_LOG -> {
                    newState = util.getStateToPlace(ModBlocks.WOODS, pair1.key(), BlockType.DEBARKED_LOG);
                    toDrop = ModItems.getBast(pair1.key());
                }
                case STRIPPED_WOOD -> {
                    newState = util.getStateToPlace(ModBlocks.WOODS, pair1.key(), BlockType.DEBARKED_LOG);
                    toDrop = ModItems.getBast(pair1.key());
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(toDrop, 2), 0, 0.05, 0));
                }
                default -> {
                    return InteractionResult.PASS;
                }
            }
            util.spawnDropsCardinal(level, pos, toDrop, 1);
            level.setBlockAndUpdate(pos, newState);
            level.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0f, 1.0f);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }).orElseGet(() -> util.getWoodWoodTypePair(ModBlocks.WOODS, state).map((pair2) -> {
            if (tool == util.TOOL.AXE && level.getBlockState(pos.above()) == Blocks.AIR.defaultBlockState()) {
                switch (pair2.value()) {
                    case DEBARKED_LOG -> util.shootLogHalves(level, pos, pair2.key(), dir);
                    case DEBARKED_HALF -> util.shootLogQuarters(level, pos, pair2.key(), dir);
                    default -> {
                        return InteractionResult.PASS;
                    }
                }
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                level.playSound(player, pos, ModSounds.LOG_CHOP.get(), SoundSource.BLOCKS, 0.6f, 1.0f);
            } else if (tool == util.TOOL.SAW) {
                switch (pair2.value()) {
                    case DEBARKED_HALF -> util.spawnDrops(level, pos, new ItemStack(TFCItems.SUPPORTS.get(pair2.key()).get(), 4));
                    case DEBARKED_QUARTER -> util.spawnDrops(level, pos, new ItemStack(TFCItems.LUMBER.get(pair2.key()).get(), 2));
                    default -> {
                        return InteractionResult.PASS;
                    }
                }
                util.spawnDrops(level, pos, new ItemStack(ModItems.SAWDUST.get(), 1));
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                level.playSound(player, pos, ModSounds.LOG_SAWED.get(), SoundSource.BLOCKS, 0.6f, 1.0f);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }).orElse(InteractionResult.PASS));
    }
}
